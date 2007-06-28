package at.cn.p2p.test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;
import at.cn.p2p.server.FileTransfer;

public class TestFileTransfer {
	
	protected static Log log = LogFactory.getLog(TestFileTransfer.class);

	public static void main(String[] args) {
		Util.loadProperties("p2p_1.properties");
	    
	    try {	    	
			FileTransfer fileTransferServer = new FileTransfer(
					Util.getBasePort() + 2, 
					Util.getSharedFolder());
			
			fileTransferServer.start();
			
			Thread.sleep(500);				
			
			at.cn.p2p.client.FileTransfer fileTransferClient = new at.cn.p2p.client.FileTransfer(
					new URI("p2p://127.0.0.1:" + (Util.getBasePort() + 2)),
					Util.getDownloadFolder(),
					new File(Util.getSharedFolder() + "/spring-src.zip"));	
			
			fileTransferClient.start();
			
			fileTransferClient.join();
			
			System.out.println("Hit Strg-C to exit finished Test");
	    }
		catch (InterruptedException e) {
	    	log.error(e);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
