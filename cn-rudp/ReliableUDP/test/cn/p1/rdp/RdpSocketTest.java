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
    public void testReceive(int err) throws Exception {
        System.out.println("receive");

        byte[] buf = new byte[256];
            InetAddress receiver = InetAddress.getLocalHost();
            //DatagramPacket p = new DatagramPacket(buf, buf.length, receiver, 4448);
            DatagramPacket p = new DatagramPacket(buf, buf.length);
            
        RdpSocket instance = new RdpSocket(4448);
        instance.setErrorRate(err);
        
        for (int i = 0; i < 4; i++)
        {
            try {
            instance.receive(p);
                System.out.println(i + " " + new String(p.getData()));
            } catch (RdpException re) {
            System.out.println(i + " error");                
            }
        }
    }
   
    /**
     * Test of send method, of class cn.p1.rdp.RdpSocket.
     */
    public void testSend(int err) throws Exception {
        try{
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
            instance.setErrorRate(err);
        try{
            str = "0123456789";
            instance.send(new DatagramPacket(str.getBytes(), str.getBytes().length, receiver, 4448));
            } catch (RdpException re) {
                System.out.print(re.getMessage());
                re.printStackTrace();
            }
        try{
            str = "1234567890";
            instance.send(new DatagramPacket(str.getBytes(), str.getBytes().length, receiver, 4448));
            } catch (RdpException re) {
                System.out.print(re.getMessage());
                re.printStackTrace();
            }
        try{
            str = "2345678901";
            instance.send(new DatagramPacket(str.getBytes(), str.getBytes().length, receiver, 4448));
            } catch (RdpException re) {
                System.out.print(re.getMessage());
                re.printStackTrace();
            }
        try{
            str = "3456789012";
            instance.send(new DatagramPacket(str.getBytes(), str.getBytes().length, receiver, 4448));
            } catch (RdpException re) {
                System.out.print(re.getMessage());
                re.printStackTrace();
            }
            
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
                r.testSend(Integer.parseInt(args[1]));
            
        }

        if (args[0].equals("r")) {
            r.testReceive(Integer.parseInt(args[1]));
        }
    
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }
    
    
}
