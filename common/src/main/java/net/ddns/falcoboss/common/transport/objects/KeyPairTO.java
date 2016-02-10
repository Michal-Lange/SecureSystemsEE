package net.ddns.falcoboss.common.transport.objects;

public class KeyPairTO {

	private String privateExponent;
	private String publicExponent;
    private String modulus;
	
    public KeyPairTO(){
    }
    
    public KeyPairTO(String privateExponent, String publicExponent, String modulus) {
    	this.privateExponent = privateExponent;
    	this.publicExponent = publicExponent;
    	this.modulus = modulus;
    }
    
    public String getPrivateExponent() {
		return privateExponent;
	}
    
	public void setPrivateExponent(String privateExponent) {
		this.privateExponent = privateExponent;
	}
	
	public String getPublicExponent() {
		return publicExponent;
	}
	
	public void setPublicExponent(String publicExponent) {
		this.publicExponent = publicExponent;
	}
	
	public String getModulus() {
		return modulus;
	}
	
	public void setModulus(String modulus) {
		this.modulus = modulus;
	}
	
    
    
}
