package net.ddns.falcoboss.common.transport.objects;

public class UserTO {
	 
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String newPassword;
     
    public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
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
      
    @Override
    public String toString() {
        return "User [email=" + this.username + ", fName=" + this.firstName
                + ", lName=" + this.lastName + ", password=" + this.password + "]";
    }
     
}
