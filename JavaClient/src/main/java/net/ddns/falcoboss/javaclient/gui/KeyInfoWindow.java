package net.ddns.falcoboss.javaclient.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import net.ddns.falcoboss.common.cryptography.KeyHelper;
import net.ddns.falcoboss.javaclient.api.Facade;

public class KeyInfoWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 270367931414246477L;
	private JPanel contentPane;
	private Facade facade;
	private JTextArea textAreaPrivateExponent;
	private JTextArea textAreaPublicExponent;
	private JTextArea textAreaModulus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					KeyInfoWindow frame = new KeyInfoWindow();
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
	public KeyInfoWindow() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				loadKeysInfo();
			}
			@Override
			public void windowActivated(WindowEvent e) {
				loadKeysInfo();
			}
		});
		setBounds(100, 100, 450, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
		);
		
		JLabel lblPrivateExponent = new JLabel("Private Exponent:");
		
		textAreaPublicExponent = new JTextArea();
		textAreaPublicExponent.setLineWrap(true);
		textAreaPublicExponent.setEditable(false);
		
		JLabel lblPublicExponent = new JLabel("Public Exponent:");
		
		textAreaPrivateExponent = new JTextArea();
		textAreaPrivateExponent.setEditable(false);
		textAreaPrivateExponent.setLineWrap(true);
		
		JLabel lblModulus = new JLabel("Common Modulus:");
		
		textAreaModulus = new JTextArea();
		textAreaModulus.setLineWrap(true);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(textAreaModulus, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
						.addComponent(textAreaPublicExponent, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
						.addComponent(lblPrivateExponent)
						.addComponent(textAreaPrivateExponent, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
						.addComponent(lblModulus)
						.addComponent(lblPublicExponent))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblPrivateExponent)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textAreaPrivateExponent, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblPublicExponent)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textAreaPublicExponent, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblModulus)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textAreaModulus, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(76, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);
	}
	
	public void loadKeysInfo() {
		if(facade.getPrivateKey() != null && facade.getPublicKey() != null)
		{
			textAreaPrivateExponent.setText(KeyHelper.getBase64StringFromBigInteger(facade.getPrivateKey().getPrivateExponent()));
			textAreaPublicExponent.setText(KeyHelper.getBase64StringFromBigInteger(facade.getPublicKey().getPublicExponent()));
			textAreaModulus.setText(KeyHelper.getBase64StringFromBigInteger(facade.getPrivateKey().getModulus()));
		}
	}

	public Facade getFacade() {
		return facade;
	}

	public void setFacade(Facade facade) {
		this.facade = facade;
	}
	public JTextArea getTextAreaPrivateExponent() {
		return textAreaPrivateExponent;
	}
	public JTextArea getTextAreaPublicExponent() {
		return textAreaPublicExponent;
	}
	public JTextArea getTextAreaModulus() {
		return textAreaModulus;
	}
}
