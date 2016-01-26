package net.ddns.falcoboss.common;

import java.util.Comparator;
import java.util.Date;

public class Message implements Comparator<Object> {
	private String text;
	private Date sendDate;
	private Date receiptDate;
	private String sender;
	private String recipient;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}
	
	@Override
	public int compare(Object o1, Object o2) {
	     Date d1 = ((Message) o1).getSendDate();
	     Date d2 = ((Message) o2).getSendDate();
	       if (d1.after(d2)) {
	           return 1;
	       } else if (d1.before(d2)){
	           return -1;
	       } else if (d1.equals(d2)){
	           return 0;
	       }
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((receiptDate == null) ? 0 : receiptDate.hashCode());
		result = prime * result + ((recipient == null) ? 0 : recipient.hashCode());
		result = prime * result + ((sendDate == null) ? 0 : sendDate.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Message)) {
			return false;
		}
		Message other = (Message) obj;
		if (receiptDate == null) {
			if (other.receiptDate != null) {
				return false;
			}
		} else if (!receiptDate.equals(other.receiptDate)) {
			return false;
		}
		if (recipient == null) {
			if (other.recipient != null) {
				return false;
			}
		} else if (!recipient.equals(other.recipient)) {
			return false;
		}
		if (sendDate == null) {
			if (other.sendDate != null) {
				return false;
			}
		} else if (!sendDate.equals(other.sendDate)) {
			return false;
		}
		if (sender == null) {
			if (other.sender != null) {
				return false;
			}
		} else if (!sender.equals(other.sender)) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		return true;
	}
}
