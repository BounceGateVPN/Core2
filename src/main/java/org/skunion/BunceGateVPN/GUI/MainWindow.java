package org.skunion.BunceGateVPN.GUI;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.log.Event.LogEvent;
import com.github.smallru8.util.log.EventSender;
import com.mysql.cj.log.Log;

import sun.rmi.server.Util;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JTextArea textArea;
	private JTextField textField;
	private JList list;//client
	private JList list_1;//server

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
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 10, 298, 340);
		contentPane.add(panel);
		panel.setLayout(null);
		
		//console output
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(10, 10, 278, 320);
		panel.add(textArea);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(318, 30, 307, 128);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		list = new JList();
		list.setBounds(10, 10, 287, 108);
		panel_1.add(list);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(318, 188, 307, 128);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		list_1 = new JList();
		list_1.setBounds(10, 10, 287, 108);
		panel_2.add(list_1);
		
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
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onConsoleOutput(LogEvent le) {
		textArea.append(le.text+"\n");
	}
}