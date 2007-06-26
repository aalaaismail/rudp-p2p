package at.cn.p2p;

import java.net.URI;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.server.Availability;
import at.cn.p2p.server.FileSearch;
import at.cn.p2p.server.FileTransfer;
import at.cn.p2p.server.Hostlist;

public class ServerInit {
	
	protected static Log log = LogFactory.getLog(ServerInit.class);
	
	private Availability availabilityServer;
	private FileSearch fileSearchServer;
	private FileTransfer fileTransferServer;
	private at.cn.p2p.client.Availability availabilityClient;
	private URI uri;
	private Hostlist hostlist;
	
	public ServerInit(URI uri) {
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
	}
	
	public void start() {
		log.info("STARTING SERVER THREADS");
		availabilityServer.start();
		fileSearchServer.start();
		fileTransferServer.start();
		
		if (uri.getPort() != Util.getOnOffPort()) {
			availabilityClient = new at.cn.p2p.client.Availability(uri, "on");
			availabilityClient.start();
		}
		else {
			hostlist.add(uri);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void stop() {
		availabilityServer.stop();
		fileSearchServer.stop();
		fileTransferServer.stop();				
		log.info("SERVER THREADS STOPED");
	}
	
	public Vector<URI> getHostlist() {
		return hostlist.getHostList();
	}
}
