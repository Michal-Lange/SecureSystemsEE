package net.ddns.falcoboss.common;

public class KeyPairBase64TO {

	private String privateExponent;
	private String publicExponent;
    private String modulus;
	
    public KeyPairBase64TO(){
    }
    
    public KeyPairBase64TO(String privateExponent, String publicExponent, String modulus) {
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
	
	public String getMonulus() {
		return modulus;
	}
	
	public void setMonulus(String monulus) {
		this.modulus = monulus;
	}
	
    
    
}
