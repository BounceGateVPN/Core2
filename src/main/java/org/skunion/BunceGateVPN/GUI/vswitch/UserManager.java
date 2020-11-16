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
import com.github.smallru8.Secure2.config.Config;

import javax.swing.JScrollPane;

/**
 * 要從SQL讀資料出來
 * 參照WS_Server.verifyUser()
 * @author smallru8
 *
 */
public class UserManager extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private String switchName = null;
	private JList<String> users;
	
	private Config cfg;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UserManager dialog = new UserManager();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 * @param strs switch name
	 */
	public UserManager(String...strs) {
		if(strs.length==1) {
			switchName = strs[0];
			cfg = WS_Server.switchLs.get(switchName).first;
		}
		
		setBounds(100, 100, 597, 396);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		users = new JList<String>();
		JScrollPane scrollPane = new JScrollPane(users);
		scrollPane.setBounds(10, 10, 215, 306);
		contentPanel.add(scrollPane);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
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
				
				users.setListData((String[]) userNames.toArray());
				//TODO
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
