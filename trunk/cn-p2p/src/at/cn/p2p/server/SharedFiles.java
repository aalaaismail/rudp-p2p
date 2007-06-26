package at.cn.p2p.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.client.FileTransfer;

public class SharedFiles {
	
	protected Log log = LogFactory.getLog(FileTransfer.class);	
	
	private String sharedFolder;
	List<File> sharedFiles = new ArrayList<File>(); 	
	
	public SharedFiles(String sharedFolder) {
		this.sharedFolder = sharedFolder;
		sharedFiles = this.getFileListing(new File(sharedFolder));
	}
	
	@SuppressWarnings("unchecked")
	private List<File> getFileListing(File startingDir) {		
		List<File> result = new ArrayList<File>();
	    File[] filesAndDirs = startingDir.listFiles();
	    List filesDirs = Arrays.asList(filesAndDirs);
	    Iterator filesIter = filesDirs.iterator();
	    File file = null;
	    while ( filesIter.hasNext() ) {
	      file = (File)filesIter.next();
	      result.add(file); //always add, even if directory
	      if (!file.isFile()) {
	        //must be a directory
	        //recursive call!
	        List deeperList = getFileListing(file);
	        result.addAll(deeperList);
	      }

	    }
	    Collections.sort(result);
	    return result;
	}
	
	public void refreshSharedFiles() {
		sharedFiles = this.getFileListing(new File(sharedFolder));
	}
	
	public void printSharedFiles() {
		for (File f : sharedFiles)
			System.out.println(f.getName());
	}
	
	public List<File> search(String pattern) {		
		List<File> result = new ArrayList<File>();
		for (File file : sharedFiles) {
			if (file.isFile()) {
				if (StringUtils.containsIgnoreCase(file.getName(), pattern)) {
					result.add(file);
				}
			}
		}
		return result;
	}
}
