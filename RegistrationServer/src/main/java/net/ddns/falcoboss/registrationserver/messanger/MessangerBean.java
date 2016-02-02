package net.ddns.falcoboss.registrationserver.messanger;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ws.rs.container.AsyncResponse;

import net.ddns.falcoboss.common.transport.objects.MessageTO;
import net.ddns.falcoboss.registrationserver.service.register.RegisterServiceHelper;

/**
 * Session Bean implementation class MessangerService
 */
@Singleton
@LocalBean
public class MessangerBean {

    private HashMap<String, AsyncResponse> listeners = new HashMap<String, AsyncResponse>();
	
	private List<MessageTO> recivedMessages = new LinkedList<MessageTO>();

	/**
     * Default constructor. 
     */
    public MessangerBean() {
        
    }

	public HashMap<String, AsyncResponse> getListeners() {
		return listeners;
	}

	public void setListeners(HashMap<String, AsyncResponse> listeners) {
		this.listeners = listeners;
	}

	public List<MessageTO> getRecivedMessages() {
		return recivedMessages;
	}

	public void setRecivedMessages(List<MessageTO> recivedMessages) {
		this.recivedMessages = recivedMessages;
	}
	
	public void sendMessage(MessageTO message) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean messageSend = false;
				message.setSendDate(new Date());
				synchronized (recivedMessages) {
					synchronized (listeners) {
						for (Entry<String, AsyncResponse> entry : listeners.entrySet()) {
							String listenerRecipient = entry.getKey();
							String messageRecipient = message.getRecipient();
							if (listenerRecipient.equals(messageRecipient)) {
								try {
									RegisterServiceHelper.send(entry.getValue(), message);
									messageSend = true;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						if (!messageSend) {
							recivedMessages.add(message);
							recivedMessages.sort(new MessageTO());
						}
					}
				}
			}
		}).start();
	}
	
	public void reciveMessage(String requestUsername, AsyncResponse asyncResponse)
	{
		boolean messageFound = false;
		synchronized (recivedMessages) {
			for (MessageTO message : recivedMessages) {
				if (message.getRecipient().equals(requestUsername)) {
					RegisterServiceHelper.send(asyncResponse, message);
					recivedMessages.remove(message);
					messageFound = true;
					break;
				}
			}
			if (!messageFound) {
				synchronized (listeners) {
					listeners.put(requestUsername, asyncResponse);
				}
			}
		}
	}

}
