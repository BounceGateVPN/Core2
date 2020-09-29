package org.skunion.BunceGateVPN.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.skunion.BunceGateVPN.core2.Main;
import org.skunion.BunceGateVPN.core2.websocket.WS_Server;

import com.github.smallru8.BounceGateVPN.Switch.VirtualSwitch;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.Pair;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AddConfig extends JDialog {
	
	private final JPanel contentPanel = new JPanel();
	private JDialog jd = this;
	private JTextField switchName;//若為Server, switchName = cfgName
	private JTextField hostname;
	private JTextField portNum;
	private JTextField userName;
	private JPasswordField passwd;
	private JTextField sqlURL;
	private JTextField sqlUser;
	private JCheckBox sqlCheck;
	private JPanel panel;
	private Config cfg;
	
	private Config.ConfType t;
	public JTextField cfgName;
	private JPasswordField sqlPasswd;
	
	/*
	public static void main(String[] args) {
		try {
			AddConfig dialog = new AddConfig();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/

	public AddConfig(Config cfg,Config.ConfType t) {
		this.t = t;
		createUI();
		this.setTitle("Edit : " + cfg.confName);
		cfgName.setText(cfg.confName);
		
		if(t.equals(Config.ConfType.CLIENT)) {//Client config
			this.setTitle("Add a new client config");
			sqlCheck.setEnabled(false);
			
			portNum.setText(cfg.pro.getProperty("port"));
			passwd.setText(cfg.pro.getProperty("passwd"));
			hostname.setText(cfg.pro.getProperty("ip"));
			userName.setText(cfg.pro.getProperty("user"));
			switchName.setText(cfg.pro.getProperty("switch"));
			
		}else {//Server config
			this.setTitle("Add a new virtual switch config");
			hostname.setText("");
			hostname.setEditable(false);
			portNum.setEditable(false);
			userName.setEditable(false);
			passwd.setEditable(false);
			switchName.setEditable(false);
			
			if(cfg.pro.getProperty("SQL").equalsIgnoreCase("TRUE"))
				sqlCheck.setSelected(true);
			else
				sqlCheck.setSelected(false);
			sqlURL.setText(cfg.pro.getProperty("host"));
			sqlUser.setText(cfg.pro.getProperty("user"));
			sqlPasswd.setText(cfg.pro.getProperty("passwd"));
			switchName.setText(cfg.pro.getProperty("switch"));
		}
		setIconImage(Main.icon.getImage());
	}
	
	public AddConfig(Config.ConfType t) {
		this.t = t;
		createUI();
		if(t.equals(Config.ConfType.CLIENT)) {//Client config
			this.setTitle("Add a new client config");
			sqlCheck.setEnabled(false);
		}else {//Server config
			this.setTitle("Add a new virtual switch config");
			hostname.setEditable(false);
			portNum.setEditable(false);
			userName.setEditable(false);
			passwd.setEditable(false);
			switchName.setEditable(false);
		}
	}
	
	/**
	 * Create the dialog.
	 */
	public AddConfig() {
		createUI();
	}
	
	private void createUI() {
		setBounds(100, 100, 450, 416);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			switchName = new JTextField();
			switchName.setBounds(91, 60, 191, 21);
			contentPanel.add(switchName);
			switchName.setColumns(10);
		}
		{
			JLabel lblNewLabel_1 = new JLabel("Hostname");
			lblNewLabel_1.setBounds(10, 35, 71, 15);
			contentPanel.add(lblNewLabel_1);
		}
		{
			JLabel lblNewLabel = new JLabel("Switch name");
			lblNewLabel.setBounds(10, 63, 122, 15);
			contentPanel.add(lblNewLabel);
		}
		{
			hostname = new JTextField();
			hostname.setText("ws://hostname");
			hostname.setBounds(91, 32, 191, 21);
			contentPanel.add(hostname);
			hostname.setColumns(10);
		}
		{
			JLabel lblNewLabel_1 = new JLabel("Port");
			lblNewLabel_1.setBounds(292, 35, 24, 15);
			contentPanel.add(lblNewLabel_1);
		}
		{
			portNum = new JTextField();
			portNum.setBounds(320, 32, 47, 21);
			contentPanel.add(portNum);
			portNum.setColumns(10);
		}
		{
			JLabel lblUserName = new JLabel("User name");
			lblUserName.setBounds(10, 91, 71, 15);
			contentPanel.add(lblUserName);
		}
		{
			userName = new JTextField();
			userName.setBounds(91, 88, 191, 21);
			contentPanel.add(userName);
			userName.setColumns(10);
		}
		{
			JLabel lblPassword = new JLabel("Password");
			lblPassword.setBounds(10, 116, 71, 15);
			contentPanel.add(lblPassword);
		}
		
		passwd = new JPasswordField();
		passwd.setBounds(91, 116, 191, 21);
		contentPanel.add(passwd);
		
		panel = new JPanel();
		panel.setBounds(10, 172, 414, 162);
		contentPanel.add(panel);
		panel.setLayout(null);
		
		sqlCheck = new JCheckBox("SQL");
		sqlCheck.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(sqlCheck.isSelected())
					panel.setVisible(true);
				else if(!sqlCheck.isSelected())
					panel.setVisible(false);
			}
		});
		sqlCheck.setBounds(10, 143, 97, 23);
		contentPanel.add(sqlCheck);
		
		if(!sqlCheck.isSelected())
			panel.setVisible(false);
		
		JLabel lblNewLabel_1 = new JLabel("JDBC URL");
		lblNewLabel_1.setBounds(10, 10, 69, 15);
		panel.add(lblNewLabel_1);
		
		sqlURL = new JTextField();
		sqlURL.setBounds(89, 7, 186, 21);
		panel.add(sqlURL);
		sqlURL.setColumns(10);
		
		JLabel lblNewLabel_1_1 = new JLabel("Name");
		lblNewLabel_1_1.setBounds(10, 35, 69, 15);
		panel.add(lblNewLabel_1_1);
		
		sqlUser = new JTextField();
		sqlUser.setColumns(10);
		sqlUser.setBounds(89, 32, 186, 21);
		panel.add(sqlUser);
		
		JLabel lblNewLabel_1_1_1 = new JLabel("Password");
		lblNewLabel_1_1_1.setBounds(10, 60, 69, 15);
		panel.add(lblNewLabel_1_1_1);
		{
			sqlPasswd = new JPasswordField();
			sqlPasswd.setBounds(89, 57, 186, 21);
			panel.add(sqlPasswd);
		}
		{
			cfgName = new JTextField();
			cfgName.setBounds(91, 7, 191, 21);
			contentPanel.add(cfgName);
			cfgName.setColumns(10);
		}
		{
			JLabel lblNewLabel_2 = new JLabel("Config name");
			lblNewLabel_2.setBounds(10, 10, 122, 15);
			contentPanel.add(lblNewLabel_2);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						cfg = new Config();
						cfg.setConf(cfgName.getText(), t);
						if(t.equals(Config.ConfType.CLIENT)) {//client
							cfg.pro.setProperty("port", portNum.getText());
							cfg.pro.setProperty("passwd",String.valueOf(passwd.getPassword()));
							String host = hostname.getText();
							if(!(host.startsWith("ws://")||host.startsWith("WS://")))
								host = "ws://" + host;
							cfg.pro.setProperty("ip", host);
							cfg.pro.setProperty("user", userName.getText());
							cfg.pro.setProperty("switch", switchName.getText());
							cfg.saveConf();
						}else {//server
							if(sqlCheck.isSelected())
								cfg.pro.setProperty("SQL","true");
							else
								cfg.pro.setProperty("SQL","false");
							
							cfg.pro.setProperty("host", sqlURL.getText());
							cfg.pro.setProperty("user", sqlUser.getText());
							cfg.pro.setProperty("passwd",String.valueOf(sqlPasswd.getPassword()));
							cfg.pro.setProperty("switch", cfgName.getText());//SwitchName = NameConf
							cfg.saveConf();
							
							if(WS_Server.switchLs.containsKey(cfgName.getText())) {
								Pair<Config,VirtualSwitch> cfgVs = WS_Server.switchLs.get(cfgName.getText());
								cfg.setConf(cfgName.getText(), t);
								cfgVs.first = cfg;
								WS_Server.switchLs.remove(cfgName.getText());
								WS_Server.switchLs.put(cfg.switchName, cfgVs);
							}else {
								Pair<Config,VirtualSwitch> cfgVs = new Pair<Config,VirtualSwitch>();
								cfg.setConf(cfgName.getText(), t);
								cfgVs.makePair(cfg, new VirtualSwitch());
								cfgVs.second.start();
								WS_Server.switchLs.put(cfg.switchName, cfgVs);
							}
							
						}
						jd.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						jd.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setIconImage(Main.icon.getImage());
	}
}
