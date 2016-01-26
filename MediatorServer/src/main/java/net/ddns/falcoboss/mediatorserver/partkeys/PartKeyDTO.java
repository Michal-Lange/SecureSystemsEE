package net.ddns.falcoboss.mediatorserver.partkeys;

public class PartKeyDTO {
	
	private String serviceKey;
    private String privateExponent;
    private String publicModulus;
    private String publicExponent;
    
    public String getServiceKey() {
		return serviceKey;
	}
    
	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
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
	
	public String getPublicModulus() {
		return publicModulus;
	}
	
	public void setPublicModulus(String publicModulus) {
		this.publicModulus = publicModulus;
	}
	
}
