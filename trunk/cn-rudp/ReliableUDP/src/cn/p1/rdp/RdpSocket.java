/*
 * RdpSocket.java
 *
 * Created on June 7, 2007, 3:52 PM
 *
 */

package cn.p1.rdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;

/**
 *
 * @author lazafi
 * // */

public class RdpSocket extends DatagramSocket {
    
    private int seq = 0;
    private Hashtable<ConnectionID, Integer> connectionTable = null;
    
    /** Creates a new instance of RdpSocket */
    public RdpSocket(int port) throws SocketException {
        super(port);
        connectionTable = new Hashtable<ConnectionID, Integer>();
    }
    
    
    public void receive(DatagramPacket p) throws IOException {
       super.receive(p);
       System.out.println("received packet " + p.getAddress().getHostAddress() + p.getPort()); 
        // if syn
       // send ack
        
       // else data
       // controll checksum
       // send ok
       
    }
    
    
    public void send(DatagramPacket packet) throws IOException {

        System.out.print("PACKET:" + packet.getAddress().getHostAddress() + packet.getPort());
        
        ConnectionID connection = new ConnectionID(packet.getAddress(), packet.getPort());
        if (connectionTable.contains(connection)) {
            // continue with old connection
        } else {
            System.out.println("establishing new connection");
            // make new connection
            // send sync packet
            int seq = randomSeq();

            byte[] buff = new byte[1];
            buff [0] = 1;
            RdpPacket syncPacket = new RdpPacket(buff, packet.getAddress(), packet.getPort());
            syncPacket.setSeq(seq);
            syncPacket.sync(); 
            DatagramPacket lowPacket = new DatagramPacket(syncPacket.toByteArray(), syncPacket.getLength(), packet.getAddress(), packet.getPort()+1);
            System.out.println("sending packet sync " + Integer.toHexString(seq));
            super.send(lowPacket);
            
            System.out.print("receiving packet ack .." + packet.getPort());
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            System.out.print("receiving packet ack .." + packet.getPort());
            
            byte[] buf = new byte[256];
            DatagramPacket ackPacket = new DatagramPacket(buf, 256, packet.getAddress(), packet.getPort());
            
            super.receive(ackPacket);
            System.out.println(ackPacket.getData());
            
            
        
            connectionTable.put(connection, new Integer(seq));

            

            RdpPacket dataPacket = new RdpPacket(buff, packet.getAddress(), packet.getPort());
            dataPacket.setData(packet.getData());

            lowPacket = new DatagramPacket(dataPacket.toByteArray(), dataPacket.getLength(), packet.getAddress(), packet.getPort());
            System.out.println("sending first data packet " + lowPacket.getData());
            super.send(lowPacket);
            
        // send seq
        // recv ack
        // store connection
        // constr. checksum
        // send head + data
        }            
            
    }
    
    private void sendSync() {
    }
    
    private int randomSeq() {
        return (int) Math.round(Math.random()*100000);
    }
    
    private class ConnectionID {
        InetAddress addr = null;
        int port = 0;
        ConnectionID(InetAddress addr, int port) {
            this.addr = addr;
            this.port = port;
        }
        
    }
    
    private class ConnectionHandler {
     
        private Thread currentThread;
        
        public ConnectionHandler (){
        
        }
        public ConnectionHandler (ConnectionID cID){
        
        }
        
        public void start() {

        /*if(currentThread == null){
            currentThread = new Thread(this);
            currentThread.start();
        */
            
        
        
        
        
    }

    public void stop() { currentThread = null; }

    public void run() {
        while(currentThread != null) {
        }
    }
    
 } 
    
    
}
