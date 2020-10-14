package org.skunion.BunceGateVPN.GUI;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.skunion.BunceGateVPN.core2.BGVConfig;
import org.skunion.BunceGateVPN.core2.Main;
import org.skunion.BunceGateVPN.core2.websocket.WS_Client;
import org.skunion.BunceGateVPN.core2.websocket.WS_Package;
import org.skunion.BunceGateVPN.core2.websocket.WS_Server;

import com.github.smallru8.BounceGateVPN.Switch.SwitchPort;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.abstracts.Port;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;

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
		if(cfg.pro.getProperty("L2L")==null) {//L2L
			cfg.pro.setProperty("L2L", ",");
			cfg.saveConf();
		}else {
			String[] L2LArray = cfg.pro.getProperty("L2L").split(",");
			for(int i=0;i<L2LArray.length;i++) {
				if(!L2LArray[i].equals(""))
					model.addElement(L2LArray[i]);
			}
		}
		
		if(cfg.pro.getProperty("L2LAuto")==null) {//L2L auto connect list
			cfg.pro.setProperty("L2LAuto", ",");
			cfg.saveConf();
		}
		
		list = new JList(model);
		list.addMouseListener(new MouseAdapter() {//右鍵
			@Override
			public void mousePressed(MouseEvent e) {
				int index = list.locationToIndex(e.getPoint());
				if (SwingUtilities.isRightMouseButton(e)&&index!=-1) {//L2L右鍵選單
					list.setSelectedIndex(index);
	                JPopupMenu menu = new JPopupMenu();
	                JMenuItem item;//connect
	                JMenuItem item2;//remove
	                JCheckBoxMenuItem chckbxmntmNewCheckItem;//autoConn
	                
	                //看WS_Client是否已存在在vSwitch(已連線)
	                Iterator iterator = WS_Server.switchLs.get(cfg.pro.getProperty("switch")).second.port.entrySet().iterator();
	                boolean connectionStart = false;
	                while (iterator.hasNext()) {
	                	Map.Entry mapEntry = (Map.Entry) iterator.next();
	                	
	                	//為WS
	                	if(((SwitchPort)mapEntry.getValue()).type.equals(Port.DeviceType.WS)) {
	                		try {
	                			WS_Client wsc = (WS_Client)((SwitchPort)mapEntry.getValue()).ws;//本機主動連出去的WS
	                			//與選中名稱相同,split是去掉.conf
	                			if(wsc.ud.sessionName.equalsIgnoreCase(((String)list.getSelectedValue()).split("\\.")[0])) {
	                				//這個連線有啟用
	                				connectionStart = true;
	                				//TODO:新增彈出式菜單物件 : Disconnect
	                				
	                			}else {
	                				continue;
	                			}
	                			
	                			
	                		}catch (ClassCastException cce) {
	                			continue;//發生exception表示不是目標
	                		}
	                	}
	                }
	                
	                if(!connectionStart) {//連線未啟動
	                	
	                	//TODO:新增彈出式菜單物件 : Connect
	                	
	                }
	                
	                /*
	                if(WS_Server.switchLs) {
	                	cfg.pro.getProperty("switch")
	                }
	                */
				}
			}
		});
		
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
