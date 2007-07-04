/*
 * RdpSocketTest.java
 * JUnit based test
 *
 * Created on June 7, 2007, 4:08 PM
 */

package cn.p1.rdp;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 *
 * @author goran
 */
public class RdpSocketTest {
    
    public RdpSocketTest(String testName) {
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    /**
     * Test of receive method, of class cn.p1.rdp.RdpSocket.
     */
    public void testReceive() throws Exception {
        System.out.println("receive");

        byte[] buf = new byte[256];
            InetAddress receiver = InetAddress.getLocalHost();
            //DatagramPacket p = new DatagramPacket(buf, buf.length, receiver, 4448);
            DatagramPacket p = new DatagramPacket(buf, buf.length);
            
        RdpSocket instance = new RdpSocket(4448);
        
        for (int i = 0; i < 1; i++)
        {
            instance.receive(p);
            System.out.println(i + " " + new String(p.getData()));
        }
    }
   
    /**
     * Test of send method, of class cn.p1.rdp.RdpSocket.
     */
    public void testSend() throws Exception {
        try {
            System.out.println("send");
            int payload_length = 256;
            /*
             byte[] buf = new byte[payload_length];
            for (int i = 0; i < payload_length; i++)
                buf[i] = (byte) i;
             */
            String str = "qwertzuiop";
            byte [] buf = str.getBytes();
            InetAddress receiver = InetAddress.getLocalHost();
             
            RdpSocket instance = new RdpSocket(4447);
 
            str = "1asdfghjkl";
            instance.send(new DatagramPacket(str.getBytes(), str.getBytes().length, receiver, 4448));
            str = "2yxcvbvnbm";
//            instance.send(new DatagramPacket(str.getBytes(), str.getBytes().length, receiver, 4448));
            str = "3qwerrtw53";
//            instance.send(new DatagramPacket(str.getBytes(), str.getBytes().length, receiver, 4448));
            str = "4444444444";
//            instance.send(new DatagramPacket(str.getBytes(), str.getBytes().length, receiver, 4448));
            
            // new connection
//            instance.send(new DatagramPacket(str.getBytes(), str.getBytes().length, receiver, 4449));

            
        } catch (Exception e) {
            System.out.print(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
            try {
    
        RdpSocketTest r = new RdpSocketTest("anttest");
        if (args[0].equals("s")) {
                r.testSend();
            
        }

        if (args[0].equals("r")) {
            r.testReceive();
        }
    
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }
    
    
}
