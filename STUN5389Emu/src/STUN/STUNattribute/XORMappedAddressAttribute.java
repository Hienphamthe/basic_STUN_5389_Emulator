package STUN.STUNattribute;

public class XORMappedAddressAttribute extends STUNAttribute {
    private String padding = "0X00";
    private String family;
    private int port;
    private String address;
    private String XORaddress;
    private String XORport;

    public String getXORaddress() {
            return XORaddress;
    }
    public void setXORaddress(String XORaddress) {
            this.XORaddress = XORaddress;
    }
    public String getXORport() {
            return XORport;
    }
    public void setXORport(String XORport) {
            this.XORport = XORport;
    }
    public String getPadding() {
            return padding;
    }
    public String getFamily() {
            return family;
    }
    public void setFamily(String family) {
            this.family = family;
    }
    public int getPort() {
            return port;
    }
    public void setPort(int port) {
            this.port = port;
    }
    public String getAddress() {
            return address;
    }
    public void setAddress(String address) {
            this.address = address;
    }	
}
