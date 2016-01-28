package net.ddns.falcoboss.mediatorserver.partkeys;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

import net.ddns.falcoboss.common.transport.objects.PartKeyDT;

@SuppressWarnings("serial")
@Entity
@Table(name="PARRTKEYS")
@Cacheable(false)
public class PartKey implements Serializable{
	@Id
    @Column(unique=true, nullable=false, length=128)
    private String serviceKey;

	@Column(nullable=false, length=512)
    private String privateExponent;
	
	@Column(nullable=false, length=512)
    private String publicExponent;
	
	@Column(nullable=false, length=512)
    private String publicModulus;

	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable=false)
    private Date createdOn;
	
	public PartKey(){
	}
	
	public PartKey(PartKeyDT partKey){
		
		this.serviceKey = partKey.getServiceKey();
		this.privateExponent = partKey.getPrivateExponent();
		this.publicExponent = partKey.getPublicExponent();
		this.publicModulus = partKey.getPublicModulus();
		this.createdOn = new Date();
	}
	
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

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
}
