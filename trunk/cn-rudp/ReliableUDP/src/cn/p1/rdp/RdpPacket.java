/*
 * RdpPacket.java
 *
 * Created on June 7, 2007, 3:50 PM
 *
 */

package cn.p1.rdp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.xml.transform.TransformerConfigurationException;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;
import org.xml.sax.SAXException;

/**
 *
 * @author lazafi
 */
@Root
public class RdpPacket implements Serializable {
    final int SYN_BIT = 0;
    final int ACK_BIT = 1;
    final int OK_BIT = 2;
    
    @Attribute
    private int seq;
    @Element(required=false,data=true)
    private byte[] data;
    private byte[] buf;
    @Attribute
    private byte options = 0;
    @Attribute
    private boolean sync = false;
    @Attribute
    private boolean ack = false;
    @Attribute
    private boolean ok = false;
    @Element(required=false)
    private byte[] checksum = null;
    
    
    /** Creates a new instance of RdpPacket */
    public RdpPacket() {
        //data = new byte[256];
    }
    
  /*  public RdpPacket(DatagramPacket p) {
        //p.getAddress();
        data = p.getData();
        //p.getLength();
        p.get
   
    }
   */
    public RdpPacket(byte[] data){
        //buf = new byte[256];
        this.data = data;
    }
    
    
    public static RdpPacket instanciate(byte[] data) throws IOException, ClassNotFoundException, Exception {
        
     /* ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
       RdpPacket retval = (RdpPacket) in.readObject();
       in.close();
       return  retval;
      */
     /*
      XMLDecoder d = new XMLDecoder( new BufferedInputStream( new ByteArrayInputStream(data)));
        RdpPacket retval = (RdpPacket) d.readObject();
        d.close();
        return  retval;
      */
        Serializer serializer = new Persister();
        RdpPacket p = serializer.read(RdpPacket.class, new BufferedInputStream(new ByteArrayInputStream(new String(data).trim().getBytes())));
        return p;
    }
 
    
    public byte[] toByteArray() throws IOException, Exception {
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    Serializer serializer = new Persister();
    serializer.write(this, bo);
    bo.flush();
    return bo.toByteArray();
    }

    
/*        public byte[] toByteArray() throws IOException {
         ByteArrayOutputStream bo = new ByteArrayOutputStream();
         XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));
         e.writeObject(this);
         e.close();
         //return retval;
         bo.flush();
 
         return (new String(bo.toByteArray()).trim().getBytes());
  }
 
 */
    /*
     public byte[] toByteArray() throws IOException, TransformerConfigurationException, SAXException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        
        StreamResult streamResult = new StreamResult(bo);
        SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        // SAX2.0 ContentHandler.
        TransformerHandler hd = tf.newTransformerHandler();
        Transformer serializer = hd.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
        //serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
        serializer.setOutputProperty(OutputKeys.INDENT,"no");
        hd.setResult(streamResult);
        hd.startDocument();
        AttributesImpl atts = new AttributesImpl();
        // USERS tag.
        hd.startElement("","","MESSAGES",atts);
            atts.clear();
            atts.addAttribute("","","SEQ","CDATA", Integer.toString(seq));
            atts.addAttribute("","","OPTIONS","CDATA",Integer.toString(options));
            atts.addAttribute("","","CHECKSUM","CDATA",new String(checksum));
            hd.startElement("","","MESSAGE",atts);
            String str = new String(this.getData());
            hd.characters(str.toCharArray(), 0, str.length());
            hd.endElement("","","MESSAGE");
        hd.endElement("","","MESSAGES");
        hd.endDocument();
        
        return bo.toByteArray();
    }
    */
//    public byte[] toByteArray() {
//
//        ByteArrayOutputStream bo = null;
//        ObjectOutputStream so = null;
//        try {
//
//            checksum = checksum(data);
//
//            bo = new ByteArrayOutputStream();
//            so = new ObjectOutputStream(bo);
//            so.writeObject(this);
//            so.flush();
//            so.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.print("1");
//        System.out.println(bo.toByteArray());
//
//        return bo.toByteArray();
//    }
//
//        ByteArrayOutputStream bo = null;
//        ObjectOutputStream so = null;
//        try {
//
//            checksum = checksum(data);
//
//            bo = new ByteArrayOutputStream();
//            bo.write();
//            so.writeObject(this);
//            so.flush();
//            so.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.print("1");
//        System.out.println(bo.toByteArray());
//
//        return bo.toByteArray();
//    }
    
    
    
    
    protected byte[] checksum(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest checksum;
        checksum = MessageDigest.getInstance("MD5");
        //System.out.println(new String(checksum.digest()));
        checksum.update(b);
        //System.out.println("Checksumm..");
        return checksum.digest();
    }
    
    public int getSeq() {
        return seq;
    }
    public void	setSeq(int seq) {
        this.seq = seq;
    }
    
    /** Returns the data buffer. */
    public byte[] getData() {
        return this.data;
    }
    /** Set the data buffer for this packet. */
    public void	setData(byte[] buf) {
        if (buf != null)
            this.data = buf;
        else
            this.data[0] = 0;

        try {
            checksum = checksum(this.data);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }
    
    public byte getOptions() {
        return options;
    }
    
    public void setOptions(byte options) {
        this.options = options;
    }
    
    public byte[] getChecksum() {
        return checksum;
    }
    
    public void setChecksum(byte[] checksum) {
        this.checksum = checksum;
    }
    
    
    /**  Returns the length of the data to be sent or the length of the data received. */
    public int 	length() throws IOException, TransformerConfigurationException, SAXException, Exception {
        return toByteArray().length;
    }
    
    public boolean isSync() {
        
        return this.sync; // ((this.getOptions() & (1 << SYN_BIT)) > 0);
    }
    
    public void sync() {
        this.setOptions((byte) (this.getOptions() & ((1 << SYN_BIT))));
        this.sync = true;
    }
    
    
    public void ack() {
        this.setOptions((byte) (this.getOptions() & ((1 << ACK_BIT))));
        this.ack = true;
    }
    
    public boolean isAck() {
        return this.ack; //((this.getOptions() & (~(1 << ACK_BIT))) == (1 << ACK_BIT));
    }
    
    public boolean isOk() {
        return this.ok; //((this.getOptions() & (~(1 << OK_BIT))) == (1 << OK_BIT));
    }
    
    public boolean checkChecksum() throws NoSuchAlgorithmException {
         if ((checksum != null) && (data != null)) {
            return Arrays.equals(checksum, checksum(this.data));
            
        } else return false;
    }
    
    
    
    public DatagramPacket render(){
        //return new DatagramPacket(this.toByteArray(), this.getLength(), p.getAddress(), p.getPort());
        return null;
    }
    
    /** Set the length for this packet. */
    public void	length(int length) {
        
    }

    
    
    
    
}
