package at.cn.p2p.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;

public class Availability extends Thread {
	
	protected Log log = LogFactory.getLog(Availability.class);
	
	private ServerSocket serverSocket;
	private int port;
	private Hostlist hostlist = new Hostlist();
	
	public Availability(int port) {
		log.info("constructing new Availability daemon thread on port " + port);
		this.port = port;
	}
	
	public void run() {
		try {
			log.info("creating a server socket to accept connections..");
			
			serverSocket = new ServerSocket(port);
		}
		catch (Exception e) {
			log.error("Problem establishing server socket: " + e);
			return;
		}

		try {
			while (true) {
		        try {
		    		log.info("wait for client to connect...");  		        	
		        	Socket client = serverSocket.accept();
		        	
		        	log.info("handling accepted client socket");    
		    		new Handler(client).start();
		        }
		        catch (IOException ioe) {
		        	log.error("Problem accepting client's socket connection: " + ioe);
		        }
			}
		}
		catch (ThreadDeath e) {
			// When the thread is killed, close the server socket
			try {
				serverSocket.close();
			}
			catch (IOException ioe) {
				log.error("Problem closing server socket: " + ioe);
			}
		}
	}

	class Handler extends Thread {
	
		private Socket clientSocket;
		
		public Handler(Socket clientSocket) {
			log.info("constructing new handler thread..");
			this.clientSocket = clientSocket;
		}
		
		public void run() {
			try {			
				log.info("get inputstream..");
				InputStream inputStream = clientSocket.getInputStream();
				ObjectInput objectInput = new ObjectInputStream(inputStream);
	
				log.info("get outputstream..");
				OutputStream outputStream = clientSocket.getOutputStream();	
				ObjectOutput objectOutput = new ObjectOutputStream(outputStream);

				log.info("read status and uris..");
				String status = (String) objectInput.readObject();
				String[] statusAndUris = StringUtils.split(status);
				status = statusAndUris[0];
				int availPort = Integer.parseInt(statusAndUris[1]);

				InetAddress inetAddress = clientSocket.getInetAddress();
				URI uri = new URI("p2p://"+inetAddress.getHostAddress()+":"+availPort);
				
				if (status.equals("on")) {
					log.info("online host: " + uri);
					
					objectOutput.writeObject(hostlist.getHostList());
					
					hostlist.add(uri);
				}
				else if (status.equals("off")) {
					log.info("offline host: " + uri);
					
					hostlist.remove(uri);					
				}
				else {
					throw new Exception("invalid status: " + status);
				}
				
				System.out.println("--- server-stored hostlist ---");
				Util.printHosts(hostlist.getHostList());
				System.out.println("------------------------------");
				
				objectOutput.flush();
				
				objectOutput.close();
				objectInput.close();
				outputStream.close();
				inputStream.close();
				clientSocket.close();
		    } 
		    catch (Exception e) {
				log.error(e.getMessage());
		    }
		}
	}
}
