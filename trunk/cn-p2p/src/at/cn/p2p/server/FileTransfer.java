package at.cn.p2p.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;
import at.cn.p2p.support.SharedFiles;

public class FileTransfer extends Thread {
	
	protected Log log = LogFactory.getLog(FileTransfer.class);
	
	private ServerSocket serverSocket;
	private int port;
	private SharedFiles sharedFiles;
	
	public FileTransfer(int port, String sharedFolder) {
		log.info("constructing new FileTransfer daemon thread on port " + port);
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

				log.info("read filename..");
				File file = (File) objectInput.readObject();
				
				log.info("read partition infos..");
				String partitionInfo = (String) objectInput.readObject();
				String[] partitionInfos = StringUtils.split(partitionInfo);
				int begin = Integer.parseInt(partitionInfos[0]);
				int end = Integer.parseInt(partitionInfos[1]);
				
				log.info("get inputstream of file " + file.getName());				
				DataInputStream di = new DataInputStream (new BufferedInputStream(new FileInputStream(file)));				
				
				log.info("sending file..");
				int size = sharedFiles.getSizeFromFile(file);
				log.info("size:  " + size);
				log.info("begin: " + begin);
				log.info("end:   " + end);
				byte[] buffer = new byte[Util.BUFFER_LENGTH];
                int len = 0;
                int i = 0;
                while ((len = di.read(buffer)) > 0) {
                	if ((i >= begin) && (i <= end)) {
                		outputStream.write(buffer, 0, len);
                		if ((len != Util.BUFFER_LENGTH) && !(i == end)) {
                			log.error("buffer sizes are not the same: " + len + " - " + Util.BUFFER_LENGTH);
                		}
                	}
                	i++;
                }
        		outputStream.flush();
                di.close();
                log.info("sending file done..");
                
				objectInput.close();
				outputStream.close();
				inputStream.close();
				clientSocket.close();
		    } 
		    catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
		    }
		}
	}
}
