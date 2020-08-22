package starter;

import com.subgraph.vega.api.model.IWorkspace;
//import com.subgraph.vega.impl.scanner.Scan;
import com.subgraph.vega.impl.scanner.Scanner;
//import com.subgraph.vega.impl.scanner.ScannerTask;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Hello starter!!");
		Scanner scr = new Scanner();
		IWorkspace workspace = null;
		//Scan.createScan(scr, workspace);
		//scr.createScan().startScan();
		//Scan scan = new Scan(scr);
		//ScannerTask st = new ScannerTask(scan);
		int i=0;
		while(i<100000000)
		{
			System.out.println("i="+i);
			i++;
			Thread.sleep(100);
		}
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye World!!");
	}

}
