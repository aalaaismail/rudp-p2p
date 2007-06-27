package at.cn.p2p.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;

public class FileSearch extends Thread {
	
	protected Log log = LogFactory.getLog(FileSearch.class);
	
	private List<URI> uris;
	private Socket socket;
	private String searchString;
	private volatile List<File> searchResults;
	
	public FileSearch(URI uri, String searchString) {
		log.info("constructing new FileSearch client thread..");
		this.uris = new ArrayList<URI>();
		this.uris.add(uri);
		this.searchString = searchString;
	}
	
	public FileSearch(List<URI> uri, String searchString) {
		log.info("constructing new FileSearch client thread..");
		this.uris = uri;
		this.searchString = searchString;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {		
		for (URI uri : this.uris) {	
			try {
				log.info("creating socket for host " + uri);
				socket = new Socket(uri.getHost(), uri.getPort() + 1);
			
				log.info("getObjectOutput");
				OutputStream outputStream = socket.getOutputStream();
				ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);
			
				log.info("write fileName to objectOutputStream");
				objectOutput.writeObject(this.searchString);
	
	        	log.info("get streams");
		        InputStream inputStream = socket.getInputStream();
				ObjectInputStream objectInput = new ObjectInputStream(inputStream);
		
		        log.info("receiving results..");
		        searchResults = Util.transform((Vector) objectInput.readObject());	        
		        Util.printFiles(searchResults);
		        log.info("receiving results done");
		        
		        objectInput.close();
		        objectOutput.close();
		        outputStream.close();
		        inputStream.close();
		        socket.close();
	        } 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch (UnknownHostException uhe) {
				log.error(uhe);
			}
			catch (IOException ioe) {
				log.error(ioe);
			}
		}
	}

	public List<File> getSearchResults() {
		return searchResults;
	}
}
