package STUN.STUNrfc;

public class Constants {
	/*Size of the Transaction ID field in bits */
	static public int TRX_ID_SIZE = 96;
	
	/* Size of the STUN request header in bytes */
	static public int HEADER_SIZE = 20;
	
        /* HEADER MESSAGE TYPE: -/Indication  */
	static public String MESSAGE_TYPE_INDICATION = "0x0010";
        
	/* HEADER MESSAGE TYPE: Binding/Request  */
	static public String MESSAGE_TYPE_BINDING_REQUEST = "0x0001";
	
	/* HEADER MESSAGE TYPE: Binding/Response Success */
	static public String MESSAGE_TYPE_BINDING_RESPONSE_SUCCESS = "0x0101";
	
	/* HEADER MESSAGE TYPE: Binding/Response Error */
	static public String MESSAGE_TYPE_BINDING_RESPONSE_ERROR = "0x0111";
	
	/* Constant magic cookie in the header */
	static public String MAGIC_COOKIE = "0x2112a442" ;

        /* 400 (Bad Request): The request was malformed. 
	 * The client should not retry the request without
	 * modification from the previous attempt.
	 */
        static public int STUN_ERROR_BAD_REQUEST = 400;
        /* 401 (Unauthorized): The Binding Request did not
	 * contain a MESSAGE- INTEGRITY attribute.
	 */
        static public int STUN_ERROR_UNAUTHORIZED = 401;
        /* 420 (Unknown Attribute): The server did not
	 * understand a mandatory attribute in the request.
	 */
        static public int STUN_ERROR_UNKNOWN_ATTRIBUTE = 420;
        /* 430 (Stale Credentials): The Binding Request
	 * did contain a MESSAGE- INTEGRITY attribute,
	 * but it used a shared secret that has expired.
	 * The client should obtain a new shared secret and try again.
	 */
        static public int STUN_ERROR_STALE_CREDENTIALS = 430;
        /* 431 (Integrity Check Failure): The Binding Request
	 * contained a MESSAGE-INTEGRITY attribute, but the HMAC
	 * failed verification.  This could be a sign of
	 * a potential attack, or client implementation error.
	 */
        static public int STUN_ERROR_INTEGRITY_CHECK_FAIL = 431;
        /* 432 (Missing Username): The Binding Request contained
	 * a MESSAGE- INTEGRITY attribute, but not a USERNAME attribute.
	 * Both must be present for integrity checks.
	 */
        static public int STUN_ERROR_MISSING_USERNAME = 432;
        /* 433 (Use TLS): The Shared Secret request has to be sent
	 * over TLS, but was not received over TLS.
	 */
        static public int STUN_ERROR_USE_TLS = 433;
        /* 500 (Server Error): The server has suffered a temporary error.
	 * The client should try again.
	 */
        static public int STUN_ERROR_SERVER_ERROR = 500;
        /* 600 (Global Failure:) The server is refusing to fulfill
	 * the request.
	 */
        static public int STUN_ERROR_GLOBAL_FAILURE = 600;
	
	static public String IPv4 = "0x01";
	static public String IPv6 = "0x02";
}
