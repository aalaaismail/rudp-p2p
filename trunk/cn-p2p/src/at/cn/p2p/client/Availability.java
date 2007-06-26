package at.cn.p2p.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;

public class Availability extends Thread {
	
	protected Log log = LogFactory.getLog(Availability.class);
	
	private URI uri;
	private Socket socket;
	private String status;
	private Vector<URI> hostList;
	
	public Availability(URI uri, String status) {
		log.info("constructing new daemon thread..");
		this.uri = uri;
		this.status = status;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {		
		try {
			log.info("creating socket");
			socket = new Socket(uri.getHost(), uri.getPort());
		
			log.info("getObjectOutput");
			OutputStream outputStream = socket.getOutputStream();
			ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);
		
			log.info("write status to objectOutputStream");
			objectOutput.writeObject(this.status);

        	log.info("get streams");
	        InputStream inputStream = socket.getInputStream();
			ObjectInputStream objectInput = new ObjectInputStream(inputStream);
			
			if (status.equals("on")) {
		        log.info("receiving results..");
		        hostList = (Vector) objectInput.readObject();	        
		        Util.printHosts(hostList);
		        log.info("receiving results done");
			}
	        
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

	public Vector<URI> getHostList() {
		return hostList;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
