package org.skunion.BunceGateVPN.GUI.vrouter;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.github.Mealf.BounceGateVPN.Router.VirtualRouter;
import com.github.smallru8.BounceGateVPN.Switch.VirtualSwitch;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.Pair;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VRouterSetting extends JFrame {

	private JPanel contentPane;

	private Pair<Config,VirtualRouter> roPair = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VRouterSetting frame = new VRouterSetting();
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
	public VRouterSetting(Pair<Config,VirtualRouter>...roPairLs) {
		if(roPairLs.length==1)
			roPair = roPairLs[0];
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 401);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("基本設定");
		lblNewLabel.setBounds(10, 10, 102, 15);
		contentPane.add(lblNewLabel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(10, 26, 231, 182);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("IP");
		lblNewLabel_1.setBounds(10, 10, 87, 15);
		panel.add(lblNewLabel_1);
		
		JButton btnNewButton = new JButton("設定");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//開啟IP,Mask設定頁面
				SetIP dialog = new SetIP(roPair);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		});
		btnNewButton.setBounds(95, 6, 126, 23);
		panel.add(btnNewButton);
		
		JLabel lblNewLabel_1_1 = new JLabel("Routing table");
		lblNewLabel_1_1.setBounds(10, 39, 87, 15);
		panel.add(lblNewLabel_1_1);
		
		JButton btnNewButton_1 = new JButton("設定");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//開啟routing table設定頁面
				RoutingTableSetting frame = new RoutingTableSetting(roPair);
				frame.setVisible(true);
			}
		});
		btnNewButton_1.setBounds(95, 35, 126, 23);
		panel.add(btnNewButton_1);
		
		JLabel lblNewLabel_1_1_1 = new JLabel("Layer2Layer");
		lblNewLabel_1_1_1.setBounds(10, 69, 87, 15);
		panel.add(lblNewLabel_1_1_1);
		
		JButton btnNewButton_2 = new JButton("管理Bridge");
		btnNewButton_2.setBounds(95, 65, 126, 23);
		panel.add(btnNewButton_2);
	}
}
