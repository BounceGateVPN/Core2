package org.skunion.BunceGateVPN.GUI;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.skunion.BunceGateVPN.core2.BGVConfig;
import org.skunion.BunceGateVPN.core2.Main;
import org.skunion.BunceGateVPN.core2.websocket.WS_Client;
import org.skunion.BunceGateVPN.core2.websocket.WS_Server;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.log.Event.LogEvent;
import com.github.smallru8.util.log.EventSender;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JScrollPane;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JList<String> list;//client
	private JList<String> list_1;//server
	private JCheckBoxMenuItem chckbxmntmNewCheckItem;
	private JTextArea textArea;
	private String path0 = "config/client/";
	private String path1 = "config/server/";
	
	/**
	 * Launch the application.
	 */
	/*
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
	*/
	
	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		EventBus.getDefault().register(this);
		setTitle("BunceGateVPN");
		setBounds(100, 100, 651, 416);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		/*
		 * Add client Config
		 */
		JMenuItem mntmNewMenuItem = new JMenuItem("Add client config");
		mntmNewMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					EventSender.sendLog("Add client config");
					AddConfig dialog = new AddConfig(Config.ConfType.CLIENT);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Refresh");
		mntmNewMenuItem_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				refreshJList();//refresh JList
			}
		});
		mnNewMenu.add(mntmNewMenuItem_2);
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Add vSwitch config");
		mntmNewMenuItem_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					EventSender.sendLog("Add vSwitch config");
					AddConfig dialog = new AddConfig(Config.ConfType.SERVER);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JMenu mnNewMenu_1 = new JMenu("TunTap");
		menuBar.add(mnNewMenu_1);
		
		chckbxmntmNewCheckItem = new JCheckBoxMenuItem("Enable Tap");
		chckbxmntmNewCheckItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(chckbxmntmNewCheckItem.isSelected()) {
					EventSender.sendLog("Tap device enable.");
					BGVConfig.bgvConf.setConf("Tap", "true");
					Main.td.runFlag = true;
					Main.td.start();
				}else if(!chckbxmntmNewCheckItem.isSelected()){
					EventSender.sendLog("Tap device disable.");
					BGVConfig.bgvConf.setConf("Tap", "false");
					Main.td.runFlag = false;
					Main.td.stop();
				}
			}
		});
		mnNewMenu_1.add(chckbxmntmNewCheckItem);
		
		JMenu mnNewMenu_2 = new JMenu("Setting");
		menuBar.add(mnNewMenu_2);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Listen port");
		mntmNewMenuItem_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					ListenPortSetting dialog = new ListenPortSetting();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_3);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		/**
		 * Client list
		 */
		list = new JList<String>();
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = list.locationToIndex(e.getPoint());
				if (SwingUtilities.isRightMouseButton(e)&&index!=-1) {//client右鍵選單
					list.setSelectedIndex(index);
	                JPopupMenu menu = new JPopupMenu();
	                JMenuItem item;
	                JMenuItem item2;//Edit
	                JMenuItem item3;//Delete conf
	                String nameConf = (String) list.getSelectedValue();
	                if(Main.WS_Client_List.containsKey(nameConf)) {
	                	item = new JMenuItem("Disconnect");
	                	item.addMouseListener(new MouseAdapter() {
	                		@Override
	            			public void mousePressed(MouseEvent e) {//中斷連線
	                			WS_Client tmpWS = Main.WS_Client_List.get((String) list.getSelectedValue());
	                			EventSender.sendLog("Close connection : " + tmpWS.ud.sessionName);
	                			Main.localVS.delDevice(tmpWS.hashCode());
	                			tmpWS.close();
	                			Main.WS_Client_List.remove((String) list.getSelectedValue());
	                		}
	                	});
	                	menu.add(item);
	                }else {//連線
	                	item = new JMenuItem("Connect");
	                	item.addMouseListener(new MouseAdapter() {
	                		@Override
	            			public void mousePressed(MouseEvent e) {//連線
	                			Config cfg = new Config();
	                			cfg.setConf(((String) list.getSelectedValue()).split("\\.")[0], Config.ConfType.CLIENT);
	                			try {
									WS_Client tmpWS = new WS_Client(cfg);
									EventSender.sendLog("Starting connection : " + tmpWS.ud.sessionName);
									tmpWS.setPort(Main.localVS.addDevice(tmpWS));
									tmpWS.connect();
									Main.WS_Client_List.put((String) list.getSelectedValue(), tmpWS);
								} catch (URISyntaxException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
	                		}
	                	});
	                	item2 = new JMenuItem("Edit");
	                	item2.addMouseListener(new MouseAdapter() {
	                		@Override
	            			public void mousePressed(MouseEvent e) {
	                			String str = ((String) list.getSelectedValue()).split("\\.")[0];
	            				Config cfgTmp = new Config();
	            				cfgTmp.setConf(str, Config.ConfType.CLIENT);
	            				
	            				AddConfig aConf = new AddConfig(cfgTmp,Config.ConfType.CLIENT);
	            				aConf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	            				aConf.setVisible(true);
	                		}
	                	});
	                	
	                	item3 = new JMenuItem("Delete");
		                item3.addMouseListener(new MouseAdapter() {
	                		@Override
	            			public void mousePressed(MouseEvent e) {
	                			new File(path0 + (String) list.getSelectedValue()).delete();
	                			list.remove(list.getSelectedIndex());	                			
	                		}
	                	});
		                
		                menu.add(item);
		                menu.add(item2);
		                menu.add(item3);
	                }
	                
	                
	                
	                menu.show(list, e.getPoint().x, e.getPoint().y);
	            }
			}
			@Override
			public void mouseClicked(MouseEvent e) {//Double click to connect
				JList list = (JList)e.getSource();
		        if (e.getClickCount() == 2) {
		            // Double-click detected
		            int index = list.locationToIndex(e.getPoint());
		            String nameConf = (String) list.getModel().getElementAt(index);
		            if(Main.WS_Client_List.containsKey(nameConf)) {//連線
		            	Config cfg = new Config();
            			cfg.setConf(((String) list.getSelectedValue()).split("\\.")[0], Config.ConfType.CLIENT);
            			try {
							WS_Client tmpWS = new WS_Client(cfg);
							EventSender.sendLog("Starting connection : " + tmpWS.ud.sessionName);
							tmpWS.setPort(Main.localVS.addDevice(tmpWS));
							tmpWS.connect();
							Main.WS_Client_List.put((String) list.getSelectedValue(), tmpWS);
						} catch (URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		            }
		        }
			}
		});
		
		JScrollPane scrollPane_cli = new JScrollPane(list);
		scrollPane_cli.setBounds(318, 30, 307, 128);
		contentPane.add(scrollPane_cli);
		
		/**
		 * vSwitch list
		 */
		list_1 = new JList<String>();
		list_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = list_1.locationToIndex(e.getPoint());
				if (SwingUtilities.isRightMouseButton(e)&&index!=-1) {//vSwitch右鍵選單
					list_1.setSelectedIndex(index);
	                JPopupMenu menu = new JPopupMenu();
	                JMenuItem item;//Edit
	                JMenuItem item2;//Delete
	                JMenuItem item3;//setting
	                String nameConf = (String) list_1.getSelectedValue();
	                
	                item = new JMenuItem("Edit");
                	item.addMouseListener(new MouseAdapter() {
                		@Override
            			public void mousePressed(MouseEvent e) {//Edit
                			String str = ((String) list_1.getSelectedValue()).split("\\.")[0];
            				Config cfgTmp = new Config();
            				cfgTmp.setConf(str, Config.ConfType.SERVER);
            				
            				AddConfig aConf = new AddConfig(cfgTmp,Config.ConfType.SERVER);
            				aConf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            				aConf.setVisible(true);
            				aConf.cfgName.setEditable(false);
                		}
                	});
                	item2 = new JMenuItem("Delete");
                	item2.addMouseListener(new MouseAdapter() {
                		@Override
            			public void mousePressed(MouseEvent e) {//Delete
                			String str = ((String) list_1.getSelectedValue()).split("\\.")[0];
            				Config cfgTmp = new Config();
            				cfgTmp.setConf(str, Config.ConfType.SERVER);
            				
            				WS_Server.switchLs.get(cfgTmp.switchName).second.stop();
            				WS_Server.switchLs.remove(cfgTmp.switchName);
            				
            				new File(path1 + (String) list_1.getSelectedValue()).delete();
                			list_1.remove(list_1.getSelectedIndex());
                		}
                	});
                	
                	item3 = new JMenuItem("Setting");//開啟詳細設定視窗
                	item3.addMouseListener(new MouseAdapter() {
                		@Override
            			public void mousePressed(MouseEvent e) {
                			
                		}
                	});
                	
                	menu.add(item);
                	menu.add(item2);
                	menu.add(item3);
	                
	                menu.show(list_1, e.getPoint().x, e.getPoint().y);
				}
			}  
		});
		
		JScrollPane scrollPane_ser = new JScrollPane(list_1);
		scrollPane_ser.setBounds(318, 188, 307, 128);
		contentPane.add(scrollPane_ser);
		
		textField = new JTextField();
		textField.setBounds(363, 329, 251, 21);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("CMD");
		lblNewLabel.setBounds(328, 329, 36, 21);
		contentPane.add(lblNewLabel);
		
		JLabel lblClient = new JLabel("Client");
		lblClient.setBounds(318, 10, 46, 21);
		contentPane.add(lblClient);
		
		JLabel lblServer = new JLabel("VirtualSwitch");
		lblServer.setBounds(318, 168, 135, 21);
		contentPane.add(lblServer);
				
		textArea = new JTextArea();
		textArea.setRows(16);
		textArea.setColumns(42);
		textArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(10, 10, 298, 340);
		contentPane.add(scrollPane);
		
		if(BGVConfig.bgvConf.getConf("Tap")!=null&&BGVConfig.bgvConf.getConf("Tap").equalsIgnoreCase("true"))
			chckbxmntmNewCheckItem.setSelected(true);

		refreshJList();
		setIconImage(Main.icon.getImage());
	}
	
	public void refreshJList() {
		if(!new File("config").exists())
			new File("config").mkdirs();
		if(!new File("config/server").exists())
			new File("config/server").mkdirs();
		if(!new File("config/client").exists())
			new File("config/client").mkdirs();
		if(!new File("config/bgv.conf").exists())
			try {
				new File("config/bgv.conf").createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		File f = new File(path0);
		String[] clientCfgLs = f.list();
		f = new File(path1);
		String[] serverCfgLs = f.list();
		list.setListData(clientCfgLs);
		list_1.setListData(serverCfgLs);
	}
	
	/**
	 * clientName.conf
	 * @return
	 */
	public String getSelClient() {
		String ret = null;
		if(!list.isSelectionEmpty())
			ret = (String) list.getSelectedValue();
		return ret;
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onConsoleOutput(LogEvent le) {
		textArea.append(le.text+"\n");
	}
}
