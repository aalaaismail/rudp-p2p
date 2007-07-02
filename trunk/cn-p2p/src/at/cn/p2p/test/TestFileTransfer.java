package at.cn.p2p.test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;
import at.cn.p2p.server.FileTransfer;
import at.cn.p2p.support.Download;

public class TestFileTransfer {
	
	private static Log log = LogFactory.getLog(TestFileTransfer.class);
	
	private static volatile Download download;

	public static void main(String[] args) {
		Util.loadProperties("p2p_1.properties");
	    
	    try {	    		
	    	/*
	    	 * start three filetransfer servers
	    	 */
			FileTransfer fileTransferServer1 = new FileTransfer(
					Util.getBasePort(), 
					Util.getSharedFolder());	
			FileTransfer fileTransferServer2 = new FileTransfer(
					Util.getBasePort() + 1, 
					Util.getSharedFolder());
			FileTransfer fileTransferServer3 = new FileTransfer(
					Util.getBasePort() + 2, 
					Util.getSharedFolder());
			fileTransferServer1.start();
			fileTransferServer2.start();
			fileTransferServer3.start();
			Thread.sleep(1000); // wait for 'em to come up
			
			/*
			 * build a download object
			 */
			File remoteFile = new File(Util.getSharedFolder() + "/spring-src.zip");
			File localFile = new File(Util.getDownloadFolder() + "/spring-src.zip");
			URI host = new URI("p2p://127.0.0.1:" + Util.getBasePort());
			List<URI> hosts = new ArrayList<URI>();
			hosts.add(host); // feauture not needed here
			download = new Download(remoteFile, localFile, hosts, 211);			
			
			/*
			 * start three transfer clients to get the specified download
			 * portion by portion from the three different servers
			 */
			at.cn.p2p.client.FileTransfer fileTransferClient1 = new at.cn.p2p.client.FileTransfer(
					host,
					download,
					0,
					70);
			fileTransferClient1.start();		
			
			at.cn.p2p.client.FileTransfer fileTransferClient2 = new at.cn.p2p.client.FileTransfer(
					Util.addIntToPort(host, 1),
					download,
					71,
					150);
			fileTransferClient2.start();		
			
			at.cn.p2p.client.FileTransfer fileTransferClient3 = new at.cn.p2p.client.FileTransfer(
					Util.addIntToPort(host, 2),
					download,
					141,
					210);
			fileTransferClient3.start();
			
			// wait till download is finished
			fileTransferClient1.join();
			fileTransferClient2.join();
			fileTransferClient3.join();	
			
			download.writeToDisk();
			
			log.info("Hit Strg-C to exit finished Test");
	    }
		catch (InterruptedException e) {
	    	log.error(e);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
