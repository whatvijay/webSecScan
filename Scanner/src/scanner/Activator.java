package scanner;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.impl.scanner.Scan;
import com.subgraph.vega.impl.scanner.Scanner;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Hello Scanner!!");
		//Scanner scanner = new Scanner();
		//IScan sc =scanner.createScan();
		//sc.startScan();
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye Scanner!!");
	}

}
