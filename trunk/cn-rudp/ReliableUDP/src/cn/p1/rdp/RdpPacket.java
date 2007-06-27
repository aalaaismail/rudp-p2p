/*
 * RdpPacket.java
 *
 * Created on June 7, 2007, 3:50 PM
 *
 */

package cn.p1.rdp;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author lazafi
 */
public class RdpPacket implements Serializable {
    final int SYN_BIT = 0;
    final int ACK_BIT = 1;
    final int OK_BIT = 2;
    
    private Integer seq;
    private byte[] data;
    private byte[] buf;
    private byte options = 0;
    private byte[] checksum = null;
    
    
    /** Creates a new instance of RdpPacket */
    public RdpPacket() {
    }
    public RdpPacket(byte[] data, InetAddress addr, int port){
        buf = new byte[256];
        this.data = data;
    }
    
    
    
    public byte[] toByteArray() {
        ByteArrayOutputStream bo = null;
        ObjectOutputStream so = null;
         try {

             checksum = checksum(data);

             bo = new ByteArrayOutputStream();
            so = new ObjectOutputStream(bo);
            so.writeObject(this);
            so.flush();
            so.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print("1");
        System.out.println(bo.toByteArray());
        
        return bo.toByteArray();
    }
    

    protected byte[] checksum(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest checksum;
        checksum = MessageDigest.getInstance("MD5");
        System.out.println(b);
        checksum.update(b);
        return checksum.digest();
    }
    
    /** Returns the data buffer. */
    public byte[] getData() {
        return this.data;
    }
    /**  Returns the length of the data to be sent or the length of the data received. */
    public int 	getLength() {
        return toByteArray().length;
    }

    
    public void sync() {
        this.options &= (1 << SYN_BIT);
    }

    public boolean getSync() {
        return ((this.options & (~(1 << SYN_BIT))) == (1 << SYN_BIT));
    }
    
    public DatagramPacket render(){
        
        //return new DatagramPacket(this.toByteArray(), this.getLength(), p.getAddress(), p.getPort());
       return null; 
    }
    
    public void	setSeq(int seq) {
        this.seq = seq;
    }
    /** Set the data buffer for this packet. */
    public void	setData(byte[] buf) {
        this.data = buf;
    }
    /** Set the length for this packet. */
    public void	setLength(int length) {

    }
}
