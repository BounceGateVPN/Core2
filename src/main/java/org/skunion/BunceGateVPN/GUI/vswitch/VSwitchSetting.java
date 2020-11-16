package org.skunion.BunceGateVPN.GUI.vswitch;

import java.awt.EventQueue;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.smallru8.BounceGateVPN.Switch.VirtualSwitch;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.Pair;

import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class VSwitchSetting extends JFrame {

	private JPanel contentPane;
	private JTable swStat;
	private Vector<Vector> rowData = new Vector<Vector>();
	private Vector<String> columnNames = new Vector<String>();
	
	public Pair<Config,VirtualSwitch> swPair = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VSwitchSetting frame = new VSwitchSetting();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 傳入config,virtual switch
	 */
	public VSwitchSetting(Pair<Config,VirtualSwitch>...swPairLs) {
		if(swPairLs.length==1)
			swPair = swPairLs[0];
		columnNames.addElement("Type");
	    columnNames.addElement("Value");
	    
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 401);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(10, 26, 231, 182);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Layer2Layer");
		lblNewLabel_1.setBounds(10, 10, 113, 15);
		panel.add(lblNewLabel_1);
		
		JButton btnNewButton = new JButton("管理Bridge");
		btnNewButton.setBounds(108, 6, 113, 23);
		panel.add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("基本設定");
		lblNewLabel.setBounds(10, 10, 97, 15);
		contentPane.add(lblNewLabel);
		
		swStat = new JTable(rowData,columnNames);
		JScrollPane scrollPane = new JScrollPane(swStat);
		scrollPane.setBounds(251, 26, 173, 182);
		contentPane.add(scrollPane);
		
		JLabel lblNewLabel_2 = new JLabel("Switch狀態");
		lblNewLabel_2.setBounds(251, 10, 106, 15);
		contentPane.add(lblNewLabel_2);
		
		
		
		
		
		loadSwitchData();
	}
	
	private void loadSwitchData() {
		if(swPair!=null) {
			setTitle("Switch : " + swPair.first.switchName);
			
			//名稱
			Vector<String> switchName = new Vector<String>();
			switchName.addElement("Switch name");
			switchName.addElement(swPair.first.switchName);
			
			//連線數
			Vector<String> links = new Vector<String>();
			links.addElement("Connections");
			links.addElement(""+swPair.second.port.size());
		}
		swStat.repaint();
	}
}
