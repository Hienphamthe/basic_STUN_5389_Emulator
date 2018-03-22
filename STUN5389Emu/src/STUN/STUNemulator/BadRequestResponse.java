package STUN.STUNemulator;

import STUN.STUNattribute.BadRequestAttribute;
import STUN.STUNattribute.STUNAttribute;
import STUN.STUNrfc.Constants;
import STUN.STUNrfc.STUNHeader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author hienpham
 */
public class BadRequestResponse {
    public String BadRequestResponse(String trainsactionID) throws UnsupportedEncodingException {     
        String reasonPhrase = "BAD REQUEST";
        int attributeLength = 4 + reasonPhrase.getBytes("UTF-8").length;
        int messageLength = attributeLength + 4 + 1;
                
        STUNHeader header = new STUNHeader();
        BadRequestAttribute badRequestAttribute = new BadRequestAttribute();

        header.setMessageType(Constants.MESSAGE_TYPE_BINDING_RESPONSE_ERROR);;
        header.setMessageLength((short)messageLength);

        badRequestAttribute.setAttributeType(STUNAttribute.STUN_ERROR_CODE);
        badRequestAttribute.setAttributeLength((short)attributeLength);
        badRequestAttribute.setErrorClass(4);
        badRequestAttribute.setErrorCode(0);
        badRequestAttribute.setErrorReasonPhrase(reasonPhrase);

        String xmlErrorResponse = "\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
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
                "<ERROR-CODE>" +  "\n" +
                    "<Attribute-Type>" + badRequestAttribute.getAttributeType() +
                    "</Attribute-Type>\n" +
                    "<Attribute-Length>" + badRequestAttribute.getAttributeLength() +
                    "</Attribute-Length>\n" +
                    "<Reserved>" + badRequestAttribute.getPadding() +
                    "</Reserved>\n" +
                    "<Error-Class>" + badRequestAttribute.getErrorClass()+
                    "</Error-Class>\n" +
                    "<Error-Code>" + badRequestAttribute.getErrorCode() +
                    "</Error-Code>\n" +
                    "<Error-Reason-Phrase>" + badRequestAttribute.getErrorReasonPhrase() +
                    "</Error-Reason-Phrase>\n" +                  
                "</ERROR-CODE>\n" +               
            "</attribute>\n"+
            "</stun>\n";					

        xmlErrorResponse = xmlErrorResponse.trim().replaceFirst("^([\\W]+)<","<");
        return xmlErrorResponse; 
    }
}
