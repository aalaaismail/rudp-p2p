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
	
	private ServerInit serverInit;
	
	public TextGui(URI firstHost) {
		serverInit = new ServerInit(firstHost);
		serverInit.start();
		
		// wait for server threads to start
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
					Util.printHosts(serverInit.getHostlist());
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
		
		serverInit.stop();
		System.exit(0);
	}
	
	public void doSuche() {
		System.out.println("");
		System.out.print("Suchstring: ");
		
		String suchString = Util.getUserInputString();
	}
}
