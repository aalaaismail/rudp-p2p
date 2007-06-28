package at.cn.p2p;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.server.Availability;
import at.cn.p2p.server.FileSearch;
import at.cn.p2p.server.FileTransfer;
import at.cn.p2p.support.Hostlist;
import at.cn.p2p.support.SearchResult;
import at.cn.p2p.support.SharedFiles;

public class Application {
	
	protected static Log log = LogFactory.getLog(Application.class);
	
	private Availability availabilityServer;
	private FileSearch fileSearchServer;
	private FileTransfer fileTransferServer;
	private URI uri;
	private Hostlist hostlist;
	private SearchResult searchResult;
	private SharedFiles sharedFiles;
	
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
		
		searchResult = new SearchResult();
		sharedFiles = new SharedFiles(Util.getSharedFolder());
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
		List<at.cn.p2p.client.Availability> availabilityThreads = 
			new ArrayList<at.cn.p2p.client.Availability>();
		for (URI uri : hostlist.getOtherHosts()) {
			at.cn.p2p.client.Availability availabilityClient = 
				new at.cn.p2p.client.Availability(uri, "off");
			availabilityClient.start();
			availabilityThreads.add(availabilityClient);
		}
		
		availabilityServer.stop();
		fileSearchServer.stop();
		fileTransferServer.stop();
		
		try {
			for (at.cn.p2p.client.Availability availabilityThread : availabilityThreads)
				availabilityThread.join();
		} 
		catch (InterruptedException e) {
			log.error(e);
		}
		
		log.info("SERVER THREADS STOPED");
	}
	
	public void search(String searchString) {
		List<at.cn.p2p.client.FileSearch> fileSearchThreads = 
			new ArrayList<at.cn.p2p.client.FileSearch>(); 
		for (URI uri : hostlist.getOtherHosts()) {
			at.cn.p2p.client.FileSearch fileSearchClient = 
				new at.cn.p2p.client.FileSearch(uri, searchString);		
			fileSearchClient.start();
			fileSearchThreads.add(fileSearchClient);
		}
		
		for (at.cn.p2p.client.FileSearch thread : fileSearchThreads) {
			try {
				thread.join();
			}
			catch (InterruptedException e) {
				log.error(e);
			}
		}
	}
	
	public Hostlist getHostlist() {
		return hostlist;
	}

	public SearchResult getSearchResult() {
		return searchResult;
	}

	public SharedFiles getSharedFiles() {
		sharedFiles.refresh();
		return sharedFiles;
	}
}
