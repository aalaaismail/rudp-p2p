package at.cn.p2p.client;

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
import at.cn.p2p.server.Hostlist;

public class Availability extends Thread {
	
	protected Log log = LogFactory.getLog(Availability.class);
	
	private volatile List<URI> uris;
	private Socket socket;
	private String status;
	private volatile Vector<URI> hostList;
	
	public Availability(URI uri, String status) {
		log.info("constructing new Availability client thread..");
		this.uris = new ArrayList<URI>();
		this.uris.add(uri);
		this.status = status;
	}
	
	public Availability(List<URI> uri, String status) {
		log.info("constructing new Availability client thread..");
		this.uris = uri;
		this.status = status;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {	
		for (URI uri : this.uris) {	
			try {
				log.info("creating socket for host " + uri);
				socket = new Socket(uri.getHost(), uri.getPort());
			
				log.info("get output stream");
				OutputStream outputStream = socket.getOutputStream();
				ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);
	
				log.info("get input stream");
		        InputStream inputStream = socket.getInputStream();
				ObjectInputStream objectInput = new ObjectInputStream(inputStream);
			
				log.info("write status to objectOutputStream");
				objectOutput.writeObject(this.status+" "+Util.getOnOffPort());
				
				if (status.equals("on")) {
			        log.info("receiving results..");
			        hostList = (Vector) objectInput.readObject();	        
			        Util.printHosts(hostList);
			        log.info("receiving results done");
			        
			        this.uris.addAll(hostList);
			        new Hostlist().add(hostList);
				}
				else if (status.equals("off")) {
					log.info("write hostlist to objectOutputStream");
					objectOutput.writeObject(new Hostlist().getAllHosts());
				}
				else {
					log.error("invalid status: " + status);
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
	}

	public Vector<URI> getHostList() {
		return hostList;
	}
}
