package net.ddns.falcoboss.javaclient.api;

import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Future;

import javax.swing.SwingUtilities;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import net.ddns.falcoboss.common.KeyHelper;
import net.ddns.falcoboss.common.KeyPairBase64TO;
import net.ddns.falcoboss.common.Message;
import net.ddns.falcoboss.common.PublicKeyCryptography;
import net.ddns.falcoboss.javaclient.rest.client.RestClient;

public class Facade extends Observable {

	private PropertyReader propertyReader;

	private RestClient restClient;

	private List<User> userList;

	private Thread reciveMessagesThread;

	private String myUsername;
	
	private RSAPrivateKey privateKey;
	
	private RSAPublicKey publicKey;
	
	private String lastSignature;
	
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
						if (response.getStatus() == 200){
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
		new Thread(new Runnable() {
			public void run() {
				try {
					Future<Response> futureResponse = restClient.requestNewKey(username, password);
					Response response = futureResponse.get();
					if (response.getStatus() == 200){
						KeyPairBase64TO keyPairBase64TO = response.readEntity(KeyPairBase64TO.class);
						String modulusBase64 = keyPairBase64TO.getModulus();
						String privateExponentBase64 = keyPairBase64TO.getPrivateExponent();
						String publicExponentBase64 = keyPairBase64TO.getPublicExponent();
						privateKey =  (RSAPrivateKey) KeyHelper.getPrivateKeyFromBase64ExponentAndModulus(privateExponentBase64, modulusBase64);
						publicKey = (RSAPublicKey) KeyHelper.getPublicKeyFromBase64ExponentAndModulus(publicExponentBase64, modulusBase64);
						update();
					}
				}
				catch(Exception e){

				}
			}
		}).start();
	}
	
	public void signFileHash(String fileHashHexString) {
		new Thread(new Runnable() {
			public void run() {
				try {
					BigInteger fileHash = new BigInteger(fileHashHexString, 16);
					BigInteger partialySignedHash = PublicKeyCryptography.signFileHash(fileHash, privateKey);
					String fileHashBase64String = KeyHelper.getBase64StringFromBigInteger(fileHash);
					String partialySignedHashBase64String = KeyHelper.getBase64StringFromBigInteger(partialySignedHash);
					Future<Response> futureResponse = restClient.signFile(partialySignedHashBase64String, fileHashBase64String);
					Response response = futureResponse.get();
					if (response.getStatus() == 200){
						String completeSignatureBase64String = response.readEntity(String.class);
						lastSignature = completeSignatureBase64String;
						update();
					}
					
				}
				catch(Exception e){

				}
			}
		}).start();
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

	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(RSAPrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public RSAPublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(RSAPublicKey publicKey) {
		this.publicKey = publicKey;
	}

	
}
