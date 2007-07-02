package at.cn.p2p;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.server.Availability;
import at.cn.p2p.server.FileSearch;
import at.cn.p2p.server.FileTransfer;
import at.cn.p2p.support.Download;
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
	private volatile SharedFiles sharedFiles;
	private volatile List<Download> downloads;
	private volatile List<Downloader> downloaders;
	
	public Application(URI uri) {
		this.uri = uri;
		
		availabilityServer = new Availability(
				Util.getBasePort());
		
		fileSearchServer = new FileSearch(
				Util.getBasePort() + 1, 
				Util.getSharedFolder());
		
		fileTransferServer = new FileTransfer(
				Util.getBasePort() + 2, 
				Util.getSharedFolder());
		
		hostlist = new Hostlist();
		hostlist.add(uri);
		
		searchResult = new SearchResult();
		sharedFiles = new SharedFiles(Util.getSharedFolder());
		downloads = new ArrayList<Download>();
		downloaders = new ArrayList<Downloader>();
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
		this.searchResult = new SearchResult();
		
		List<at.cn.p2p.client.FileSearch> fileSearchThreads = 
			new ArrayList<at.cn.p2p.client.FileSearch>(); 
		for (URI uri : hostlist.getOtherHosts()) {
			at.cn.p2p.client.FileSearch fileSearchClient = 
				new at.cn.p2p.client.FileSearch(Util.addIntToPort(uri, 1), searchString);		
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
	
	public void download(File file, Vector<URI> hosts) {
		Download download = new Download(
				file,
				new File(Util.getDownloadFolder() + "/" + file.getName()),
				hosts,
				sharedFiles.getSizeFromFile(file));	
		downloads.add(download);
		
		Downloader downloader = new Downloader(download);
		downloaders.add(downloader);
		downloader.start();
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

	public List<Download> getDownloads() {
		return downloads;
	}

	public void resumeDownload() {
		for (Download download : downloads) {
			//if (download.getDownloadStatus() != 100.0f) {
				Downloader downloader = new Downloader(download);
				downloaders.add(downloader);
				downloader.start();
			//}
		}
	}

	public void suspendDownload() {
		for (Downloader downloader : downloaders)
			downloader.suspendDownload();
	}
	
	class Downloader extends Thread {
		
		private volatile Download download;
		private volatile boolean downloadIsActive = true;
		
		public Downloader(Download download) {
			log.info("constructing downloader thread..");
			log.info("starting/resuming status: " + download.getDownloadStatus());
			this.download = download;
		}
		
		public void run() {
			List<URI> hosts = download.getHosts();
			
			// TODO: obatin size not from local file
			// maybe create: class File extends java.io.File
			// with object field size

			Map<Integer, Boolean> map = download.getDownloadMap();
			for (int i : map.keySet()) {
				if (downloadIsActive && !map.get(i)) {
					URI host = hosts.get(i%hosts.size());
					at.cn.p2p.client.FileTransfer fileTransferClient = new at.cn.p2p.client.FileTransfer(
							Util.addIntToPort(host, 1),
							download,
							i,
							i+1);
					fileTransferClient.start();
					
					try {
						//fileTransferClient.join();
						Thread.sleep(100);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
				
			if (download.getDownloadStatus() == 100.0f)
				download.writeToDisk();
		}
		
		public void suspendDownload() {
			this.downloadIsActive = false;
		}
	}
}
