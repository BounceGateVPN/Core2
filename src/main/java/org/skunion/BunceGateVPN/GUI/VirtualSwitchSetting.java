package org.skunion.BunceGateVPN.GUI;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.skunion.BunceGateVPN.core2.BGVConfig;
import org.skunion.BunceGateVPN.core2.Main;
import org.skunion.BunceGateVPN.core2.websocket.WS_Server;

import com.github.smallru8.Secure2.config.Config;

import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VirtualSwitchSetting extends JFrame {

	private JPanel contentPane;
	private DefaultListModel<String> model = new DefaultListModel<String>();
	private Config cfg;
	
	
	public JList list;//L2Linker list
	
	/*
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VirtualSwitchSetting frame = new VirtualSwitchSetting();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	*/
	
	/**
	 * Create the frame.
	 */
	public VirtualSwitchSetting(String switchNameConf) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 674, 409);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setTitle(switchNameConf);
		
		cfg = WS_Server.switchLs.get(((String)switchNameConf.split("\\.")[0])).first;//拿Switch的cfg
		if(cfg.pro.getProperty("L2L")==null) {
			cfg.pro.setProperty("L2L", ",");
			cfg.saveConf();
		}else {
			String[] L2LArray = cfg.pro.getProperty("L2L").split(",");
			for(int i=0;i<L2LArray.length;i++) {
				if(!L2LArray[i].equals(""))
					model.addElement(L2LArray[i]);
			}
		}
		
		list = new JList(model);
		//list.setBounds(10, 34, 127, 149);
		//contentPane.add(list);
		JScrollPane scrollPane1 = new JScrollPane(list);
		scrollPane1.setBounds(10, 34, 127, 149);
		contentPane.add(scrollPane1);
		
		JLabel lblNewLabel = new JLabel("L2Linker");
		lblNewLabel.setBounds(10, 9, 78, 15);
		contentPane.add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Add");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//讀mainframe的client list選中的,加入L2Link list
				String clientName = Main.mainframe.getSelClient();//clientName.conf
				model.addElement(clientName);
				cfg.pro.setProperty("L2L", cfg.pro.getProperty("L2L")+clientName+",");
				cfg.saveConf();
			}
		});
		
		//TODO L2L右鍵連接,L2L右鍵remove
		
		btnNewButton.setBounds(66, 5, 71, 23);
		contentPane.add(btnNewButton);
	}
}
