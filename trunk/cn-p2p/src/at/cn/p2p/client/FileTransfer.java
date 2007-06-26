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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileTransfer extends Thread {
	
	protected Log log = LogFactory.getLog(FileTransfer.class);
	
	private URI uri;
	private Socket socket;
	private String downFolder;
	private File file;
	
	public FileTransfer(URI uri, String downFolder, File file) {
		log.info("constructing new daemon thread..");
		this.uri = uri;
		this.downFolder = downFolder;
		this.file = file;
	}
	
	public void run() {		
		try {
			log.info("creating socket");
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