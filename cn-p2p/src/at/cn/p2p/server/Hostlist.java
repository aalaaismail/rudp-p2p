package at.cn.p2p.server;

import java.net.URI;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Hostlist {
	
	private Log log = LogFactory.getLog(Hostlist.class);	

	private Vector<URI> hostList = new Vector<URI>();
	
	public Hostlist() {
		// singleton
		if (hostList == null) {
			hostList = new Vector<URI>();
			log.info("Hostlist created");
		}
		else {
			log.info("Hostlist was already created");
		}
	}
	
	public synchronized boolean remove(URI uri) {
		return hostList.removeElement(uri);
	}
	
	public synchronized boolean add(URI uri) {
		return hostList.add(uri);
	}

	public Vector<URI> getHostList() {
		return hostList;
	}
}
