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
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;
import at.cn.p2p.support.SearchResult;

public class FileSearch extends Thread {
	
	protected Log log = LogFactory.getLog(FileSearch.class);
	
	private URI uri;
	private Socket socket;
	private String searchString;
	private volatile SearchResult searchResult;
	
	public FileSearch(URI uri, String searchString) {
		log.info("constructing new FileSearch client thread..");
		this.uri = uri;
		this.searchString = searchString;
		searchResult = new SearchResult();
	}
	
	@SuppressWarnings("unchecked")
	public void run() {		
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
	        List<File> searchResults = Util.transform((Vector) objectInput.readObject());	        
	        Util.printFiles(searchResults);
	        log.info("receiving results done");
	        
	        log.info("adding result to SearchResult..");
	        searchResult.add(searchResults, uri);
	        
	        objectInput.close();
	        objectOutput.close();
	        outputStream.close();
	        inputStream.close();
	        socket.close();
        } 
		catch (ClassNotFoundException cnfe) {
			log.error(cnfe);
		}
		catch (UnknownHostException uhe) {
			log.error(uhe);
		}
		catch (IOException ioe) {
			log.error(ioe);
		}
	}
}
