package net.ddns.falcoboss.javaclient.api;

public enum UserStatus {
	AVAILABLE(""),
	NOTAVAILABLE("");
	private String statusText;
	UserStatus(String text){
		this.statusText = text;
	}
	
	public String toString(){
		return statusText;
		
	}
}
