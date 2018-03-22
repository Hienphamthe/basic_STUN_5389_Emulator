package STUN.STUNrfc;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class STUNHeader {
    private String messageType;
    private short messageLength;
    private String magicCookie = Constants.MAGIC_COOKIE;
    private BigInteger transactionId;

    public String getMessageType() {
            return messageType;
    }
    public void setMessageType(String messageType) {
            this.messageType = messageType;
    }
    public short getMessageLength() {
            return messageLength;
    }
    public void setMessageLength(short messageLength) {
            this.messageLength = messageLength;
    }
    public String getMagicCookie() {
            return magicCookie;
    }
    public void setMagicCookie(String magicCookie) {
            this.magicCookie = magicCookie;
    }
    public BigInteger getTransactionId() {
            return transactionId;
    }
    public void setTransactionId(BigInteger transactionId) {
            this.transactionId = transactionId;
    }
}
