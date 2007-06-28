package at.cn.p2p;

import java.io.File;
import java.net.URI;
import java.util.Vector;

public class TextGui {

	public static void main(String[] args) {
		if (args.length != 2)
			System.err.println("Usage: TextGui <property-file> <uri>");
		URI firstHost;
		try {
			firstHost = new URI(args[1]);
			Util.loadProperties(args[0]);
			TextGui gui = new TextGui(firstHost);
			gui.doMenu();
		} 
		catch (Exception e) {
			System.err.println("Usage: TextGui <property-file> <uri>");
		}
	}
	
	private Application application;
	
	public TextGui(URI firstHost) {
		application = new Application(firstHost);
		application.start();
	}

	public void doMenu() {
		
		boolean run_menu = true;
		int menu_input = 0;
		
		while (run_menu) {			
			System.out.println("");	
			System.out.println("Waehlen Sie bitte aus folgenden Menuepunkten.");
			System.out.println("");
			System.out.println("1 Files suchen");
			System.out.println("2 Suchergebnis anzeigen");
			System.out.println("3 File downloaden");
			System.out.println("4 Hostlist anzeigen");
			System.out.println("5 Shared Files anzeigen");
			System.out.println("6 Programm beenden");
			System.out.println("");
			System.out.println("Ihre Wahl: ");
			System.out.flush();
			
			menu_input = Util.getUserInputInt();
	    
			switch (menu_input)	{	    
				case 1: 
					doSuche();
					break;
				case 2:
					printSearchResult();
					break;
				case 3:
					doDownload();
					break;
				case 4:
					Util.printHosts(application.getHostlist().getAllHosts());
					break;
				case 5:
					application.getSharedFiles().printSharedFiles();
					break;
				case 6:
					run_menu = false;
					break;
	    	}	
		}
		
		application.stop();
		System.exit(0);
	}
	
	public void doSuche() {
		System.out.println("");
		System.out.println("Suchstring: ");
		System.out.flush();
		
		String searchString = Util.getUserInputString();
		
		application.search(searchString);
		
		printSearchResult();
	}
	
	public void doDownload() {
		System.out.println("");
		System.out.println("SearchResult Id zum Download: ");
		System.out.flush();
		
		int id = Util.getUserInputInt();
		
		File file = application.getSearchResult().getFileFromId(id);
		Vector<URI> hosts = application.getSearchResult().getHostsFromFile(file);
		
		System.out.println("trying to download file " + file + " from following hosts:");
		Util.printHosts(hosts);
		
		application.download(file, hosts);
	}
	
	public void printSearchResult() {
		Util.printSearchResult(application.getSearchResult());
	}
}
