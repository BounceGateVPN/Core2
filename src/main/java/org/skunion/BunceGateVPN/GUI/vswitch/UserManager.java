package org.skunion.BunceGateVPN.GUI.vswitch;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.skunion.BunceGateVPN.core2.websocket.WS_Server;

import com.github.smallru8.Secure2.Secure2;
import com.github.smallru8.Secure2.SQL.SQL;
import com.github.smallru8.Secure2.config.Config;

import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 要從SQL讀資料出來
 * 參照WS_Server.verifyUser()
 * @author smallru8
 *
 */
public class UserManager extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private JDialog UM = this;
	private String switchName = null;
	private JList<String> users;
	
	private Config cfg;
	
	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			UserManager dialog = new UserManager();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/

	/**
	 * Create the dialog.
	 * @param strs switch name
	 */
	public UserManager(String...strs) {
		if(strs.length==1) {
			switchName = strs[0];
			cfg = WS_Server.switchLs.get(switchName).first;
		}
		
		setBounds(100, 100, 344, 396);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		users = new JList<String>();
		JScrollPane scrollPane = new JScrollPane(users);
		scrollPane.setBounds(10, 10, 215, 306);
		contentPanel.add(scrollPane);
		
		JButton btnNewButton = new JButton("Add");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//Add
				AddUser dialog = new AddUser(cfg,true);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		});
		btnNewButton.setBounds(235, 8, 87, 23);
		contentPanel.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Edit");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//Edit
				if(!users.isSelectionEmpty()) {
					AddUser dialog = new AddUser(cfg,false,users.getSelectedValue());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				}
			}
		});
		btnNewButton_1.setBounds(235, 41, 87, 23);
		contentPanel.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Remove");
		btnNewButton_2.addMouseListener(new MouseAdapter() {//Remove
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!users.isSelectionEmpty()) {
					Connection sqlconn = null;
					if(cfg.SQLFlag)//使用SQL
						sqlconn = Secure2.sql.getSQLConnection(cfg.confName, cfg.host, cfg.userName, cfg.passwd);
					else //使用SQLite
						sqlconn = Secure2.sql.getSQLConnection(cfg.confName);
					SQL.removeUser(cfg.confName, sqlconn, users.getSelectedValue());
				}
			}
		});
		btnNewButton_2.setBounds(235, 74, 87, 23);
		contentPanel.add(btnNewButton_2);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						UM.dispose();
					}
				});
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		loadData();
	}
	
	private void loadData() {
		if(switchName!=null) {
			//從SQL載入資料
			Connection sqlconn = null;
			if(cfg.SQLFlag)//使用SQL
				sqlconn = Secure2.sql.getSQLConnection(cfg.confName, cfg.host, cfg.userName, cfg.passwd);
			else //使用SQLite
				sqlconn = Secure2.sql.getSQLConnection(cfg.confName);
				
			try {
				PreparedStatement ps = sqlconn.prepareStatement("SELECT Name FROM "+switchName+";");
				ResultSet rs = ps.executeQuery();
				ArrayList<String> userNames = new ArrayList<String>();
				while(rs.next()) {
					userNames.add(rs.getString(1));
				}
				
				String[] userList = new String[userNames.size()];
				userList = userNames.toArray(userList);
				users.setListData(userList);
				//TODO
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
