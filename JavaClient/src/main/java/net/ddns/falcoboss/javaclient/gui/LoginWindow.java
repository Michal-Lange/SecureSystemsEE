package net.ddns.falcoboss.javaclient.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.ws.rs.core.Response;

import net.ddns.falcoboss.javaclient.api.Facade;

@SuppressWarnings("serial")
public class LoginWindow extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldLogin;
	private JTextField textFieldPassword;
	private JLabel lblLoginStatus;
	private JButton buttonLogin;
	private Facade facade;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginWindow frame = new LoginWindow();
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
	public LoginWindow() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 340, 160);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panelLogin = new JPanel();
		panelLogin.setLayout(null);
		panelLogin.setBounds(0, 0, 324, 121);
		contentPane.add(panelLogin);

		JLabel labelLogin = new JLabel("Login:");
		labelLogin.setBounds(10, 11, 74, 14);
		panelLogin.add(labelLogin);

		JLabel labelPassword = new JLabel("Password:");
		labelPassword.setBounds(10, 36, 74, 14);
		panelLogin.add(labelPassword);

		textFieldLogin = new JTextField();
		textFieldLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		textFieldLogin.setColumns(10);
		textFieldLogin.setBounds(94, 8, 220, 20);
		panelLogin.add(textFieldLogin);

		textFieldPassword = new JTextField();
		textFieldPassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		textFieldPassword.setColumns(10);
		textFieldPassword.setBounds(94, 33, 220, 20);
		panelLogin.add(textFieldPassword);

		lblLoginStatus = new JLabel("");
		lblLoginStatus.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblLoginStatus.setForeground(Color.RED);
		lblLoginStatus.setBounds(10, 61, 304, 14);
		panelLogin.add(lblLoginStatus);

		buttonLogin = new JButton("Login");
		buttonLogin.setBounds(10, 86, 304, 20);
		panelLogin.add(buttonLogin);
		buttonLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		getRootPane().setDefaultButton(buttonLogin);
		this.facade = new Facade();
	}

	public void login() {
		buttonLogin.setEnabled(false);
		new Thread(new Runnable() {
			public void run() {
				try {
					Response response = facade.login(textFieldLogin.getText(), textFieldPassword.getText());
					if (response.getStatus() == 200) {
						MainWindow mainWindow = new MainWindow();
						mainWindow.setFacade(facade);
						mainWindow.setTitle(textFieldLogin.getText());
						mainWindow.setVisible(true);
						dispose();
					}
					if (response.getStatus() == 401) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								lblLoginStatus.setText("Incorrect username and/or password!");
								buttonLogin.setEnabled(true);
							}
						});
					} else {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								lblLoginStatus.setText("Http Status: " + response.getStatus());
								buttonLogin.setEnabled(true);
							}
						});
					}
				} catch (IOException e1) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							lblLoginStatus.setText("config.properties error!");
							buttonLogin.setEnabled(true);
						}
					});
				} catch (Exception e2) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							lblLoginStatus.setText("Unknown error:" + e2.toString());
							buttonLogin.setEnabled(true);
						}
					});
				}
			}
		}).start();
	}
}
