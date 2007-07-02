package at.cn.p2p.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Download {
	
	private Log log = LogFactory.getLog(Download.class);
	
	private File remoteFile;
	private File localFile;
	private List<URI> hosts;
	private byte[][] stream;	
	
	public Download(File remoteFile, File localFile, List<URI> hosts, int size) {
		this.remoteFile = remoteFile;
		this.localFile = localFile;
		this.hosts = hosts;
		this.stream = new byte[size][];
	}

	public List<URI> getHosts() {
		return hosts;
	}

	public File getLocalFile() {
		return localFile;
	}

	public File getRemoteFile() {
		return remoteFile;
	}

	public int getSize() {
		return stream.length;
	}

	public synchronized void addFragmentToStream(byte[] b, int index, int len) {
		byte[] buffer = new byte[len];
		for (int i=0; i<len; i++)
			buffer[i] = b[i];
		log.debug("addFragmentToStream " + index + " " + buffer.length);
		stream[index] = buffer;
	}
	
	public void writeToDisk() {
		log.info("writing file to disk");
		try {
			OutputStream outputStream = new FileOutputStream(localFile);
			log.info("size of file: " + stream.length);
			for (int i = 0; i < stream.length; i++) {
				if (stream[i]!=null)
					outputStream.write(stream[i], 0, stream[i].length);
				else 
					System.err.print(" "+i+" ");
			}
			outputStream.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		log.info("file " + localFile + " written to disk");
	}
	
	public float getDownloadStatus() {
		float cnt = 0;
		for (int i = 0; i < stream.length; i++)
			if (stream[i]!=null)
				cnt++;
		float perc = (cnt / ((float)stream.length)) * 100.0f;
		return perc;
	}
	
	public Map<Integer, Boolean> getDownloadMap() {
		Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
		for (int i = 0; i < stream.length; i++) {
			if (stream[i]!=null)
				map.put(i, true);
			else 
				map.put(i, false);
		}
		return map;
	}

	@Override
	public String toString() {
		String s = new String();
		s += remoteFile.getName() + " " + hosts + "\n";
		s += getDownloadStatus() + "%";		
		return s;
	}
}
