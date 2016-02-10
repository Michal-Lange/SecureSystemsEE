package net.ddns.falcoboss.registrationserver.usermanagement;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;

import net.ddns.falcoboss.common.transport.objects.UserTO;

@SuppressWarnings("serial")
@Entity
@Table(name="USERS")
@Cacheable(false)
public class User implements Serializable {
          
    @Id
    @Column(unique=true, nullable=false, length=128)
    private String username;
  
    @Column(nullable=false, length=128)
    private String firstName;
      
    @Column(nullable=false, length=128)
    private String lastName;
      
    @Column(nullable=false, length=128)
    private String password;
    
    @Column(nullable=false, length=128)
    private String serviceKey;
      
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable=false)
    private Date registeredOn;
          
    @ElementCollection(targetClass = Group.class)
    @CollectionTable(name = "USERS_GROUPS", 
                    joinColumns       = @JoinColumn(name = "username", nullable=false), 
                    uniqueConstraints = { @UniqueConstraint(columnNames={"username","groupname"}) } ) 
    @Enumerated(EnumType.STRING)
    @Column(name="groupname", length=64, nullable=false)
    private List<Group> groups;
     
    public User(){
         
    }
     
    public User(UserTO user){
         
        this.username		= user.getUsername();
        this.firstName		= user.getFirstName();
        this.lastName		= user.getLastName();        
        this.password		= user.getPassword();
        this.registeredOn	= new Date();
    }

        public String getFirstName() {
        return firstName;
    }
 
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
 
    public String getLastName() {
        return lastName;
    }
 
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
 
    public String getUsername() {
        return username;
    }
  
    public void setUsername(String email) {
        this.username = email;
    }
  
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
 
    public Date getRegisteredOn() {
        return registeredOn;
    }
 
    public void setRegisteredOn(Date registeredOn) {
        this.registeredOn = registeredOn;
    }
 
    public List<Group> getGroups() {
        return groups;
    }
 
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
 
    @Override
    public String toString() {
        return "User [username=" + username + ", firstName=" + firstName
                + ", lastName=" + lastName + ", password=" + password
                + ", registeredOn=" + registeredOn + ", groups=" + groups + "]";
    }

	public String getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}
}
