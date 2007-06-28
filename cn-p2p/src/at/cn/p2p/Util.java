package at.cn.p2p;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.support.SearchResult;

public class Util {
	
	private static Log log = LogFactory.getLog(Util.class);
	
	private static Properties properties;
	
	public static String getDownloadFolder() {
		return properties.getProperty("DownloadFolder");
	}

	public static String getSharedFolder() {
		return properties.getProperty("SharedFolder");
	}

	public static int getBasePort() {
		return Integer.parseInt(properties.getProperty("BasePort"));
	}
	
	public static void printFiles(List<File> files) {
		for (File file : files) {
			System.out.println(file.getName());
		}
	}
	
	public static void printHosts(Vector<URI> hosts) {
		for (URI uri : hosts)
			System.out.println(uri);
	}
	
	public static void printSearchResult(SearchResult searchResult) {
		int i = 0;		
		System.out.println("======= search results =======");
		for (File file : searchResult.getFiles()) {
			++i;
			System.out.println("(" + i +") " + file.getName() + " (" + searchResult.getHostsFromFile(file)+")");
		}
		System.out.println("==============================");
	}
	
	public static Vector<File> transform(List<File> files) {
		Vector<File> v = new Vector<File>(); 
	    for (File file : files) {
	    	v.add(file);
	    }
	    return v;
	}
	
	public static List<File> transform(Vector<File> files) {
		List<File> l = new ArrayList<File>(); 
	    for (File file : files) {
	    	l.add(file);
	    }
	    return l;
	}	
	
	public static int getUserInputInt() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int input = 0;
		try {
			input = Integer.parseInt(in.readLine());	    	
		} 
		catch (IOException ioe)	{
			log.error(ioe.toString());  
		}
		return input; 
	}
	
	public static String getUserInputString() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		try {
			input = in.readLine();	    	
		} 
		catch (IOException ioe)	{
			log.error(ioe.toString());  
		}
		return input; 
	}
	
	public static URI addIntToPort(URI uri, int i) {
		String scheme = uri.getScheme();
		String host = uri.getHost();
		int port = uri.getPort() + i;
		URI modiefiedURI = null;
		try {
			modiefiedURI = new URI(scheme + "://" + host + ":" + port);
		} 
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return modiefiedURI;
	}

	public static void loadProperties(String fileName) {
		if (properties == null) {
			try {
		    	properties = new Properties();
				/*
				 *  TODO make loading path relative
				 *  
		    	String path = TestFileTransfer.class.getResource("").getPath();
		    	File propFile = new File(path+"../../../../resources/" + fileName);
		    	log.info("loading " + propFile.getCanonicalFile());
		        //properties.load(new FileInputStream(propFile.getCanonicalFile()));
		    	*/
				log.info("loading " + fileName);
		    	properties.load(new FileInputStream("C:/Dokumente und Einstellungen/el torro/Eigene Dateien/ASE2778/eclipsews/cn-p2p/classes/" + fileName));
			}
			catch (Exception e) {
				log.error("error loading properties: " + e);
			}
		}
	}
}
