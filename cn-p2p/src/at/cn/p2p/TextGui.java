package at.cn.p2p;

import java.net.URI;
import java.net.URISyntaxException;

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
		catch (URISyntaxException e) {
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
			System.out.println("2 Hostlist anzeigen");
			System.out.println("5 Programm beenden");
			System.out.println("");
			System.out.println("Ihre Wahl: ");
			System.out.flush();
			
			menu_input = Util.getUserInputInt();
	    
			switch (menu_input)	{	    
				case 1: 
					this.doSuche();
					break;
				case 2:
					Util.printHosts(application.getHostlist().getAllHosts());
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
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
	}
}
