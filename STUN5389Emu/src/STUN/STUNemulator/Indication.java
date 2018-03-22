package STUN.STUNemulator;

import STUN.STUNrfc.STUNHeader;
import STUN.STUNrfc.Constants;
import STUN.STUNrfc.Util;
/**
 *
 * @author hienpham
 */
public class Indication {
    
    public String Indication()
    {           
        short messageLength = 0;
        
        STUNHeader header = new STUNHeader();
        header.setMessageType(Constants.MESSAGE_TYPE_INDICATION);
        header.setTransactionId(Util.generateTrxId());
        header.setMessageLength(messageLength);
        
        // Convert byte array to string
        byte[] bytesTrxID = new byte[12];
        bytesTrxID = header.getTransactionId().toByteArray();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytesTrxID) {
            sb.append(String.format("%02X ", b));
        }
        String transactionID = sb.toString().trim().replaceAll(" ", "");
        
        String xmlIndication = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
            "<stun>\n" + 
            "<header>\n" + 
            "<Message-Type>" + header.getMessageType() +
            "</Message-Type>\n" + 
            "<Message-Length>"  + header.getMessageLength() +
            "</Message-Length>\n" + 
            "<Magic-Cookie>"  + header.getMagicCookie() +
            "</Magic-Cookie>\n" + 
            "<Transaction-ID>"  + transactionID +
            "</Transaction-ID>\n" + 
            "</header>\n" +   
            "</stun>\n";
        
        xmlIndication = xmlIndication.trim().replaceFirst("^([\\W]+)<","<");
        return xmlIndication; 
    }
}
