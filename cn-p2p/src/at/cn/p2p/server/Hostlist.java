package at.cn.p2p.server;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.cn.p2p.Util;

public class Hostlist {
	
	private Log log = LogFactory.getLog(Hostlist.class);	

	private static Set<URI> hostList;
	private static volatile Set<URI> localUris;
	
	public Hostlist() {
		// singleton
		if (hostList == null) {
			hostList = new HashSet<URI>();
			log.info("Hostlist created");
			
			localUris = new HashSet<URI>();
			try {
				localUris.add(new URI(
						"p2p://"+InetAddress.getLocalHost().getHostAddress()+":"+
						Util.getOnOffPort()));
				localUris.add(new URI("p2p://127.0.0.1:"+Util.getOnOffPort()));
			} 
			catch (UnknownHostException e) {
				log.error(e);
			}
			catch (URISyntaxException e) {
				log.error(e);
			}
			log.info("Local URIs obtained");
		}
		else {
			log.info("Hostlist was already created");
		}
	}
	
	public synchronized boolean remove(URI uri) {
		return hostList.remove(uri);
	}
	
	public synchronized boolean add(URI uri) {
		return hostList.add(uri);
	}
	
	public synchronized boolean add(Vector<URI> uris) {
		return hostList.addAll(uris);
	}
	
	public Vector<URI> getAllHosts() {
		Vector<URI> v = new Vector<URI>();
		v.addAll(hostList);
		Collections.sort(v);
		return v;
	}

	public Vector<URI> getOtherHosts() {
		Vector<URI> v = new Vector<URI>();
		for (URI uri : hostList)
			if (!isLocalUri(uri))
				v.add(uri);
		Collections.sort(v);
		return v;
	}

	public boolean isLocalUri(URI uri) {
		boolean isLocalUri = false;
		for (URI localUri : localUris)
			if (localUri.compareTo(uri) == 0)
				isLocalUri = true;
		return isLocalUri;
	}
}
