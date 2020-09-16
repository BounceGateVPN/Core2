package org.skunion.BunceGateVPN.GUI;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.skunion.BunceGateVPN.core2.BGVConfig;
import org.skunion.BunceGateVPN.core2.Main;

import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.log.Event.LogEvent;
import com.github.smallru8.util.log.EventSender;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JTextArea textArea;
	private JTextField textField;
	private JList list;//client
	private JList list_1;//server
	private JButton editClientCfg;
	private JButton editServerCfg;
	private JCheckBoxMenuItem chckbxmntmNewCheckItem;
	
	private String path0 = "config/client/";
	private String path1 = "config/server/";
	
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
		EventBus.getDefault().register(this);
		setTitle("BunceGateVPN");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 651, 422);
		
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
		chckbxmntmNewCheckItem.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(chckbxmntmNewCheckItem.isSelected()) {
					BGVConfig.bgvConf.setConf("Tap", "true");
					Main.td.runFlag = true;
					Main.td.run();
				}else {
					BGVConfig.bgvConf.setConf("Tap", "false");
					Main.td.runFlag = false;
					Main.td.stop();
				}
			}
		});
		mnNewMenu_1.add(chckbxmntmNewCheckItem);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(10, 10, 298, 340);
		contentPane.add(panel);
		panel.setLayout(null);
		
		//console output
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(10, 10, 278, 320);
		panel.add(textArea);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(318, 30, 307, 128);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		list = new JList();
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!list.isSelectionEmpty()) {
					editClientCfg.setEnabled(true);
				}else {
					editClientCfg.setEnabled(false);
				}
			}
		});
		list.setBounds(10, 10, 219, 108);
		panel_1.add(list);
		
		editClientCfg = new JButton("Edit");
		editClientCfg.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//編輯 client config
				String str = ((String) list.getSelectedValue()).split("\\.")[0];
				Config cfgTmp = new Config();
				cfgTmp.setConf(str, Config.ConfType.CLIENT);
				
				AddConfig aConf = new AddConfig(cfgTmp,Config.ConfType.CLIENT);
				aConf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				aConf.setVisible(true);
			}
		});
		editClientCfg.setBounds(239, 10, 58, 23);
		panel_1.add(editClientCfg);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setBounds(318, 188, 307, 128);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		list_1 = new JList();
		list_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!list_1.isSelectionEmpty()) {
					editServerCfg.setEnabled(true);
				}else {
					editServerCfg.setEnabled(false);
				}
			}
		});
		list_1.setBounds(10, 10, 219, 108);
		panel_2.add(list_1);
		
		editServerCfg = new JButton("Edit");
		editServerCfg.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//編輯 vSwitch config
				String str = ((String) list_1.getSelectedValue()).split("\\.")[0];
				Config cfgTmp = new Config();
				cfgTmp.setConf(str, Config.ConfType.SERVER);
				
				AddConfig aConf = new AddConfig(cfgTmp,Config.ConfType.SERVER);
				aConf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				aConf.setVisible(true);
			}
		});
		editServerCfg.setBounds(239, 10, 58, 23);
		panel_2.add(editServerCfg);
		
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
		
		JLabel lblServer = new JLabel("Server");
		lblServer.setBounds(318, 168, 46, 21);
		contentPane.add(lblServer);
		
		Main.localVS.run();//啟動Switch
		Main.td.startEthernetDev(Main.localVS.addDevice(Main.td));
		if(BGVConfig.bgvConf.getConf("Tap")!=null&&BGVConfig.bgvConf.getConf("Tap").equalsIgnoreCase("true")) {
			chckbxmntmNewCheckItem.setSelected(true);
			Main.td.runFlag = true;
			Main.td.run();
		}else {
			BGVConfig.bgvConf.setConf("Tap", "false");
			Main.td.runFlag = false;
			Main.td.stop();
		}
		editClientCfg.setEnabled(false);
		editServerCfg.setEnabled(false);
		refreshJList();
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
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onConsoleOutput(LogEvent le) {
		textArea.append(le.text+"\n");
	}
}
