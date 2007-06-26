package at.cn.p2p;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Util {
	
	private static Log log = LogFactory.getLog(Util.class);
	
	private static Properties properties;
	
	public static String getDownloadFolder() {
		load();
		return properties.getProperty("DownloadFolder");
	}

	public static String getSharedFolder() {
		load();
		return properties.getProperty("SharedFolder");
	}

	public static int getFileTransferPort() {
		load();
		return Integer.parseInt(properties.getProperty("FileTransferPort"));
	}

	public static int getOnOffPort() {
		load();
		return Integer.parseInt(properties.getProperty("OnOffPort"));
	}

	public static int getSearchPort() {
		load();
		return Integer.parseInt(properties.getProperty("SearchPort"));
	}
	
	public static void printFiles(List<File> files) {
		for (File file : files)
			System.out.println(file.getName());
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

	private static void load() {
		if (properties == null) {
			try {
				/*
				 * TODO make path relative
				 */
		    	properties = new Properties();
		    	String path = TestFileTransfer.class.getResource("").getPath();
		    	File propFile = new File(path+"../../../../resources/p2p.properties");
		    	log.info("loading " + propFile.getCanonicalFile());
		        //properties.load(new FileInputStream(propFile.getCanonicalFile()));
		    	
		    	properties.load(new FileInputStream("C:/Dokumente und Einstellungen/el torro/Eigene Dateien/ASE2778/eclipsews/cnetworks/classes/p2p.properties"));
			}
			catch (Exception e) {
				log.error(e);
			}
		}
	}
}
