package net.ddns.falcoboss.javaclient.api;

public enum UserStatus {
	AVAILABLE("AVAILABLE"),
	NOTAVAILABLE("NOT AVAILABLE");
	private String statusText;
	UserStatus(String text){
		this.statusText = text;
	}
	
	public String toString(){
		return statusText;
		
	}
}
