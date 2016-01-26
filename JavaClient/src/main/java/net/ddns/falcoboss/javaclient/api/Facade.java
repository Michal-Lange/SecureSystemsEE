package net.ddns.falcoboss.javaclient.api;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import net.ddns.falcoboss.common.Message;
import net.ddns.falcoboss.javaclient.rest.client.RestClient;

public class Facade extends Observable {

	private PropertyReader propertyReader;

	private RestClient restClient;

	private List<User> userList;

	private Thread reciveMessagesThread;

	private String myUsername;
	
	//

	public Facade() {
		propertyReader = new PropertyReader();
		restClient = new RestClient();
		userList = new LinkedList<User>();
		reciveMessagesThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Future<Response> reciveMessage = reciveMessage();
						Response response = reciveMessage.get();
						Message message = response.readEntity(Message.class);
						String sender = message.getSender();
						User senderUser = new User(sender);
						if (!userList.contains(senderUser)) {
							senderUser.setFistName("[___]");
							senderUser.setLastName("[___]");
							senderUser.setUserStatus(UserStatus.NOTAVAILABLE);
							userList.add(senderUser);
						}
						for (User user : userList) {
							if (user.equals(senderUser)) {
								user.addMessage(message);
								user.setUpdated(true);
								break;
							}
							
						}
						update();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	public Response login(String username, String password) throws IOException {
		this.myUsername = username;
		propertyReader.readPropertyValues();
		restClient.setWebTarget(propertyReader.getServiceUrl());
		restClient.setServiceKey(propertyReader.getServiceKey());
		Response response = restClient.login(username, password);
		return response;
	}

	public Response logout() {
		Response response = restClient.logout();
		reciveMessagesThread.interrupt();
		return response;
	}

	private Response sendMessage(Message message) {
		return restClient.sendMessage(message);
	}

	public Response sendMessage(String recipient, String text) {
		Message message = new Message();
		message.setSender(myUsername);
		message.setRecipient(recipient);
		message.setText(text);
		Response response = sendMessage(message);
		if (response.getStatus() == 200) {
			User recipientUser = new User(recipient);
			if (!userList.contains(recipientUser)) {
				userList.add(recipientUser);
			}
			for (User user : userList) {
				if (user.equals(recipientUser)) {
					user.addMessage(message);
				}
				break;
			}
			update();
		}
		return response;
	}

	private Future<Response> reciveMessage() {
		return restClient.reciveMessage();
	}

	public void startReciveMessages() {
		reciveMessagesThread.start();
	}
	
	public void requestNewKey(String username, String password){
		restClient.requestNewKey(username, password);
	}

	public void addContact(User user) throws JAXBException {
		userList.add(user);
		XmlSerializer.marshall(userList);
	}

	public void removeContact(User user) throws JAXBException {
		this.userList.remove(user);
		XmlSerializer.marshall(userList);
	}

	public String getAuthToken() {
		return restClient.getAuthToken().getString("auth_token");
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) throws JAXBException {
		this.userList = userList;
	}
	
	public void update()
	{
		setChanged();
		notifyObservers();
	}
}
