package net.ddns.falcoboss.javaclient.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import net.ddns.falcoboss.common.transport.objects.MessageTO;

@XmlRootElement
public class User extends Observable{
	private String username;
	private String fistName;
	private String lastName;
	private String status;
	private Boolean updated;
	private UserStatus userStatus;
	private List<MessageTO> messages;
	
	public List<MessageTO> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageTO> messages) {
		this.messages = messages;
	}
	
	public void addMessage(MessageTO message){
		messages.add(message);
		setChanged();
	    notifyObservers();
	}

	public User(){
		messages = new LinkedList<MessageTO>();
	}
	
	public User(String username){
		messages = new LinkedList<MessageTO>();
		this.username = username;
	}
	
	@XmlAttribute
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@XmlAttribute
	public String getFistName() {
		return fistName;
	}
	
	public void setFistName(String fistName) {
		this.fistName = fistName;
	}
	
	@XmlAttribute
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Boolean isUpdated() {
		return updated;
	}
	public void setUpdated(Boolean update) {
		this.updated = update;
	}
	
	public UserStatus getUserStatus() {
		return userStatus;
	}
	
	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}
	@Override
	public boolean equals (Object o) {
		 if(o instanceof User){
			 User toCompare = (User) o;
			 return this.username.equals(toCompare.getUsername());
		 }
		 return false;
	}
	@Override
	public String toString(){
		return fistName + " " + lastName + " (" + username + ")";
	}
}