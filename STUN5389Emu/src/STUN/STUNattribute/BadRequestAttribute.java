package STUN.STUNattribute;

public class BadRequestAttribute extends STUNAttribute {
    private String padding = "0x0000";
	private int errorClass;
	private int errorCode;
        private String errorPhrase;
	
	public String getPadding() {
		return padding;
	}
        public void setErrorClass(int errorType) {
                this.errorClass = errorType;
        }
        public int getErrorClass() {
                return errorClass;
        }
        public void setErrorCode(int errorCode) {
                this.errorCode = errorCode;
        }
        public int getErrorCode() {
                return errorCode;
        }
        public void setErrorReasonPhrase(String errorPhrase) {
            this.errorPhrase = errorPhrase;
        }
        public String getErrorReasonPhrase() {
                return errorPhrase;
        }
        
}
