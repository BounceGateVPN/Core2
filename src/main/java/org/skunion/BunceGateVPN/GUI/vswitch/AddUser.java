package org.skunion.BunceGateVPN.GUI.vswitch;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.smallru8.Secure2.Secure2;
import com.github.smallru8.Secure2.SQL.SQL;
import com.github.smallru8.Secure2.config.Config;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Edit/Add user data
 * @author smallru8
 *
 */
public class AddUser extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JPasswordField passwordField;
	private boolean flag;
	private JDialog AU = this;
	private Config cfg;
	private String userName;
	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			AddUser dialog = new AddUser();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/

	/**
	 * Create the dialog.
	 * b=true add
	 * b=false edit
	 */
	public AddUser(Config cfg_,boolean b,String...name) {
		this.flag = b;
		this.cfg = cfg_;
		if(name.length==1)
			userName = name[0];
		setBounds(100, 100, 209, 146);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("UserName");
		lblNewLabel.setBounds(10, 10, 107, 15);
		contentPanel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Password");
		lblNewLabel_1.setBounds(10, 35, 107, 15);
		contentPanel.add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(85, 7, 96, 21);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(85, 35, 96, 21);
		contentPanel.add(passwordField);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(textField.getText()!=null&&passwordField.getPassword()!=null) {
							
							Connection sqlconn = null;
							if(cfg.SQLFlag)//使用SQL
								sqlconn = Secure2.sql.getSQLConnection(cfg.confName, cfg.host, cfg.userName, cfg.passwd);
							else //使用SQLite
								sqlconn = Secure2.sql.getSQLConnection(cfg.confName);
							if(flag) {
								SQL.registered(cfg.confName, sqlconn, textField.getText(), String.valueOf(passwordField.getPassword()));
							}else {
								SQL.changePasswd(cfg.confName, sqlconn, textField.getText(), String.valueOf(passwordField.getPassword()));
							}
							try {
								sqlconn.close();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							AU.dispose();
						}
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
						AU.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		loadData();
	}
	private void loadData() {
		if(!flag) {
			textField.setText(userName);
			textField.setEditable(false);
		}
	}
}
