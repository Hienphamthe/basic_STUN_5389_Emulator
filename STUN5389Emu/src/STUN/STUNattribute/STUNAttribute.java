package STUN.STUNattribute;

public abstract class STUNAttribute {
    /* STADARD ATTRIBUTES */
	static public final String MAPPED_ADDRESS = "0x0001";
        
        /* Constant XOR mapped address */
        static public String XOR_MAPPED_ADDRESS = "0x0020";
        
        /* Constant realm */
        static public String REALM = "0x0014";
        
        /* Constant nonce */
        static public String NONCE = "0x0015";
        
        /* Constant error code*/
        static public String STUN_ERROR_CODE = "0x0009";
        
        /* Constant unknown attribute*/
	static public String STUN_UNKNOWN_ATTRIBUTES = "0x000a";
	
	private String attributeType;
	private short attributeLength;
	
	public String getAttributeType() {
		return attributeType;
	}
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}
	public short getAttributeLength() {
		return attributeLength;
	}
	public void setAttributeLength(short attributeLength) {
		this.attributeLength = attributeLength;
	}
}
