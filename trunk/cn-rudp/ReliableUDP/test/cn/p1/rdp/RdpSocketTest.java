/*
 * RdpSocketTest.java
 * JUnit based test
 *
 * Created on June 7, 2007, 4:08 PM
 */

package cn.p1.rdp;

import junit.framework.*;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 *
 * @author goran
 */
public class RdpSocketTest extends TestCase {
    
    public RdpSocketTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    /**
     * Test of receive method, of class cn.p1.rdp.RdpSocket.
     
    public void testReceive() throws Exception {
        System.out.println("receive");

        byte[] buf = new byte[256];
        DatagramPacket p = new DatagramPacket(buf, 256);

        RdpSocket instance = new RdpSocket();
        
        instance.receive(p);
        
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    */
    /**
     * Test of send method, of class cn.p1.rdp.RdpSocket.
     */
    public void testSend() throws Exception {
        try {
            System.out.println("send");
            int payload_length = 256;
            byte[] buf = new byte[payload_length];
            for (int i = 0; i < payload_length; i++)
                buf[i] = (byte) i;
            InetAddress receiver = InetAddress.getByName("192.168.0.10");
            DatagramPacket p = new DatagramPacket(buf, buf.length, receiver, 4447);
            
            RdpSocket instance = new RdpSocket(4447);
            instance.send(p);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
       fail("The test case is a prototype.");
    }

}
