package STUN.STUNemulator;

import STUN.STUNrfc.Constants;
import STUN.STUNrfc.STUNHeader;
import STUN.STUNattribute.XORMappedAddressAttribute;
import STUN.STUNattribute.STUNAttribute;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 *
 * @author hienpham
 */
public class BindingSuccessResponse {
    private int clientPort;
    private String clientAddress;
    private String XORaddress;
    private String XORport;
    
    private void XORsocket(String clientAddress, int clientPort) {
        InetAddress ip;
        try { 
            // Convert IPaddress to byte array
            ip = InetAddress.getByName(clientAddress);
            byte[] bytes1 = ip.getAddress(); 
            
            // Convert port to byte array
            String Hex = Integer.toHexString(clientPort);
            int len3 = Hex.length();
            byte[] bytes2 = new byte[len3 / 2];
            for (int i = 0; i < len3; i += 2) {
                bytes2[i / 2] = (byte) ((Character.digit(Hex.charAt(i), 16) << 4)
                                     + Character.digit(Hex.charAt(i+1), 16));
            }
            
            // Convert magic cookies to byte array for addressXOR
            String cookiesAddress = "2112a442";
            int len1 = cookiesAddress.length();
            byte[] cookies1 = new byte[len1 / 2];
            for (int i = 0; i < len1; i += 2) {
                cookies1[i / 2] = (byte) ((Character.digit(cookiesAddress.charAt(i), 16) << 4)
                                     + Character.digit(cookiesAddress.charAt(i+1), 16));
            }
            
            // Convert magic cookies to byte array for portXOR
            String cookiesPort = "2112";
            int len2 = cookiesPort.length();
            byte[] cookies2 = new byte[len2 / 2];
            for (int i = 0; i < len2; i += 2) {
                cookies2[i / 2] = (byte) ((Character.digit(cookiesPort.charAt(i), 16) << 4)
                                     + Character.digit(cookiesPort.charAt(i+1), 16));
            }
            
            // XORing adddress
            byte[] bytes3 = new byte[4];
            for (int i = 0; i < bytes1.length; i++)
            {
                bytes3[i] = (byte) (bytes1[i] ^ cookies1[i]);
            }     
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes3) {
                sb.append(String.format("%02X ", b));
            }
            this.XORaddress = sb.toString().trim().replaceAll(" ", "");
            
            // XORing port
            byte[] bytes4 = new byte[2];
            for (int i = 0; i < bytes4.length; i++)
            {
                bytes4[i] = (byte) (bytes2[i] ^ cookies2[i]);
            }     
            StringBuilder sb2 = new StringBuilder();
            for (byte b : bytes4) {
                sb2.append(String.format("%02X ", b));
            }
            this.XORport = sb2.toString().trim().replaceAll(" ", "");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    
    public String SuccessResponse(String trainsactionID, int clientPort, String clientAddress) {

        short attributeMappedAddressLength = 8;
        short attributeXORMappedAddressLength = 8;
        short messageLength = (short) (attributeMappedAddressLength + attributeXORMappedAddressLength + 4);
        
        this.clientPort = clientPort;
        this.clientAddress = clientAddress;
        
        STUNHeader header = new STUNHeader();
        XORMappedAddressAttribute successResponseXOR = new XORMappedAddressAttribute();
        XORMappedAddressAttribute successResponseMAPPED = new XORMappedAddressAttribute();
        
        header.setMessageType(Constants.MESSAGE_TYPE_BINDING_RESPONSE_SUCCESS);;
        header.setMessageLength(messageLength);
        
        successResponseXOR.setAttributeType(STUNAttribute.XOR_MAPPED_ADDRESS);
        successResponseXOR.setFamily(Constants.IPv4);
        successResponseXOR.setAttributeLength(attributeXORMappedAddressLength);
        successResponseXOR.setPort(this.clientPort);
        successResponseXOR.setAddress(this.clientAddress);
        XORsocket(clientAddress, clientPort);
        successResponseXOR.setXORaddress(this.XORaddress);
        successResponseXOR.setXORport(this.XORport);
        
        successResponseMAPPED.setAttributeType(STUNAttribute.MAPPED_ADDRESS);
        successResponseMAPPED.setFamily(Constants.IPv4);
        successResponseMAPPED.setAttributeLength(attributeMappedAddressLength);
        successResponseMAPPED.setPort(this.clientPort);
        successResponseMAPPED.setAddress(this.clientAddress);
        
        String xmlSuccessResponse = "\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
            "<stun>\n" + 
                "<header>\n" + 
                    "<Message-Type>" + header.getMessageType() +
                    "</Message-Type>\n" + 
                    "<Message-Length>"  + header.getMessageLength() +
                    "</Message-Length>\n" + 
                    "<Magic-Cookie>"  + header.getMagicCookie() +
                    "</Magic-Cookie>\n" + 
                    "<Transaction-ID>"  + trainsactionID +
                    "</Transaction-ID>\n" + 
                "</header>\n" +
            "<attribute>\n" +
                "<XOR-MAPPED-ADDRESS>" + successResponseXOR.getAddress()+":"+successResponseXOR.getPort()+ "\n" +
                    "<Attribute-Type>" + successResponseXOR.getAttributeType() +
                    "</Attribute-Type>\n" +
                    "<Attribute-Length>" + successResponseXOR.getAttributeLength() +
                    "</Attribute-Length>\n" +
                    "<Reserved>" + successResponseXOR.getPadding() +
                    "</Reserved>\n" +
                    "<Protocol-Family>" + successResponseXOR.getFamily() +
                    "</Protocol-Family>\n" +
                    "<Port-XOR-d>" + successResponseXOR.getXORport() +
                    "</Port-XOR-d>\n" +
                    "<Port>" + successResponseXOR.getPort() +
                    "</Port>\n" +
                    "<IP-XOR-d>" + successResponseXOR.getXORaddress() +
                    "</IP-XOR-d>\n" +
                    "<IP>" + successResponseXOR.getAddress() +
                    "</IP>\n" +
                "</XOR-MAPPED-ADDRESS>\n" +
                "<MAPPED-ADDRESS>" + successResponseMAPPED.getAddress()+":"+successResponseMAPPED.getPort()+ "\n" +
                    "<Attribute-Type>" + successResponseMAPPED.getAttributeType() +
                    "</Attribute-Type>\n" +
                    "<Attribute-Length>" + successResponseMAPPED.getAttributeLength() +
                    "</Attribute-Length>\n" +
                    "<Reserved>" + "00" +
                    "</Reserved>\n" +
                    "<Protocol-Family>" + successResponseMAPPED.getFamily() +
                    "</Protocol-Family>\n" +
                    "<Port>" + successResponseMAPPED.getPort() +
                    "</Port>\n" +
                    "<IP>" + successResponseXOR.getAddress() +
                    "</IP>\n" +
                "</MAPPED-ADDRESS>\n" +
            "</attribute>\n"+
            "</stun>\n";					

        xmlSuccessResponse = xmlSuccessResponse.trim().replaceFirst("^([\\W]+)<","<");
        return xmlSuccessResponse; 
    }
}
