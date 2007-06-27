package at.cn.p2p;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.server.Availability;
import at.cn.p2p.server.FileSearch;
import at.cn.p2p.server.FileTransfer;
import at.cn.p2p.server.Hostlist;

public class Application {
	
	protected static Log log = LogFactory.getLog(Application.class);
	
	private Availability availabilityServer;
	private FileSearch fileSearchServer;
	private FileTransfer fileTransferServer;
	private URI uri;
	private Hostlist hostlist;
	
	public Application(URI uri) {
		this.uri = uri;
		
		availabilityServer = new Availability(
				Util.getOnOffPort());
		
		fileSearchServer = new FileSearch(
				Util.getSearchPort(), 
				Util.getSharedFolder());
		
		fileTransferServer = new FileTransfer(
				Util.getFileTransferPort(), 
				Util.getSharedFolder());
		
		hostlist = new Hostlist();
		hostlist.add(uri);
	}
	
	public void start() {
		log.info("STARTING SERVER THREADS");
		availabilityServer.start();
		fileSearchServer.start();
		fileTransferServer.start();
		
		// wait for server threads to start
		try {
			Thread.sleep(500);
		} 
		catch (InterruptedException e) {
			log.error(e);
		}
		
		// make ourself available
		if (!hostlist.isLocalUri(uri))
			new at.cn.p2p.client.Availability(uri, "on").start();
	}
	
	@SuppressWarnings("deprecation")
	public void stop() {
		at.cn.p2p.client.Availability availabilityClient = 
			new at.cn.p2p.client.Availability(hostlist.getOtherHosts(), "off");
		availabilityClient.start();
		
		availabilityServer.stop();
		fileSearchServer.stop();
		fileTransferServer.stop();
		
		try {
			availabilityClient.join();
		} 
		catch (InterruptedException e) {
			log.error(e);
		}
		
		log.info("SERVER THREADS STOPED");
	}
	
	public void search(String searchString) {
		at.cn.p2p.client.FileSearch fileSearchClient = new at.cn.p2p.client.FileSearch(
				hostlist.getOtherHosts(), searchString);		
		fileSearchClient.start();
		try {
			fileSearchClient.join();
			
			System.out.println("------- search results -------");
			Util.printFiles(fileSearchClient.getSearchResults());
		}
		catch (InterruptedException e) {
			log.error(e);
		}
	}
	
	public Hostlist getHostlist() {
		return hostlist;
	}
}
