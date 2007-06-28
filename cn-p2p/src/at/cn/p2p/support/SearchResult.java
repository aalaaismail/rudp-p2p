package at.cn.p2p.support;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SearchResult {
	
	private Log log = LogFactory.getLog(SearchResult.class);	

	private static volatile Map<File, Set<URI>> searchResult;
	
	public SearchResult() {
		// singleton
		if (searchResult == null) {
			searchResult = new HashMap<File, Set<URI>>();
			log.info("SearchResult created");
		}
		else {
			log.info("SearchResult was already created");
		}
	}
	
	public synchronized Set<URI> add(File file, URI uri) {
		if (searchResult.containsKey(file)) {
			Set<URI> uris = searchResult.get(file);
			uris.add(uri);
			return searchResult.put(file, uris);			
		}
		else {
			Set<URI> uris = new HashSet<URI>();
			uris.add(uri);
			return searchResult.put(file, uris);		
		}
	}
	
	public synchronized void add(List<File> files, URI uri) {
		for (File file : files)
			add(file, uri);
	}
	
	public Vector<URI> getHostsFromFile(File file) {
		Set<URI> uris = searchResult.get(file);
		Vector<URI> v = new Vector<URI>();
		v.addAll(uris);
		Collections.sort(v);
		return v;
	}

	public Vector<File> getFiles() {
		Set<File> files = searchResult.keySet();
		Vector<File> v = new Vector<File>();
		v.addAll(files);
		Collections.sort(v);
		return v;
	}

	public File getFileFromId(int id) {
		Set<File> files = searchResult.keySet();
		List<File> list = new ArrayList<File>();
		list.addAll(files);
		Collections.sort(list);
		return list.get(id - 1);
	}
}
