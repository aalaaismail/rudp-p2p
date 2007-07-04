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
import java.net.SocketTimeoutException;
import java.util.Hashtable;

/**
 *
 * @author lazafi
 * // */

public class RdpSocket extends DatagramSocket {
    
    public final int BUFLEN = 55000;
//    private Hashtable<ConnectionID, Integer> sendConnectionTable = null;
//    private Hashtable<ConnectionID, Integer> receiveConnectionTable = null;
    private Hashtable<String, Integer> sendConnectionTable = null;
    private Hashtable<String, Integer> receiveConnectionTable = null;
    private int packetCount = 0;
    private int errorRate = 10;
    
    /** Creates a new instance of RdpSocket */
    public RdpSocket(int port) throws SocketException {
        super(port);
        setSoTimeout(5000);
        //sendConnectionTable = new Hashtable<ConnectionID, Integer>();
        //receiveConnectionTable = new Hashtable<ConnectionID, Integer>();
        sendConnectionTable = new Hashtable<String, Integer>();
        receiveConnectionTable = new Hashtable<String, Integer>();
    }
    
    
    public void receive(DatagramPacket p) throws IOException {
        try {
            byte[] databuf = new byte[BUFLEN];
            DatagramPacket inPacket = new DatagramPacket(databuf, databuf.length);
            
            receive_bench(inPacket);
            ConnectionID connection = new ConnectionID(inPacket.getAddress(), inPacket.getPort());
            RdpPacket packet = RdpPacket.instanciate(inPacket.getData());
            System.out.print("receiving packet " + connection.getAddress().getHostAddress() + ":" + connection.getPort() + "\t");
            
            
            if (receiveConnectionTable.containsKey(connection.toString())) {
                System.out.println("EXISTING CONNECTION");
                // receive DATA packet
                if ((!packet.isSync()) && (!packet.isAck())) {
                    if (packet.getSeq() == receiveConnectionTable.get(connection.toString())) {
                        // seq OK
                        if (packet.checkChecksum()) {
                            sendAck(connection, receiveConnectionTable.get(connection.toString()) + 1);
                            p.setData(packet.getData());
                        } else {
                            System.out.println("WRONG CHECKSUM1");
                            throw new RdpException("RUDP: wrong checksum in received data packet");
                            //sendAck(connection, receiveConnectionTable.get(connection.toString()));
                        }
                    } else {
                        System.out.println("WRONG SEQ1");
                        throw new RdpException("RUDP: wrong order (SEQ) in received data");
                    }
                    
                } else {
                    System.out.println("DATA PACKET ERWARTET1");
                    throw new RdpException("RUDP: wrong packet type (DATA excepted)");
                }
                
                
            } else {
                System.out.println("NEW CONNECTION");
                //RdpPacket packet = receivePacket(connection);
                if (packet.isSync()) {
                    int seq = packet.getSeq();
                    // store connection
                    receiveConnectionTable.put(connection.toString(), new Integer(seq));
                    // send ack
                    
                    //System.out.println(connection.toString());
                    sendAck(connection, receiveConnectionTable.get(connection.toString()) + 1);
                } else {
                        throw new RdpException("RUDP: Connection not open, SYNC required");
                }
                // receive DATA packet
                packet = receivePacket(connection);
                if ((!packet.isSync()) && (!packet.isAck())) {
                    if (packet.getSeq() == receiveConnectionTable.get(connection.toString())) {
                        // seq OK
                        if (packet.checkChecksum()) {
                            sendAck(connection, receiveConnectionTable.get(connection.toString()) + 1);
                            p.setData(packet.getData());
                        } else {
                            throw new RdpException("RUDP: wrong checksum in received data packet");
                        //sendAck(connection, receiveConnectionTable.get(connection.toString()));
                        }
                    } else {
                        throw new RdpException("RUDP: wrong order (SEQ) in received data");
                    }
                    
                } else {
                    throw new RdpException("RUDP: wrong packet type (DATA excepted)");
                }
                
            }
            
        } catch (SocketTimeoutException ste) {
            throw new RdpException("RUDP: no packet received!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void send(DatagramPacket packet) throws IOException {
        byte[] buf = new byte[256];
        try {
            
            System.out.print("sending packet to " + packet.getAddress().getHostAddress() + packet.getPort());
            
            ConnectionID connection = new ConnectionID(packet.getAddress(), packet.getPort());
            
            if (sendConnectionTable.containsKey(connection.toString())) {
                // continue with old connection
                System.out.println(" old connection");
            } else {
                System.out.println(" establishing new connection");
                // make new connection
                // send sync packet
                int seq = randomSeq();
                RdpPacket syncPacket = new RdpPacket();
                syncPacket.setSeq(seq);
                syncPacket.sync();
                
                sendPacket(syncPacket, connection);
                
                System.out.print("receiving packet ack .. ");
                RdpPacket ackPacket = receivePacket(connection);
                
                if (ackPacket.isAck()) {
                    seq = ackPacket.getSeq();
                    System.out.print("ACK SEQ=" + seq);
                    
                        sendConnectionTable.put(connection.toString(), new Integer(seq));
                } else {
                    
                    System.out.println("ACK ERWARTET");
                        throw new RdpException("RUDP: false ACK received");
                }
            }
            try {
                
                RdpPacket dataPacket = new RdpPacket(buf);
                dataPacket.setData(packet.getData());
                int newSeq = sendConnectionTable.get(connection.toString());
                dataPacket.setSeq(newSeq);
                sendPacket(dataPacket, connection);
                
                // send seq
                // recv ack
                // store connection
                // constr. checksum
                // send head + data
                
                
                System.out.print("receiving packet ack .. ");

                //setSoTimeout(1000);
                RdpPacket ackPacket;
                //try {
                ackPacket = receivePacket(connection);
                //} catch (SocketTimeoutException ste) {
                //    System.out.print("timeout, resending");
                //    sendPacket(dataPacket, connection);                
                //    ackPacket = receivePacket(connection);
                //    System.out.print("received!! SEQ=" + ackPacket.getSeq());
                //}
                if (ackPacket.isAck()) {
                    int seq = ackPacket.getSeq();
                    System.out.println("ACK SEQ=" + seq);
                    // check if old packet requested
                    if (seq == sendConnectionTable.get(connection.toString())) {
                        // resend packet
                        System.out.println("RESENDING SEQ=" + seq);                        
                        sendPacket(dataPacket, connection);
                    } else {
                        sendConnectionTable.put(connection.toString(), new Integer(seq));
                    }
               } else {
                    
                    System.out.println("ACK ERWARTET2");
                        throw new RdpException("RUDP: false ACK received");
                }
                
                
            } catch (SocketTimeoutException ste) {
                        throw new RdpException("RUDP: Receive Timeout while waiting for ACK");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

        

    private void receive_bench(DatagramPacket p) throws IOException {
        super.receive(p);
        //System.out.print(((int) Math.round(Math.random()*100)));        
        if (((int) Math.round(Math.random()*100)) < getErrorRate()) {
        //if (((packetCount++) % errorRate) == 0) {
            RdpPacket packet = null;
            try {
                packet = RdpPacket.instanciate(p.getData());
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        System.out.print("DROPPED INCOMMING:\t");
        if (packet.isAck()) System.out.print("ACK\t");
        if (packet.isSync()) System.out.print("SYN\t");
        if ((!packet.isAck()) && (!packet.isSync())) System.out.print("DATA\t");
        System.out.print(packet.getSeq());
        System.out.println();
            super.receive(p);
        }
    }
    
    private RdpPacket receivePacket(ConnectionID connection) throws IOException, ClassNotFoundException, Exception {
        
        byte[] databuf = new byte[BUFLEN];
        DatagramPacket inPacket = new DatagramPacket(databuf, databuf.length, connection.getAddress(), connection.getPort());
        receive_bench(inPacket);
        RdpPacket dataPacket = RdpPacket.instanciate(inPacket.getData());
        return dataPacket;
    }

    private void sendPacket(RdpPacket packet, ConnectionID con) throws IOException, Exception{
        byte[] payload = packet.toByteArray();
        
        DatagramPacket lowPacket = new DatagramPacket(payload, payload.length, con.getAddress(), con.getPort());
        //System.out.println("OUT: ---" + new String(lowPacket.getData()) + "---");
        System.out.print("OUT:\t");
        if (packet.isAck()) System.out.print("ACK\t");
        if (packet.isSync()) System.out.print("SYN\t");
        if ((!packet.isAck()) && (!packet.isSync())) System.out.print("DATA\t");
        System.out.print(packet.getSeq());
        System.out.println();
        super.send(lowPacket);
    }
    
    
    private void sendAck(ConnectionID connection, Integer seq) throws IOException, Exception {
        byte buf[] = new byte[256];
        RdpPacket ackPacket = new RdpPacket();
        
        ackPacket.setSeq(seq);
        ackPacket.ack();
        sendPacket(ackPacket, connection);
        receiveConnectionTable.put(connection.toString(), new Integer(seq));
        
    }
    
    private void sendSync() {
    }
    
    private int randomSeq() {
        return (int) Math.round(Math.random()*100000);
    }
    
    private class ConnectionID {
        private InetAddress addr = null;
        private int port = 0;
        ConnectionID(InetAddress addr, int port) {
            this.setAddress(addr);
            this.setPort(port);
        }
        
        public InetAddress getAddress() {
            return addr;
        }
        
        public void setAddress(InetAddress addr) {
            this.addr = addr;
        }
        
        public int getPort() {
            return port;
        }
        
        public void setPort(int port) {
            this.port = port;
        }
        public String toString(){
            return new String(addr.getHostAddress() + ":" + port);
        }
        
    }
    
    private class ConnectionHandler {
        
        private Thread currentThread;
        
        public ConnectionHandler(){
            
        }
        public ConnectionHandler(ConnectionID cID){
            
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

    public int getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(int errorRate) {
        this.errorRate = errorRate;
    }
    
    
}
