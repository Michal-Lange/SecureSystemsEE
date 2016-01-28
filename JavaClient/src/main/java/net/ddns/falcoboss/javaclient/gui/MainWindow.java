package net.ddns.falcoboss.javaclient.gui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import net.ddns.falcoboss.common.cryptography.SHA512;
import net.ddns.falcoboss.javaclient.api.Facade;
import net.ddns.falcoboss.javaclient.api.User;
import net.ddns.falcoboss.javaclient.api.UserStatus;
import net.ddns.falcoboss.javaclient.api.XmlSerializer;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements Observer{

	private JPanel contentPane;
	private JLabel lblToken;
	private JTable tableUsers;
	private MainWindow mainWindow;
	private JTextField textFieldUsername;
	private JTextField textFieldFirstName;
	private JTextField textFieldLastName;
	private Map<User, MessageWindow> userMessageWindows;
	private Facade facade;
	private JTextField textFieldFile;
	private JTextField textFieldSignature;
	private JTextField textFieldCertyficate;
	private JTextField textFieldSignFilePath;
	private JTextField textFieldActualPassword;
	private JTextField textField_2;
	private JTextField textFieldConfirmPassword;
	private JTextField textFieldKeyUsername;
	private JTextField textFieldKeyPassword;
	private JButton btnRequestNewKeyPair;
	private JTextField textFieldKeyBitLength;
	private JTextArea textAreaSignFileHash;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
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
	public MainWindow() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				loadUserList();
				facade.addObserver(mainWindow);
				facade.startReciveMessages();
			}
		});
		mainWindow = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 420, 700);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmLogout = new JMenuItem("Logout");
		mntmLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logout();
			}
		});
		mnFile.add(mntmLogout);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmEditUserList = new JMenuItem("Edit user list");
		mntmEditUserList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cardLayout = (CardLayout) contentPane.getLayout();
				cardLayout.show(contentPane, "panel_Edit_Users");
			}
		});
		mnEdit.add(mntmEditUserList);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmTokenInfo = new JMenuItem("Token Info");
		mntmTokenInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						facade.getAuthToken(),
					    "Authorisation token",
					    JOptionPane.INFORMATION_MESSAGE);
				
			}
		});
		mnHelp.add(mntmTokenInfo);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JPanel panelMain = new JPanel();
		tabbedPane.addTab("Messanger", null, panelMain, null);
		
		Panel panelStatus = new Panel();
		
		lblToken = new JLabel("");
		
		JPanel panelUserList = new JPanel();
		
		tableUsers = new JTable();
		tableUsers.setToolTipText("Double click on a contact to Open Message Window");
		tableUsers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				 if (e.getClickCount() == 2) {
			         JTable target = (JTable)e.getSource();
			         int row = target.getSelectedRow();
			         User selectedUser = facade.getUserList().get(row);
			         if(userMessageWindows.containsKey(selectedUser) && userMessageWindows.get(selectedUser)!=null)
			         {
			        	 MessageWindow messageWindow = userMessageWindows.get(selectedUser);
			        	 messageWindow.setVisible(true);
			         }
			         else
			         {
			        	 MessageWindow messageWindow = new MessageWindow();
			        	 messageWindow.setFacade(facade);
			        	 messageWindow.setUser(selectedUser);
			        	 messageWindow.setVisible(true);
			        	 userMessageWindows.put(selectedUser, messageWindow);
			         }
			     }
			}
		});
		tableUsers.setShowVerticalLines(false);
		tableUsers.setShowHorizontalLines(false);
		tableUsers.setShowGrid(false);
		tableUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableUsers.setFont(new Font("Tahoma", Font.PLAIN, 14));
		tableUsers.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"User", "Message", "Status"
				}
			){
				@Override
				public boolean isCellEditable(int row, int column){  
			          return false;  
				}
			});
		tableUsers.setRowSelectionAllowed(true);
		tableUsers.getColumn("User").setPreferredWidth(190);
		GroupLayout gl_panelUserList = new GroupLayout(panelUserList);
		gl_panelUserList.setHorizontalGroup(
			gl_panelUserList.createParallelGroup(Alignment.LEADING)
				.addComponent(tableUsers, GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
		);
		gl_panelUserList.setVerticalGroup(
			gl_panelUserList.createParallelGroup(Alignment.LEADING)
				.addComponent(tableUsers, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
		);
		panelUserList.setLayout(gl_panelUserList);
		GroupLayout gl_panelStatus = new GroupLayout(panelStatus);
		gl_panelStatus.setHorizontalGroup(
			gl_panelStatus.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelStatus.createSequentialGroup()
					.addGap(197)
					.addComponent(lblToken))
		);
		gl_panelStatus.setVerticalGroup(
			gl_panelStatus.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelStatus.createSequentialGroup()
					.addGap(5)
					.addComponent(lblToken))
		);
		panelStatus.setLayout(gl_panelStatus);
		GroupLayout gl_panelMain = new GroupLayout(panelMain);
		gl_panelMain.setHorizontalGroup(
			gl_panelMain.createParallelGroup(Alignment.TRAILING)
				.addComponent(panelStatus, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
				.addComponent(panelUserList, GroupLayout.PREFERRED_SIZE, 389, Short.MAX_VALUE)
		);
		gl_panelMain.setVerticalGroup(
			gl_panelMain.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelMain.createSequentialGroup()
					.addComponent(panelUserList, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelStatus, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
		);
		panelMain.setLayout(gl_panelMain);
		
		JPanel panelEditUsers = new JPanel();
		tabbedPane.addTab("Users", null, panelEditUsers, null);
		
		JPanel panel = new JPanel();
		
		JButton btnAddUser = new JButton("Add User");
		btnAddUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNewUser();
			}
		});
		
		JLabel lblUsername = new JLabel("Username:");
		
		JLabel lblFirstName = new JLabel("First Name:");
		
		JLabel lblLastName = new JLabel("Last Name:");
		
		textFieldUsername = new JTextField();
		textFieldUsername.setColumns(10);
		
		textFieldFirstName = new JTextField();
		textFieldFirstName.setColumns(10);
		
		textFieldLastName = new JTextField();
		textFieldLastName.setColumns(10);
		GroupLayout gl_panelEditUsers = new GroupLayout(panelEditUsers);
		gl_panelEditUsers.setHorizontalGroup(
			gl_panelEditUsers.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
		);
		gl_panelEditUsers.setVerticalGroup(
			gl_panelEditUsers.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelEditUsers.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
					.addGap(475))
		);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(10)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblUsername, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
							.addGap(10)
							.addComponent(textFieldUsername, GroupLayout.PREFERRED_SIZE, 262, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblFirstName, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
							.addGap(10)
							.addComponent(textFieldFirstName, GroupLayout.PREFERRED_SIZE, 262, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblLastName, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
							.addGap(10)
							.addComponent(textFieldLastName, GroupLayout.PREFERRED_SIZE, 262, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnAddUser, GroupLayout.PREFERRED_SIZE, 374, GroupLayout.PREFERRED_SIZE)))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(8)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(3)
							.addComponent(lblUsername))
						.addComponent(textFieldUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(3)
							.addComponent(lblFirstName))
						.addComponent(textFieldFirstName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(3)
							.addComponent(lblLastName))
						.addComponent(textFieldLastName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addComponent(btnAddUser))
		);
		panel.setLayout(gl_panel);
		panelEditUsers.setLayout(gl_panelEditUsers);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 394, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 630, Short.MAX_VALUE)
		);
		
		JPanel panelDigitalSignature = new JPanel();
		tabbedPane.addTab("Digital Signature", null, panelDigitalSignature, null);
		
		JPanel panelVerifySignature = new JPanel();
		
		JPanel panelSign = new JPanel();
		GroupLayout gl_panelDigitalSignature = new GroupLayout(panelDigitalSignature);
		gl_panelDigitalSignature.setHorizontalGroup(
			gl_panelDigitalSignature.createParallelGroup(Alignment.LEADING)
				.addComponent(panelVerifySignature, GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
				.addComponent(panelSign, GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
		);
		gl_panelDigitalSignature.setVerticalGroup(
			gl_panelDigitalSignature.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelDigitalSignature.createSequentialGroup()
					.addComponent(panelVerifySignature, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelSign, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(172, Short.MAX_VALUE))
		);
		
		JLabel lblSignFile = new JLabel("Sign File");
		lblSignFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		textFieldSignFilePath = new JTextField();
		textFieldSignFilePath.setEditable(false);
		textFieldSignFilePath.setColumns(10);
		
		JButton btnLoadFileToSign = new JButton("Load File");
		btnLoadFileToSign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Load file for sign");
				int result = fileChooser.showOpenDialog(mainWindow);
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = fileChooser.getSelectedFile();
				    String absolutePath = selectedFile.getAbsolutePath();
				    new Thread(new Runnable() {
						public void run() {
							try {
								byte[] selectedFileAllBytes = Files.readAllBytes(Paths.get(absolutePath));
								String fileHash = SHA512.convertByteToHex(selectedFileAllBytes);
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										textAreaSignFileHash.setText(fileHash);
									}
								});
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(mainWindow,
									     e1.getMessage(),
									    "File Load Error",
									    JOptionPane.INFORMATION_MESSAGE);
								e1.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		
		JButton btnSignFile = new JButton("Sign File");
		btnSignFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				facade.signFileHash(textAreaSignFileHash.getText());
			}
		});
		btnSignFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton btnSaveSignature = new JButton("Save Signature");
		
		textAreaSignFileHash = new JTextArea();
		textAreaSignFileHash.setLineWrap(true);
		textAreaSignFileHash.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textAreaSignFileHash.setEditable(false);
		GroupLayout gl_panelSign = new GroupLayout(panelSign);
		gl_panelSign.setHorizontalGroup(
			gl_panelSign.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSign.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelSign.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelSign.createSequentialGroup()
							.addComponent(textAreaSignFileHash, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(Alignment.TRAILING, gl_panelSign.createSequentialGroup()
							.addGroup(gl_panelSign.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSignFile)
								.addGroup(gl_panelSign.createSequentialGroup()
									.addComponent(btnLoadFileToSign, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(textFieldSignFilePath, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)))
							.addContainerGap())
						.addGroup(Alignment.TRAILING, gl_panelSign.createSequentialGroup()
							.addComponent(btnSaveSignature, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
							.addGap(266))
						.addGroup(Alignment.TRAILING, gl_panelSign.createSequentialGroup()
							.addComponent(btnSignFile, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
							.addContainerGap())))
		);
		gl_panelSign.setVerticalGroup(
			gl_panelSign.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSign.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblSignFile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelSign.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnLoadFileToSign)
						.addComponent(textFieldSignFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(textAreaSignFileHash, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnSignFile)
					.addPreferredGap(ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
					.addComponent(btnSaveSignature)
					.addContainerGap())
		);
		panelSign.setLayout(gl_panelSign);
		
		JLabel lblVerifySignature = new JLabel("Verify Signature");
		lblVerifySignature.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton btnLoadFile = new JButton("Load File");
		
		JButton btnLoadSignature = new JButton("Load Signature");
		
		JButton btnLoad = new JButton("Load Certyficate");
		
		textFieldFile = new JTextField();
		textFieldFile.setEditable(false);
		textFieldFile.setColumns(10);
		
		textFieldSignature = new JTextField();
		textFieldSignature.setEditable(false);
		textFieldSignature.setColumns(10);
		
		textFieldCertyficate = new JTextField();
		textFieldCertyficate.setEditable(false);
		textFieldCertyficate.setColumns(10);
		
		JButton btnVerify = new JButton("Verify File");
		btnVerify.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GroupLayout gl_panelVerifySignature = new GroupLayout(panelVerifySignature);
		gl_panelVerifySignature.setHorizontalGroup(
			gl_panelVerifySignature.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelVerifySignature.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelVerifySignature.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnVerify, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
						.addGroup(gl_panelVerifySignature.createSequentialGroup()
							.addGroup(gl_panelVerifySignature.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(btnLoadSignature, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblVerifySignature, Alignment.LEADING)
								.addComponent(btnLoad, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnLoadFile, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelVerifySignature.createParallelGroup(Alignment.LEADING)
								.addComponent(textFieldSignature, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
								.addComponent(textFieldFile, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
								.addComponent(textFieldCertyficate, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_panelVerifySignature.setVerticalGroup(
			gl_panelVerifySignature.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelVerifySignature.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblVerifySignature)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelVerifySignature.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnLoadFile)
						.addComponent(textFieldFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelVerifySignature.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnLoadSignature)
						.addComponent(textFieldSignature, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelVerifySignature.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnLoad)
						.addComponent(textFieldCertyficate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnVerify)
					.addContainerGap(35, Short.MAX_VALUE))
		);
		panelVerifySignature.setLayout(gl_panelVerifySignature);
		panelDigitalSignature.setLayout(gl_panelDigitalSignature);
		
		JPanel panelConfiguration = new JPanel();
		tabbedPane.addTab("Configuration", null, panelConfiguration, null);
		
		JPanel panelRequestNewKey = new JPanel();
		
		btnRequestNewKeyPair = new JButton("Request New Key Pair");
		btnRequestNewKeyPair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				requestNewKey();
			}
		});
		
		JLabel lblDigitalSignatureKey = new JLabel("Digital Signature Key");
		lblDigitalSignatureKey.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblKeyUsername = new JLabel("Username:");
		
		JLabel lblKeyPassword = new JLabel("Password:");
		
		textFieldKeyUsername = new JTextField();
		textFieldKeyUsername.setColumns(10);
		
		textFieldKeyPassword = new JTextField();
		textFieldKeyPassword.setColumns(10);
		
		JLabel labelRequestNewKey = new JLabel("Request New Key Pair");
		labelRequestNewKey.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblKeyBitLength = new JLabel("Key Bit Length:");
		
		textFieldKeyBitLength = new JTextField();
		textFieldKeyBitLength.setEditable(false);
		textFieldKeyBitLength.setColumns(10);

		
		JButton btnPublicKeyDetails = new JButton("Public Key Details");
		btnPublicKeyDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(facade.getPrivateKey() != null && facade.getPublicKey() != null)
				{
					JOptionPane.showMessageDialog(mainWindow,
						    "Public Exponent: " + facade.getPublicKey().getPublicExponent().toString()
						    + "\n\nModulus: " + facade.getPublicKey().getModulus().toString(),
						    "RSA Public Key",
						    JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					noRSAKeyMessageWindow();
				}
			}
		});
		
		JButton btnPrivateKeyDetails = new JButton("Private Key Details");
		btnPrivateKeyDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(facade.getPrivateKey() != null && facade.getPublicKey() != null)
				{
					JOptionPane.showMessageDialog(mainWindow,
						    "Private Exponent: " + facade.getPrivateKey().getPrivateExponent().toString()
						    + "\n\nModulus: " + facade.getPrivateKey().getModulus().toString(),
						    "RSA Private Key",
						    JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					noRSAKeyMessageWindow();
				}
			}
		});
		GroupLayout gl_panelRequestNewKey = new GroupLayout(panelRequestNewKey);
		gl_panelRequestNewKey.setHorizontalGroup(
			gl_panelRequestNewKey.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelRequestNewKey.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelRequestNewKey.createParallelGroup(Alignment.LEADING)
						.addComponent(lblDigitalSignatureKey, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
						.addComponent(labelRequestNewKey, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
						.addGroup(gl_panelRequestNewKey.createSequentialGroup()
							.addGroup(gl_panelRequestNewKey.createParallelGroup(Alignment.LEADING)
								.addComponent(lblKeyUsername)
								.addComponent(lblKeyPassword))
							.addGap(18)
							.addGroup(gl_panelRequestNewKey.createParallelGroup(Alignment.LEADING)
								.addComponent(textFieldKeyUsername, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
								.addComponent(textFieldKeyPassword, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)))
						.addComponent(btnRequestNewKeyPair, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
						.addGroup(gl_panelRequestNewKey.createSequentialGroup()
							.addComponent(lblKeyBitLength)
							.addGap(18)
							.addComponent(textFieldKeyBitLength, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
						.addComponent(btnPublicKeyDetails, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
						.addComponent(btnPrivateKeyDetails, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panelRequestNewKey.setVerticalGroup(
			gl_panelRequestNewKey.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelRequestNewKey.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblDigitalSignatureKey)
					.addGap(24)
					.addGroup(gl_panelRequestNewKey.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblKeyBitLength)
						.addComponent(textFieldKeyBitLength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(btnPublicKeyDetails)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnPrivateKeyDetails)
					.addPreferredGap(ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
					.addComponent(labelRequestNewKey, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_panelRequestNewKey.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblKeyUsername)
						.addComponent(textFieldKeyUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelRequestNewKey.createParallelGroup(Alignment.LEADING)
						.addComponent(lblKeyPassword)
						.addComponent(textFieldKeyPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(btnRequestNewKeyPair)
					.addContainerGap())
		);
		panelRequestNewKey.setLayout(gl_panelRequestNewKey);
		
		JPanel panelPassword = new JPanel();
		GroupLayout gl_panelConfiguration = new GroupLayout(panelConfiguration);
		gl_panelConfiguration.setHorizontalGroup(
			gl_panelConfiguration.createParallelGroup(Alignment.LEADING)
				.addComponent(panelRequestNewKey, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
				.addComponent(panelPassword, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
		);
		gl_panelConfiguration.setVerticalGroup(
			gl_panelConfiguration.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelConfiguration.createSequentialGroup()
					.addComponent(panelRequestNewKey, GroupLayout.PREFERRED_SIZE, 292, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(127, Short.MAX_VALUE))
		);
		
		JLabel lblPassword = new JLabel("Change Password");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton btnChangePassword = new JButton("Change Password");
		
		JLabel lblActualPassword = new JLabel("Actual Password:");
		
		textFieldActualPassword = new JTextField();
		textFieldActualPassword.setColumns(10);
		
		JLabel lblNewPassword = new JLabel("New Password:");
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		
		textFieldConfirmPassword = new JTextField();
		textFieldConfirmPassword.setColumns(10);
		
		JLabel lblConfirmPassword = new JLabel("Confirm Password:");
		GroupLayout gl_panelPassword = new GroupLayout(panelPassword);
		gl_panelPassword.setHorizontalGroup(
			gl_panelPassword.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelPassword.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelPassword.createParallelGroup(Alignment.LEADING)
						.addComponent(lblPassword)
						.addGroup(gl_panelPassword.createSequentialGroup()
							.addGroup(gl_panelPassword.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewPassword)
								.addComponent(lblConfirmPassword)
								.addComponent(lblActualPassword))
							.addGap(18)
							.addGroup(gl_panelPassword.createParallelGroup(Alignment.LEADING)
								.addComponent(textField_2, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
								.addComponent(textFieldActualPassword, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
								.addComponent(textFieldConfirmPassword, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)))
						.addComponent(btnChangePassword, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panelPassword.setVerticalGroup(
			gl_panelPassword.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelPassword.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblPassword)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelPassword.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblActualPassword)
						.addComponent(textFieldActualPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelPassword.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewPassword)
						.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelPassword.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblConfirmPassword)
						.addComponent(textFieldConfirmPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnChangePassword)
					.addGap(37))
		);
		panelPassword.setLayout(gl_panelPassword);
		panelConfiguration.setLayout(gl_panelConfiguration);
		contentPane.setLayout(gl_contentPane);
		contentPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{panelMain, panelEditUsers}));
		userMessageWindows = new HashMap<User, MessageWindow>();
	}
	
	private void logout(){
		Response response = facade.logout();
		if(response.getStatus() == 204){
			LoginWindow loginWindow = new LoginWindow();
			loginWindow.setVisible(true);
			dispose();
		}
	}

	private void loadUserList() {
		try {			
			facade.setUserList(XmlSerializer.unmarshall());
		} catch (Exception e) {
			e.printStackTrace();
		}
		updateUserList();
	}
	
	public void updateUserList() {
		DefaultTableModel tableUsersModel = (DefaultTableModel) tableUsers.getModel();
		tableUsersModel.setRowCount(0);
		for (int i = 0; i < facade.getUserList().size(); i++) {
	        Object[] data = new String[3];

	            data[0] = facade.getUserList().get(i).getFistName() + " " + facade.getUserList().get(i).getLastName() + " (" + facade.getUserList().get(i).getUsername() + ")";
	            data[1] = facade.getUserList().get(i).getUserStatus().toString();
	            if(facade.getUserList().get(i).isUpdated()){
	            	data[2] = "MSG";
	            }
	            else{
	            	data[2] = "";
	            }
	            tableUsersModel.addRow(data);
	    }
		tableUsersModel.fireTableDataChanged();
	}
	
	public void udpateKeyInfo(){
		if(facade.getPrivateKey() != null && facade.getPublicKey() != null)
		{
			int keyBitLength = facade.getPrivateKey().getModulus().bitLength();
			textFieldKeyBitLength.setText(Integer.toString(keyBitLength));
		}
	}

	public JLabel getLblToken() {
		return lblToken;
	}

	public Facade getFacade() {
		return facade;
	}

	public void setFacade(Facade facade) {
		this.facade = facade;
	}
	
	private void addNewUser(){
		User newUser = new User();
		newUser.setUsername(textFieldUsername.getText());
		newUser.setFistName(textFieldFirstName.getText());
		newUser.setLastName(textFieldLastName.getText());
		newUser.setUpdated(false);
		newUser.setUserStatus(UserStatus.NOTAVAILABLE);
		if ("".equals(newUser.getUsername()) || "".equals(newUser.getFistName()) || "".equals(newUser.getLastName())){
			JOptionPane.showMessageDialog(mainWindow,
				    "Enter Username, First Name and Last Name",
				    "Wrong User Data!",
				    JOptionPane.WARNING_MESSAGE);
		} else if(facade.getUserList().contains(newUser)){
			JOptionPane.showMessageDialog(mainWindow,
				    "User already on the list!",
				    "User exist!",
				    JOptionPane.WARNING_MESSAGE);
		} else	{
			try {
				facade.addContact(newUser);
			} catch (JAXBException e) {
				JOptionPane.showMessageDialog(mainWindow,
					    e.getMessage(),
					    "Error",
					    JOptionPane.WARNING_MESSAGE);
				btnRequestNewKeyPair.setEnabled(true);
			}
			updateUserList();
			JOptionPane.showMessageDialog(mainWindow,
				    "User added to the list!",
				    "User created!",
				    JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void requestNewKey()
	{
		if("".equals(textFieldKeyUsername.getText()) || "".equals(textFieldKeyPassword.getText()))
		{
			JOptionPane.showMessageDialog(mainWindow,
				    "Enter Username and Password",
				    "Wrong Login Data!",
				    JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			btnRequestNewKeyPair.setEnabled(false);
			new Thread(new Runnable() {
				public void run() {
					try {
						facade.requestNewKey(textFieldKeyUsername.getText(), textFieldKeyPassword.getText());
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								btnRequestNewKeyPair.setEnabled(false);
							}
						});
					} catch(Exception e){
						JOptionPane.showMessageDialog(mainWindow,
							    e.getMessage(),
							    "Error",
							    JOptionPane.WARNING_MESSAGE);
						btnRequestNewKeyPair.setEnabled(true);
					}
				}
			}).start();
		}
	}
	
	public void noRSAKeyMessageWindow()
	{
		JOptionPane.showMessageDialog(mainWindow,
			    "Please generate or deliver the RSA Key Pair ",
			    "RSA key not loaded!",
			    JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void update(Observable o, Object arg) {
		checkErrors();
		updateUserList();
		udpateKeyInfo();
	}
	private void checkErrors() {
		synchronized(facade.getErrorResponse())
		{
			while(!facade.getErrorResponse().isEmpty())
			{
				int responseStatus = facade.getErrorResponse().poll().getStatus();
				if(responseStatus == 401)
				{
					JOptionPane.showMessageDialog(mainWindow,
						    "Wrong username or password",
						    "Login Error",
						    JOptionPane.WARNING_MESSAGE);
				}
				if(responseStatus == 500)
				{
					JOptionPane.showMessageDialog(mainWindow,
						    "Registration Server Error",
						    "Server Error",
						    JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					JOptionPane.showMessageDialog(mainWindow,
						    "Unknown Response Status Code: " + Integer.toString(responseStatus),
						    "Unknown Response",
						    JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}

	public JTextField getTextFieldSignFilePath() {
		return textFieldSignFilePath;
	}
	public JTextArea getTextAreaSignFileHash() {
		return textAreaSignFileHash;
	}
}
