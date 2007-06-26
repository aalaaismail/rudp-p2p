package at.cn.p2p;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.server.Availability;

/**
 * won't work correctly, since Hostlist has become static
 */
public class TestAvailability {	
	
	protected static Log log = LogFactory.getLog(TestAvailability.class);
	
	public static void main(String[] args) {
		Util.loadProperties("p2p_1.properties");
	    try {	 
	    	/*
	    	 * start server
	    	 */
			Availability availabilityServer = new Availability(Util.getOnOffPort());
			availabilityServer.start();
			
			Thread.sleep(500);				
			
			/*
			 * register some online hosts
			 */
			at.cn.p2p.client.Availability availabilityClient1 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getOnOffPort()),
					"on");
			availabilityClient1.start();
			Thread.sleep(500);	
			
			at.cn.p2p.client.Availability availabilityClient2 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getOnOffPort()),
					"on");
			availabilityClient2.start();
			Thread.sleep(500);	
			
			at.cn.p2p.client.Availability availabilityClient3 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getOnOffPort()),
					"on");
			availabilityClient3.start();
			Thread.sleep(1000);	
			
			/*
			 * print clients hostlists
			 */
			System.out.println("------- host list 1 -------");
			Util.printHosts(availabilityClient1.getHostList());
			
			System.out.println("------- host list 2 -------");
			Util.printHosts(availabilityClient2.getHostList());
			
			System.out.println("------- host list 3 -------");
			Util.printHosts(availabilityClient3.getHostList());
			
			/*
			 * unregister hosts
			 */
			Thread.sleep(500);	
			at.cn.p2p.client.Availability availabilityClient4 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getOnOffPort()),
					"off");
			availabilityClient4.start();
			Thread.sleep(500);
			
			at.cn.p2p.client.Availability availabilityClient5 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getOnOffPort()),
					"off");
			availabilityClient5.start();
			Thread.sleep(500);
			
			at.cn.p2p.client.Availability availabilityClient6 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getOnOffPort()),
					"off");
			availabilityClient6.start();
			Thread.sleep(500);
	    }
		catch (InterruptedException e) {
	    	log.error(e);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}		
	}
}
