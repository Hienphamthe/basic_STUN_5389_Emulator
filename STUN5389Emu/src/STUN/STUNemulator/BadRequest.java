package STUN.STUNemulator;

import STUN.STUNrfc.Constants;
import STUN.STUNrfc.STUNHeader;
import STUN.STUNrfc.Util;

/**
 *
 * @author hienpham
 */
public class BadRequest {
    public String StunID = "";
    public String BadRequest()
    {           
        short messageLength = 0;
        
        STUNHeader header = new STUNHeader();
        header.setMessageType(Constants.MESSAGE_TYPE_BINDING_REQUEST);
        header.setTransactionId(Util.generateTrxId());
        header.setMessageLength(messageLength);
        
        // Convert byte array to string
        byte[] bytesTrxID = new byte[12];
        bytesTrxID = header.getTransactionId().toByteArray();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytesTrxID) {
            sb.append(String.format("%02X ", b));
        }
        this.StunID = sb.toString().trim().replaceAll(" ", "");
        //System.out.println(StunID);
        
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
            "<stun>\n" + 
            "<header>\n" + 
            "<Message-Type>" + header.getMessageType() +
            "</Message-Type>\n" + 
            "<Message-Length>"  + header.getMessageLength() +
            "</Message-Length>\n" + 
            "<Magic-Cookie>"  + "0x211210442" +
            "</Magic-Cookie>\n" + 
            "<Transaction-ID>"  + StunID +
            "</Transaction-ID>\n" + 
            "</header>\n" +   
            "</stun>\n";
        
        xmlRequest = xmlRequest.trim().replaceFirst("^([\\W]+)<","<");
        return xmlRequest; 
    }
}
