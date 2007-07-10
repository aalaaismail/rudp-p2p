package chat;

import java.net.*;
import java.io.*;
import java.util.*;
import chat.valueobjects.*;


public class PollingClient extends Thread {
	
	int pollingIntervall;
	InetAddress ia;
	int port;
	DatagramSocket socket;
	public static final String ABFRAGE = "ABFRAGE";
	public int datagramLaenge = 64;
	
	public PollingClient(int pollingInterval, InetAddress ia, int port, DatagramSocket socket)	{
		//System.out.println("poo: " + pollingInterval);
		this.pollingIntervall = pollingInterval;
		this.ia = ia;
		this.port = port;
		this.socket = socket;
	
	}
	
	public void run()	{
		
		DatagramPacket packet;

		
		for (;;)	{
			try	{
				System.out.println("hier0");
				byte[] ba = new byte[datagramLaenge];
				System.out.println(ba.length);
				/*for (int i = 0; i<ba.length; i++)
					ba[i] = new Byte(" ").byteValue();*/
				ba = (ABFRAGE).getBytes();
				
				System.out.println("hier1");
				packet = new DatagramPacket( new byte[datagramLaenge], datagramLaenge);
				System.out.println(ba);
				System.out.println(ba.length);
				//socket.connect(ia, port);
				//packet = new DatagramPacket( ba, ba.length, ia, port);
				packet.setAddress(ia);
				packet.setLength(ba.length);
				packet.setData(ba);
				packet.setPort(port);
				System.out.println("packet.length" + packet.getLength());
				System.out.println(packet.getData());
				socket.send( packet );
				// Warten auf Ankunft eines Datagrammes
				System.out.println("hier2");
				packet = new DatagramPacket( new byte[datagramLaenge], datagramLaenge);
				System.out.println("hier3");
			    socket.receive(packet);
			    // Daten aus Datagramm extrahieren
			    System.out.println("hier4");
			    String msg = new String(packet.getData());
			    //System.out.println("msglaenge: " + msg.length());
				System.out.println("pollingIntervall: " + this.pollingIntervall);
			    if (!msg.equalsIgnoreCase(" "))
			    	System.out.println("msg: " + msg);
				sleep(this.pollingIntervall);
			}
			catch(IOException e)	{
				System.out.println(e);
			}
			catch(InterruptedException e)	{
				System.out.println(e);
			}

		
		}
	}
	
}