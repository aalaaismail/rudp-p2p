package at.cn.p2p.test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;
import at.cn.p2p.server.FileSearch;
import at.cn.p2p.support.SearchResult;
import at.cn.p2p.support.SharedFiles;

public class TestFileSearch {
	
	protected static Log log = LogFactory.getLog(TestFileSearch.class);
	
	public static void main(String[] args) {
		Util.loadProperties("p2p_1.properties");
		
		/*
		 * test "local" file search
		 */
		SharedFiles sharedFiles = new SharedFiles(Util.getSharedFolder());
		sharedFiles.printSharedFiles();		
		System.out.println("======= LOCAL search results =======");
		Util.printFiles(sharedFiles.search("Spring"));		

	    /*
	     * test file search threads (server and client)
	     */
	    try {	    	
			FileSearch fileSearchServer = new FileSearch(
					Util.getSearchPort(), 
					Util.getSharedFolder());
			
			fileSearchServer.start();
			
			Thread.sleep(500);				
			
			at.cn.p2p.client.FileSearch fileSearchClient = new at.cn.p2p.client.FileSearch(
					new URI("p2p://127.0.0.1:" + Util.getSearchPort()),
					"spring");	
			
			fileSearchClient.start();
			
			fileSearchClient.join();
			
			System.out.println("======= REMOTE search results =======");
			Util.printSearchResult(new SearchResult());						
	    }
		catch (InterruptedException e) {
	    	log.error(e);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}		
	}
}
