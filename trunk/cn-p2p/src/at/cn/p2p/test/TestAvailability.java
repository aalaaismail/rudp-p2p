package at.cn.p2p.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;
import at.cn.p2p.server.Availability;
import at.cn.p2p.support.Hostlist;

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
			Availability availabilityServer = new Availability(Util.getBasePort());
			availabilityServer.start();
			
			Thread.sleep(500);				
			
			/*
			 * register some online hosts
			 */
			at.cn.p2p.client.Availability availabilityClient1 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getBasePort()),
					"on");
			availabilityClient1.start();
			Thread.sleep(500);	
			
			at.cn.p2p.client.Availability availabilityClient2 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getBasePort()),
					"on");
			availabilityClient2.start();
			Thread.sleep(500);	
			
			at.cn.p2p.client.Availability availabilityClient3 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getBasePort()),
					"on");
			availabilityClient3.start();
			Thread.sleep(1000);	
			
			/*
			 * print clients hostlists
			 */
			Hostlist hostlist = new Hostlist();
			System.out.println("------- host list -------");			
			Util.printHosts(hostlist.getAllHosts());
			
			/*
			 * unregister hosts
			 */
			Thread.sleep(500);	
			at.cn.p2p.client.Availability availabilityClient4 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getBasePort()),
					"off");
			availabilityClient4.start();
			Thread.sleep(500);
			
			at.cn.p2p.client.Availability availabilityClient5 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getBasePort()),
					"off");
			availabilityClient5.start();
			Thread.sleep(500);
			
			at.cn.p2p.client.Availability availabilityClient6 = new at.cn.p2p.client.Availability(
					new URI("p2p://127.0.0.1:" + Util.getBasePort()),
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
