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
package org.w3c.dom.html2;

/**
 * Client-side image map area definition. See the AREA element definition in 
 * HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLAreaElement extends HTMLElement {
    /**
     * A single character access key to give access to the form control. See 
     * the accesskey attribute definition in HTML 4.01.
     */
    public String getAccessKey();
    /**
     * A single character access key to give access to the form control. See 
     * the accesskey attribute definition in HTML 4.01.
     */
    public void setAccessKey(String accessKey);

    /**
     * Alternate text for user agents not rendering the normal content of this 
     * element. See the alt attribute definition in HTML 4.01.
     */
    public String getAlt();
    /**
     * Alternate text for user agents not rendering the normal content of this 
     * element. See the alt attribute definition in HTML 4.01.
     */
    public void setAlt(String alt);

    /**
     * Comma-separated list of lengths, defining an active region geometry. 
     * See also <code>shape</code> for the shape of the region. See the 
     * coords attribute definition in HTML 4.01.
     */
    public String getCoords();
    /**
     * Comma-separated list of lengths, defining an active region geometry. 
     * See also <code>shape</code> for the shape of the region. See the 
     * coords attribute definition in HTML 4.01.
     */
    public void setCoords(String coords);

    /**
     * The URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] of the linked resource. See the href attribute definition in 
     * HTML 4.01.
     */
    public String getHref();
    /**
     * The URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] of the linked resource. See the href attribute definition in 
     * HTML 4.01.
     */
    public void setHref(String href);

    /**
     * Specifies that this area is inactive, i.e., has no associated action. 
     * See the nohref attribute definition in HTML 4.01.
     */
    public boolean getNoHref();
    /**
     * Specifies that this area is inactive, i.e., has no associated action. 
     * See the nohref attribute definition in HTML 4.01.
     */
    public void setNoHref(boolean noHref);

    /**
     * The shape of the active area. The coordinates are given by 
     * <code>coords</code>. See the shape attribute definition in HTML 4.01.
     */
    public String getShape();
    /**
     * The shape of the active area. The coordinates are given by 
     * <code>coords</code>. See the shape attribute definition in HTML 4.01.
     */
    public void setShape(String shape);

    /**
     * Index that represents the element's position in the tabbing order. See 
     * the tabindex attribute definition in HTML 4.01.
     */
    public int getTabIndex();
    /**
     * Index that represents the element's position in the tabbing order. See 
     * the tabindex attribute definition in HTML 4.01.
     */
    public void setTabIndex(int tabIndex);

    /**
     * Frame to render the resource in. See the target attribute definition in 
     * HTML 4.01.
     */
    public String getTarget();
    /**
     * Frame to render the resource in. See the target attribute definition in 
     * HTML 4.01.
     */
    public void setTarget(String target);

}
