/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestTask;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.RequestEngineException;
import com.subgraph.vega.http.requests.custom.VegaHttpEntityEnclosingUriRequest;
import com.subgraph.vega.http.requests.custom.VegaHttpUriRequest;

public class ProxyRequestHandler implements HttpRequestHandler {

	/**
	 * Hop-by-hop headers to be removed by this proxy.
	 */
	private final static String[] HOP_BY_HOP_HEADERS = {
		// Hop-by-hop headers specified in RFC2616 section 13.5.1.
		HTTP.CONN_DIRECTIVE, // "Connection"
		HTTP.CONN_KEEP_ALIVE, // "Keep-Alive"
		"Proxy-Authenticate",
		"Proxy-Authorization",
		"TE",
		"Trailers",
		HTTP.TRANSFER_ENCODING, // "Transfer-Encoding"
		"Upgrade",

		// Not part of the RFC but should not be forwarded; see http://homepage.ntlworld.com/jonathan.deboynepollard/FGA/web-proxy-connection-header.html
		"Proxy-Connection",
	};

	private final Logger logger;
	private final HttpProxyListener httpProxy;
	private final IHttpRequestEngine requestEngine;

	ProxyRequestHandler(HttpProxyListener httpProxy, Logger logger, IHttpRequestEngine requestEngine) {
		this.httpProxy = httpProxy;
		this.logger = logger;
		this.requestEngine = requestEngine;
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		final ProxyTransaction transaction = new ProxyTransaction(requestEngine, context);
		context.setAttribute(HttpProxyListener.PROXY_HTTP_TRANSACTION, transaction);

		try {
			if (handleRequest(transaction, request) == false) {
				response.setStatusCode(503);
				transaction.signalComplete(false);
				return;
			}

			final HttpUriRequest uriRequest = transaction.getRequest();
			final IHttpRequestTask requestTask = requestEngine.sendRequest(uriRequest);
			transaction.setRequestTask(requestTask);
			transaction.signalForward();
			IHttpResponse r = null;
			try {
				r = requestTask.get(false);
			} finally {
				transaction.setRequestTask(null);
			}
			if(r == null) {
				response.setStatusCode(503);
				transaction.signalComplete(false);
				return;
			}

			if (handleResponse(transaction, r) == false) {
				response.setStatusCode(503);
				transaction.signalComplete(true);
				return;
			}
			
			HttpResponse httpResponse = copyResponse(r.getRawResponse());
			removeHeaders(httpResponse);
			response.setStatusLine(httpResponse.getStatusLine());
			response.setHeaders(httpResponse.getAllHeaders());
			response.setEntity(httpResponse.getEntity());
			transaction.signalForward();
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, "Error processing request: " + e.getMessage(), e);
			response.setStatusCode(503);
		} catch (RequestEngineException e) {
			logger.log(Level.WARNING, "Error processing request: " + e.getMessage());
			response.setStatusCode(502);
		} catch (ProtocolException e) {
			logger.log(Level.WARNING, "Error processing request: " + e.getMessage(), e);
			response.setStatusCode(400);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error processing request: " + e.getMessage(), e);
			response.setStatusCode(500);
		} finally {
			transaction.signalComplete(false);
		}
	}

	private HttpEntity copyEntity(HttpEntity entity) {
		try {
			if(entity == null) {
				return null;
			}
			final ByteArrayEntity newEntity = new ByteArrayEntity(EntityUtils.toByteArray(entity));
			newEntity.setContentEncoding(entity.getContentEncoding());
			newEntity.setContentType(entity.getContentType());
			return newEntity;
		} catch (IOException e) {
			return null;
		}
	}

	private HttpUriRequest copyToUriRequest(HttpRequest request) throws ProtocolException {
		URI uri;
		final String hostStr = removePath(request.getRequestLine().getUri());
		try {
			uri = new URI(hostStr);
		} catch (URISyntaxException e) {
    		throw new ProtocolException("Invalid URI: " + hostStr, e);
		}
		// ensuring we have scheme and host also prevents the proxy from connecting back to itself
		if (uri.getScheme() == null) {
			throw new ProtocolException("No scheme in proxy request URI");
		}
		if (uri.getHost() == null) {
			throw new ProtocolException("No host in proxy request URI");
		}
		
		final HttpHost host = URIUtils.extractHost(uri);
		final HttpUriRequest uriRequest = createRequestCopy(host, request);
		uriRequest.setParams(request.getParams());
		uriRequest.setHeaders(request.getAllHeaders());
		return uriRequest;
	}
	
	private String removeSchemeAndHost(String line) {
		final int idx = findPathStart(line);
		if(idx == -1) {
			return "";
		} else {
			return line.substring(idx);
		}
	}
	
	private String removePath(String line) {
		final int idx = findPathStart(line);
		if(idx == -1) {
			return line;
		} else {
			return line.substring(0, idx);
		}
	}
	
	
	private int findPathStart(String line) {
		int idx = line.indexOf("://");
		if(idx == -1 || ((idx + 3) >= line.length())) {
			return 0;
		} else {
			idx += 3;
		}
		while(line.charAt(idx) == '/') idx++;
		return line.indexOf('/', idx);
	}

	private HttpUriRequest createRequestCopy(HttpHost host, HttpRequest request) {
		final String method = request.getRequestLine().getMethod();
		final String uriLine = removeSchemeAndHost(request.getRequestLine().getUri());
		if(request instanceof HttpEntityEnclosingRequest) {
			final VegaHttpEntityEnclosingUriRequest r = new VegaHttpEntityEnclosingUriRequest(host, method, uriLine);
			r.setEntity(copyEntity(((HttpEntityEnclosingRequest) request).getEntity()));
			return r;
		}
		return new VegaHttpUriRequest(host, method, uriLine);
	}
	
	private HttpResponse copyResponse(HttpResponse originalResponse) {
		HttpResponse r = new BasicHttpResponse(originalResponse.getStatusLine());
		r.setHeaders(originalResponse.getAllHeaders());
		r.setEntity(originalResponse.getEntity());
		return r;
	}

	private void removeHeaders(HttpMessage message) {
		for(String hdr: HOP_BY_HOP_HEADERS) { 
			message.removeHeaders(hdr);
		}
	}

	private boolean handleRequest(ProxyTransaction transaction, HttpRequest request) throws InterruptedException, ProtocolException {
		removeHeaders(request);
		transaction.setRequest(copyToUriRequest(request));
		if (httpProxy.handleTransaction(transaction) == true) {
			return transaction.getForward();
		} else {
			return true;
		}
	}

	private boolean handleResponse(ProxyTransaction transaction, IHttpResponse response) throws InterruptedException {
		transaction.setResponse(response);
		if (httpProxy.handleTransaction(transaction) == true) {
			return transaction.getForward();
		} else {
			return true;
		}
	}
}
