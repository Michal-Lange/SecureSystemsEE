package net.ddns.falcoboss.javaclient.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.ws.rs.core.Response;

import net.ddns.falcoboss.common.Message;
import net.ddns.falcoboss.javaclient.api.Facade;
import net.ddns.falcoboss.javaclient.api.User;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class MessageWindow extends JFrame implements Observer{

	private JPanel contentPane;
	private User user;
	private Facade facade;
	private JTextArea textAreaSendMessage;
	private JTextArea textAreaConversation;
	private MessageWindow messageWindow;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MessageWindow frame = new MessageWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MessageWindow() {
		addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				user.setUpdated(false);
			}
			public void windowLostFocus(WindowEvent e) {
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				updateConversation();
				user.addObserver(messageWindow);
				user.setUpdated(false);
			}
		});
		messageWindow = this;
		setBounds(100, 100, 550, 405);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panelMain = new JPanel();
		
		JPanel panelConversation = new JPanel();
		
		textAreaConversation = new JTextArea();
		textAreaConversation.setEditable(false);
		textAreaConversation.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GroupLayout gl_panelConversation = new GroupLayout(panelConversation);
		gl_panelConversation.setHorizontalGroup(
			gl_panelConversation.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelConversation.createSequentialGroup()
					.addContainerGap()
					.addComponent(textAreaConversation, GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panelConversation.setVerticalGroup(
			gl_panelConversation.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelConversation.createSequentialGroup()
					.addContainerGap()
					.addComponent(textAreaConversation, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
					.addContainerGap())
		);
		panelConversation.setLayout(gl_panelConversation);
		
		JPanel panelSendMessage = new JPanel();
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		
		textAreaSendMessage = new JTextArea();
		textAreaSendMessage.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GroupLayout gl_panelSendMessage = new GroupLayout(panelSendMessage);
		gl_panelSendMessage.setHorizontalGroup(
			gl_panelSendMessage.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSendMessage.createSequentialGroup()
					.addContainerGap()
					.addComponent(textAreaSendMessage, GroupLayout.PREFERRED_SIZE, 416, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panelSendMessage.setVerticalGroup(
			gl_panelSendMessage.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelSendMessage.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelSendMessage.createParallelGroup(Alignment.TRAILING)
						.addComponent(textAreaSendMessage, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
						.addComponent(btnSend, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		panelSendMessage.setLayout(gl_panelSendMessage);
		GroupLayout gl_panelMain = new GroupLayout(panelMain);
		gl_panelMain.setHorizontalGroup(
			gl_panelMain.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelMain.createSequentialGroup()
					.addGroup(gl_panelMain.createParallelGroup(Alignment.TRAILING)
						.addComponent(panelConversation, GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
						.addComponent(panelSendMessage, GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panelMain.setVerticalGroup(
			gl_panelMain.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelMain.createSequentialGroup()
					.addComponent(panelConversation, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelSendMessage, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE))
		);
		panelMain.setLayout(gl_panelMain);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panelMain, GroupLayout.PREFERRED_SIZE, 524, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(panelMain, GroupLayout.PREFERRED_SIZE, 356, Short.MAX_VALUE)
		);
		contentPane.setLayout(gl_contentPane);
	}

	public void setFacade(Facade facade) {
		this.facade = facade;
		
	}

	public void setUser(User selectedUser) {
		this.user = selectedUser;
		this.setTitle(this.user.toString());
		
	}
	
	private void sendMessage(){
		Response response = facade.sendMessage(user.getUsername(), textAreaSendMessage.getText());
		if(response.getStatus() == 200){
			textAreaSendMessage.setText("");
		}
	}
	
	public void updateConversation(){
		String conversation = "";
		for(Message message: user.getMessages())
		{
			conversation += message.getSender() + ":\n" + message.getText() +"\n\n";
		}
		textAreaConversation.setText(conversation);
	}

	@Override
	public void update(Observable o, Object arg) {
		updateConversation();
	}
}
