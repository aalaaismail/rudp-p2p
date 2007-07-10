package chat;

import java.net.*;
import java.io.*;


public class ChatClient {
	public static final String  ANMELDUNG = "ANMELDUNG||NICK||";
	public static final String  NICKSCHONVORHANDEN = "ANMELDUNG||STATUS||";
	public static int port;
	public static String server;
	public static int pollingIntervall;
	public static int datagramLaenge;
	
	public static void main(String args[]){
		
		if (args.length!=3)	{
			System.out.println("Usage: ");
			System.out.println("java ChatClient server port pollingIntervall (in Millisekunden");
			System.exit(-1);
		}
		
		server = args[0];
		port = new Integer(args[1]).intValue();
		pollingIntervall = new Integer(args[2]).intValue();
		//System.out.println("frisPollingIntervall: " + pollingIntervall);
		
	
		try	{

			String text = null;
			DatagramPacket packet;
			byte[] ba = "test".getBytes();
			DatagramSocket socket = new DatagramSocket();
			InetAddress ia =  InetAddress.getByName( server );
			packet = new DatagramPacket( new byte[datagramLaenge], datagramLaenge);
			System.out.println("Bitte geben Sie Ihren Nickname ein: ");
			BufferedReader br = new BufferedReader(new InputStreamReader( System.in )  );
            text = ANMELDUNG + br.readLine();
            ba = text.getBytes();
            packet.setData( ba );
            packet.setPort(port);
            packet.setAddress(ia);
            socket.send( packet );
            //socket.receive(packet);
            //String angemeldet = new String(packet.getData());
            String angemeldet= "";
            //System.out.println("secondenpoi: " + pollingIntervall);
			PollingClient myPollingClient = new PollingClient(pollingIntervall, ia, port, socket );
			myPollingClient.start();
			System.out.println("Bitte geben Sie die Nachrichten im Format: \"nickname message\" ein.");
            System.out.println("Viel Spaﬂ beim Chatten! :-)");
			br = new BufferedReader(new InputStreamReader( System.in )  );
	        do {
	            text = br.readLine();
	            ba = text.getBytes();
	            packet.setData( ba, 0, ba.length );
	            socket.send( packet );
	         } while( ! text.equals("ENDE") );

			System.exit(0);

		}
		catch( IOException e ) {
	          System.err.println("Ausnahmefehler: " +  e );
		}
	
			
	}	
}