package chat;

import java.net.*;
import java.io.*;
import java.util.*;
import chat.valueobjects.*;


public class ChatServer {
	
	public static DatagramSocket socket;
	public static int myPort;
	static int datagramLaenge  = 1024;
	public static final String  ANMELDUNG = "ANMELDUNG||NICK||";
	public static final String  NICKSCHONVORHANDEN = "ANMELDUNG||STATUS||";
	public static final String ABFRAGE = "ABFRAGE";
	static final String  ENDE      = "ENDE";
	public static ArrayList myClients = new ArrayList();
	public static ArrayList myStoredMessages = new ArrayList();
	
	public ChatServer()	{
		
		
	}
	
	public static void main(String args[]){
		
		if (args.length!=3)	{
			System.out.println("Usage:");
			System.out.println("java ChatServer port client-timeout Hostname:Portnummer");
			System.exit(-1);
		}
		
		myPort = new Integer(args[0]).intValue();
		
		try	{
			socket = new DatagramSocket(myPort);
			DatagramPacket paket = new DatagramPacket( new byte[datagramLaenge], datagramLaenge);
			//DatagramPacket paket = new DatagramPacket();
			for( ;; ) {
				System.out.println("anfang");
				//socket = new DatagramSocket(myPort);
				socket.receive(paket);
				InetSocketAddress add = (InetSocketAddress)paket.getSocketAddress();
				System.out.println("add" + add);
				System.out.println(paket.getData());
				System.out.println("paket.getLength()" + paket.getLength());
				paket.setLength(datagramLaenge);
				System.out.println("paket.getLength()" + paket.getLength());
				String text = new String(paket.getData(), 0, paket.getLength());
				System.out.println("text: " + text);
				//neuer Client meldet sich an
				if (text.indexOf(ANMELDUNG)!=-1)	{
					
					String nickName = text.substring(ANMELDUNG.length());
					System.out.println("Folgender Nickname versucht sich anzumelden: " + nickName);
					
					boolean nickSchonVorhanden = false;				
					Iterator it = myClients.iterator();
					
					//Prüfung ob Nickname schon vorhanden
					while (it.hasNext())	{
						
						Clients myClient = (Clients) it.next();
						System.out.println("nicknamen: " + myClient.getNickName());
						System.out.println("nickSchonVorhanden: " + myClient.getNickSchonVorhanden());
						System.out.println("SocketAdress: " + myClient.getSocketAddress());
						if (myClient.getNickName().equals(nickName))
							nickSchonVorhanden = true;					
					}
					
					Clients newClient = new Clients();
					newClient.setNickName(nickName);
					newClient.setNickSchonVorhanden(nickSchonVorhanden);
					newClient.setSocketAddress(add);
					myClients.add(newClient);
				}
				
				else if (text.indexOf(ABFRAGE) != -1)	{
					
					String myNN = "";
					boolean nickvorhanden = true;
					
					//nickname des requesters rausfinden
					Iterator it = myClients.iterator();
					while (it.hasNext())	{
						Clients mycl = (Clients)it.next();
						InetSocketAddress mySA = mycl.getSocketAddress();
						System.out.println("mySA: " + mySA);
						System.out.println("add: " + add);
						if (mySA.equals(add))	{
							System.out.println("gefunden: " + mycl.getNickName());
							myNN = mycl.getNickName();
							nickvorhanden = mycl.getNickSchonVorhanden();
							System.out.println( "nickvorhanden: " + nickvorhanden);
						}
					}
					
					//rausfinden, ob message für nickname vorhanden
					it = myStoredMessages.iterator();
					int j = 0;
					while (it.hasNext())	{
						j++;
						
						StoredMessages myStM = (StoredMessages)it.next();
						if (myStM.getAnNick().equals(myNN) && !myStM.getZugestellt() && !nickvorhanden)	{
							System.out.println("message an client schicken");
							System.out.println(myStM.getMessage());
							byte[] ba = myStM.getMessage().getBytes();
							paket = new DatagramPacket( ba, ba.length, add);
							socket.send( paket );
							myStM.setZugestellt(true);	
						}
						else	{
							byte[] inhalt = new byte[1024];
							inhalt = "".getBytes();
							//paket = new DatagramPacket(  inhalt, inhalt.length, add);
							socket.send(paket);
						}
					}	
					if (j == 0)	{
							byte[] inhalt = new byte[1024];
							inhalt = "".getBytes();
							//paket = new DatagramPacket( inhalt, inhalt.length, add);
							socket.send(paket);
					}
					
				}
				
				else if (text.indexOf(" ") != -1)	{
				
					String messageSendTo = text.substring(0,text.indexOf(" "));
					String messageToSend = text.substring(text.indexOf(" "));
					StoredMessages myMessage = new StoredMessages();
					myMessage.setAnNick(messageSendTo);
					myMessage.setMessage(messageToSend);
					myMessage.setZugestellt(false);
					myStoredMessages.add(myMessage);
					
					Iterator it = myStoredMessages.iterator();
					while (it.hasNext())	{
						
						StoredMessages sm = (StoredMessages) it.next();
						System.out.println("senden an: " + sm.getAnNick());
						System.out.println("message: " + sm.getMessage());
						System.out.println("gesendet: " + sm.getZugestellt());
						
					}
				}
				
				
				//socket.close();
				//System.out.println( add +">" + text);
			}

		}
		catch (SocketException e)	{
			System.out.println(e);
		}	
		catch (IOException e)	{
			System.out.println(e);
		}
	}
}