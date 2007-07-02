package at.cn.p2p.support;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;
import at.cn.p2p.client.FileTransfer;

public class SharedFiles {
	
	protected Log log = LogFactory.getLog(FileTransfer.class);	
	
	private String sharedFolder;
	private List<File> sharedFiles = new ArrayList<File>();
	private Map<File, Integer> sharedFilesSize = new HashMap<File, Integer>(); 
	
	public SharedFiles(String sharedFolder) {
		this.sharedFolder = sharedFolder;
		sharedFiles = this.getFileListing(new File(sharedFolder));
		buildSizeMap();
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
	
	public void refresh() {
		sharedFiles = this.getFileListing(new File(sharedFolder));
	}
	
	public int getSizeFromFile(File file) {
		log.info(file + " " + sharedFilesSize.get(file));
		return sharedFilesSize.get(file);
	}
	
	public void printSharedFiles() {
		for (File f : sharedFiles)
			System.out.println(f.getName());
	}
	
	private void buildSizeMap() {
		for (File file : sharedFiles) {
            try {
				FileInputStream fileInputStream = new FileInputStream(file);
				int size = 0;
				byte[] buffer = new byte[Util.BUFFER_LENGTH];
				while ((fileInputStream.read(buffer)) > 0) {
					size++;
				}
				sharedFilesSize.put(file, size);
				fileInputStream.close();
			}
            catch (Exception e) {
				log.error(e);
			}	
		}
	}
}
