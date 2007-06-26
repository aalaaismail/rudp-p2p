package at.cn.p2p;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.server.FileTransfer;

public class TestFileTransfer {
	
	protected static Log log = LogFactory.getLog(TestFileTransfer.class);

	public static void main(String[] args) {
	    
	    try {	    	
			FileTransfer fileTransferServer = new FileTransfer(
					Util.getFileTransferPort(), 
					Util.getSharedFolder());
			
			fileTransferServer.start();
			
			Thread.sleep(500);				
			
			at.cn.p2p.client.FileTransfer fileTransferClient = new at.cn.p2p.client.FileTransfer(
					new URI("p2p://127.0.0.1:" + Util.getFileTransferPort()),
					Util.getDownloadFolder(),
					new File(Util.getSharedFolder() + "/spring-src.zip"));	
			
			fileTransferClient.start();
	    }
		catch (InterruptedException e) {
	    	log.error(e);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
