package net.ddns.falcoboss.javaclient.api;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import net.ddns.falcoboss.common.PropertyReader;
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
	
	private BigInteger verificationFileHash;
	
	private BigInteger verificationSignature;
	
	private RSAPublicKey verificationRSAPublicKey;
	
	private Queue<Response> errorResponse = new LinkedList<Response>();
	
	public BigInteger getVerificationFileHash() {
		return verificationFileHash;
	}

	public void setVerificationFileHash(BigInteger verificationFileHash) {
		this.verificationFileHash = verificationFileHash;
	}

	public BigInteger getVerificationSignature() {
		return verificationSignature;
	}

	public void setVerificationSignature(BigInteger verificationSignature) {
		this.verificationSignature = verificationSignature;
	}

	public RSAPublicKey getVerificationRSAPublicKey() {
		return verificationRSAPublicKey;
	}

	public void setVerificationRSAPublicKey(RSAPublicKey verificationRSAPublicKey) {
		this.verificationRSAPublicKey = verificationRSAPublicKey;
	}

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

	public String getAuthToken() {
		return restClient.getAuthToken().getString("auth_token");
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) throws JAXBException {
		this.userList = userList;
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



	public Facade() {
		propertyReader = new PropertyReader();
		restClient = new RestClient();
		userList = new LinkedList<User>();
		try {
			privateKey = (RSAPrivateKey) openPrivateKey("private.key");
			publicKey = (RSAPublicKey) openPublicKey("public.key");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		reciveMessagesThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						Future<Response> reciveMessage = reciveMessage();
						Response response = reciveMessage.get();
						if (response.getStatus() == 200) {
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
						} else {
							synchronized (errorResponse) {
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

	public void update() {
		setChanged();
		notifyObservers();
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
		} else {
			synchronized (errorResponse) {
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

	public void requestNewKey(String username, String password) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Future<Response> futureResponse = restClient.requestNewKey(username, SHA512.hashText(password));
					Response response = futureResponse.get();
					if (response.getStatus() == 200) {
						KeyPairTO keyPairBase64TO = response.readEntity(KeyPairTO.class);
						String modulusBase64 = keyPairBase64TO.getModulus();
						String privateExponentBase64 = keyPairBase64TO.getPrivateExponent();
						String publicExponentBase64 = keyPairBase64TO.getPublicExponent();
						privateKey = (RSAPrivateKey) KeyHelper
								.getPrivateKeyFromBase64ExponentAndModulus(privateExponentBase64, modulusBase64);
						publicKey = (RSAPublicKey) KeyHelper
								.getPublicKeyFromBase64ExponentAndModulus(publicExponentBase64, modulusBase64);
						savePrivateKey("private.key",privateKey);
						savePublicKey("public.key",publicKey);
					} else {
						synchronized (errorResponse) {
							errorResponse.add(response);
						}
					}
					update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void signFileHash(String fileHashHexString, String filename) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BigInteger fileHash = new BigInteger(fileHashHexString, 16);
					BigInteger partialySignedHash = PublicKeyCryptography.signFileHash(fileHash, privateKey);
					String fileHashBase64String = KeyHelper.getBase64StringFromBigInteger(fileHash);
					String partialySignedHashBase64String = KeyHelper.getBase64StringFromBigInteger(partialySignedHash);
					Future<Response> futureResponse = restClient.signFile(partialySignedHashBase64String,
							fileHashBase64String);
					Response response = futureResponse.get();
					if (response.getStatus() == 200) {
						String completeSignatureBase64String = response.readEntity(String.class);
						setLastSignature(completeSignatureBase64String);
						BigInteger completeSignature = KeyHelper.getBigIntegerFromBase64String(completeSignatureBase64String);
						saveSignature(completeSignature.toByteArray(),filename);
					} else {
						synchronized (errorResponse) {
							errorResponse.add(response);
						}
					}
					update();
				} catch (Exception e) {
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

	public void savePrivateKey(String filename, PrivateKey privateKey) throws IOException {
		if (privateKey != null) {
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file, false);
			fos.write(pkcs8EncodedKeySpec.getEncoded());
			fos.close();
		}
	}

	public void savePublicKey(String filename, PublicKey publicKey) throws IOException {
		if (publicKey != null) {
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file, false);
			fos.write(x509EncodedKeySpec.getEncoded());
			fos.close();
		}
	}

	public PrivateKey openPrivateKey(String filename) throws Exception {
		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	public PublicKey openPublicKey(String filename) throws Exception {
		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}
	
	public void saveSignature(byte[] signature, String filename) throws IOException {
		filename += "." + new Timestamp(new Date().getTime()) + ".sig";
		filename = filename.replace(" ", "_").replace(",", "_").replace(":", "_");
		File file = new File(filename);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file, false);
		fos.write(signature);
		fos.close();
	}
	
	public byte[] loadSignature(String filename) throws IOException {
		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] signatureBytes = new byte[(int) f.length()];
		dis.readFully(signatureBytes);
		dis.close();
		return signatureBytes;
	}
	
	public boolean verifyFileSignature(BigInteger fileHash, BigInteger fileSignature, RSAPublicKey rsaPublicKey) {
		BigInteger publicExponent = publicKey.getPublicExponent();
		BigInteger modulus = publicKey.getModulus();
		BigInteger inverted = fileSignature.modPow(publicExponent, modulus);
		return inverted.equals(fileHash);
	}
}
