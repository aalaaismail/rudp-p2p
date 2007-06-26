package at.cn.p2p.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;

public class FileSearch extends Thread {
	
	protected Log log = LogFactory.getLog(FileSearch.class);
	
	private ServerSocket serverSocket;
	private int port;
	private SharedFiles sharedFiles;
	
	public FileSearch(int port, String sharedFolder) {
		log.info("constructing new FileSearch daemon thread on port " + port);
		this.port = port;
		this.sharedFiles = new SharedFiles(sharedFolder);
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
				ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);				

				log.info("read search string..");
				String searchString = (String) objectInput.readObject();				
				
				log.info("searching..");
				List<File> searchResult = sharedFiles.search(searchString);
				Util.printFiles(searchResult);
                log.info("searching done..");
                
                objectOutput.writeObject(Util.transform(searchResult));
                
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
