package at.cn.p2p.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
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

import at.cn.p2p.Util;
import at.cn.p2p.support.Download;

public class FileTransfer extends Thread {
	
	protected Log log = LogFactory.getLog(FileTransfer.class);
	
	private URI uri;
	private Socket socket;
	private int begin = 0;
	private int end = 0;
	private Download download;
	
	public FileTransfer(URI uri, Download download, 
			int begin, int end) {
		log.info("constructing new FileTransfer client thread..");
		this.uri = uri;
		this.download = download;
		this.begin = begin;
		this.end = end;
	}
	
	public void run() {	
		try {
			log.info("creating socket for host " + uri);
			socket = new Socket(uri.getHost(), uri.getPort());
		
			log.info("getObjectOutput");
			OutputStream o = socket.getOutputStream();
			ObjectOutputStream objectOutput = new ObjectOutputStream(o);
		
			log.info("write fileName to objectOutputStream");
			objectOutput.writeObject(download.getRemoteFile());
		
			log.info("write partition Infos to objectOutputStream");
			objectOutput.writeObject(begin + " " + end);
			objectOutput.flush();

        	log.info("get streams");
        	OutputStream outputStream = new FileOutputStream(download.getLocalFile());
	        InputStream inputStream = socket.getInputStream();
	        DataInputStream di = new DataInputStream (new BufferedInputStream(inputStream));
	        
	        log.info("receiving file..");
	        byte[] buffer = new byte[Util.BUFFER_LENGTH];
	        int i = 0;
	        int len = 0;
	        while ((len = di.read(buffer)) > 0) {
        		if ((len != Util.BUFFER_LENGTH) && !(i == end)) {
        			log.error("buffer sizes are not the same: " + len + " - " + Util.BUFFER_LENGTH);
        		}
	        	download.addFragmentToStream(buffer, begin + i++, len);
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
