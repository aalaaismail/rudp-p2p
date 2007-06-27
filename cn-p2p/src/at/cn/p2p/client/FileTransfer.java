package at.cn.p2p.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileTransfer extends Thread {
	
	protected Log log = LogFactory.getLog(FileTransfer.class);
	
	private List<URI> uris;
	private Socket socket;
	private String downFolder;
	private File file;
	
	public FileTransfer(URI uri, String downFolder, File file) {
		log.info("constructing new FileTransfer client thread..");
		this.uris = new ArrayList<URI>();
		this.uris.add(uri);
		this.downFolder = downFolder;
		this.file = file;
	}
	
	public FileTransfer(List<URI> uri, String downFolder, File file) {
		log.info("constructing new FileTransfer client thread..");
		this.uris = uri;
		this.downFolder = downFolder;
		this.file = file;
	}
	
	public void run() {
		for (URI uri : this.uris) {	
			try {
				log.info("creating socket for host " + uri);
				socket = new Socket(uri.getHost(), uri.getPort());
			
				log.info("getObjectOutput");
				OutputStream o = socket.getOutputStream();
				ObjectOutputStream objectOutput = new ObjectOutputStream(o);
			
				log.info("write fileName to objectOutputStream");
				objectOutput.writeObject(file);
	
	        	log.info("get streams");
	        	File downloadedFile = new File(downFolder + "/" + file.getName()); 
	        	OutputStream outputStream = new FileOutputStream(downloadedFile);
		        InputStream inputStream = socket.getInputStream();
		
		        log.info("receiving file..");
		        byte[] buffer = new byte[16384];
		        int len = 0;
		        while ((len = inputStream.read(buffer)) > 0) {
		            outputStream.write(buffer, 0, len);
		        }
		        log.info("receiving file done");		        
		        
		        outputStream.close();
		        inputStream.close();
		        socket.close();
	        } 
	        catch (FileNotFoundException e) {
	        	log.error(e);
			}
			catch (UnknownHostException uhe) {
				log.error(uhe);
			}
			catch (IOException ioe) {
				log.error(ioe);
			}
		}
	}
}
