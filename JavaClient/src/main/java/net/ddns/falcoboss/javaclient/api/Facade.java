package net.ddns.falcoboss.javaclient.api;

import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import net.ddns.falcoboss.common.cryptography.KeyHelper;
import net.ddns.falcoboss.common.cryptography.PublicKeyCryptography;
import net.ddns.falcoboss.common.cryptography.SHA512;
import net.ddns.falcoboss.common.transport.objects.KeyPairTO;
import net.ddns.falcoboss.common.transport.objects.MessageTO;
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
	
	private Queue<Response> errorResponse = new LinkedList<Response>();
	
	public String getLastSignature() {
		return lastSignature;
	}

	public void setLastSignature(String lastSignature) {
		this.lastSignature = lastSignature;
	}
	
	public Queue<Response> getErrorResponse() {
		return errorResponse;
	}

	public void setErrorResponse(Queue<Response> errorResponse) {
		this.errorResponse = errorResponse;
	}

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
							MessageTO message = response.readEntity(MessageTO.class);
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
						}
						else{
							synchronized(errorResponse){
								errorResponse.add(response);
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

	private Response sendMessage(MessageTO message) {
		return restClient.sendMessage(message);
	}

	public Response sendMessage(String recipient, String text) {
		MessageTO message = new MessageTO();
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
		}
		else{
			synchronized(errorResponse){
				errorResponse.add(response);
			}
		}
		update();
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
					Future<Response> futureResponse = restClient.requestNewKey(username, SHA512.hashText(password));
					Response response = futureResponse.get();
					if (response.getStatus() == 200){
						KeyPairTO keyPairBase64TO = response.readEntity(KeyPairTO.class);
						String modulusBase64 = keyPairBase64TO.getModulus();
						String privateExponentBase64 = keyPairBase64TO.getPrivateExponent();
						String publicExponentBase64 = keyPairBase64TO.getPublicExponent();
						privateKey = (RSAPrivateKey) KeyHelper.getPrivateKeyFromBase64ExponentAndModulus(privateExponentBase64, modulusBase64);
						publicKey = (RSAPublicKey) KeyHelper.getPublicKeyFromBase64ExponentAndModulus(publicExponentBase64, modulusBase64);
					}
					else
					{	
						synchronized(errorResponse){
							errorResponse.add(response);
						}
					}
					update();
				}
				catch(Exception e){
					e.printStackTrace();
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
						setLastSignature(completeSignatureBase64String);
					}
					else
					{	
						synchronized(errorResponse){
							errorResponse.add(response);
						}
					}
					update();
				}
				catch(Exception e){
					e.printStackTrace();
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
