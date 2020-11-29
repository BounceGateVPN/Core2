package org.skunion.BunceGateVPN.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.skunion.BunceGateVPN.core2.Layer2Layer;
import org.skunion.BunceGateVPN.core2.websocket.WS_Server;

import com.github.Mealf.BounceGateVPN.Router.VirtualRouter;
import com.github.smallru8.BounceGateVPN.Switch.VirtualSwitch;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.Pair;

import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map.Entry;

public class CreateBr extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5583190885555505357L;
	private JDialog CB= this;
	private final JPanel contentPanel = new JPanel();
	private JButton routerBtn_left;
	private JButton switchBtn_left;
	private JButton routerBtn_right;
	private JButton switchBtn_right;
	private String[] switchNameLs;
	private String[] routerNameLs;
	private JList<String> jl_left;
	private JList<String> jl_right;
	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			CreateBr dialog = new CreateBr();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	/**
	 * Create the dialog.
	 */
	public CreateBr() {
		setTitle("Create Layer2Layer bridge");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		jl_left = new JList<String>();
		
		JScrollPane scrollPane = new JScrollPane(jl_left);
		scrollPane.setBounds(10, 38, 202, 182);
		contentPanel.add(scrollPane);
		
		jl_right = new JList<String>();
		JScrollPane scrollPane_1 = new JScrollPane(jl_right);
		scrollPane_1.setBounds(224, 38, 202, 182);
		contentPanel.add(scrollPane_1);
		
		/**
		 * 載入switch/router資料
		 */
		ArrayList<String> swNameLs = new ArrayList<String>();
		ArrayList<String> roNameLs = new ArrayList<String>();
		for(Entry<String, Pair<Config, VirtualSwitch>> entry : WS_Server.switchLs.entrySet())
			swNameLs.add(entry.getKey());
		switchNameLs = new String[swNameLs.size()];
		switchNameLs = swNameLs.toArray(switchNameLs);
		for(Entry<String, Pair<Config, VirtualRouter>> entry : WS_Server.routerLs.entrySet())
			roNameLs.add(entry.getKey());
		routerNameLs = new String[roNameLs.size()];
		routerNameLs = roNameLs.toArray(routerNameLs);
		
		routerBtn_left = new JButton("Router");
		switchBtn_left = new JButton("Switch");
		switchBtn_left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				switchBtn_left.setEnabled(false);
				routerBtn_left.setEnabled(true);
				//load switch list from WS_Server.switchLs
				jl_left.setListData(switchNameLs);
			}
		});

		switchBtn_left.setBounds(10, 10, 85, 23);
		contentPanel.add(switchBtn_left);
		routerBtn_left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				switchBtn_left.setEnabled(true);
				routerBtn_left.setEnabled(false);
				//load router list from WS_Server.routerLs
				jl_left.setListData(routerNameLs);
			}
		});
		routerBtn_left.setBounds(105, 10, 85, 23);
		contentPanel.add(routerBtn_left);
		
		routerBtn_right = new JButton("Router");
		switchBtn_right = new JButton("Switch");
		switchBtn_right.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				switchBtn_right.setEnabled(false);
				routerBtn_right.setEnabled(true);
				//load switch list from WS_Server.switchLs
				jl_right.setListData(switchNameLs);
			}
		});
		switchBtn_right.setBounds(224, 10, 85, 23);
		contentPanel.add(switchBtn_right);
		
		routerBtn_right.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				switchBtn_right.setEnabled(true);
				routerBtn_right.setEnabled(false);
				//load router list from WS_Server.routerLs
				jl_right.setListData(routerNameLs);
			}
		});
		routerBtn_right.setBounds(319, 10, 85, 23);
		contentPanel.add(routerBtn_right);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {//建立bridge				
						if((!switchBtn_left.isEnabled())&&routerBtn_left.isEnabled()) {//左邊選switch
							if((!switchBtn_right.isEnabled())&&routerBtn_right.isEnabled()) {//右邊選switch
								new Layer2Layer(WS_Server.switchLs.get(jl_left.getSelectedValue()).second,WS_Server.switchLs.get(jl_right.getSelectedValue()).second);
							}else if(switchBtn_right.isEnabled()!=routerBtn_right.isEnabled()){//右邊選router
								new Layer2Layer(WS_Server.switchLs.get(jl_left.getSelectedValue()).second,WS_Server.routerLs.get(jl_right.getSelectedValue()).second);
							}
						}else if(switchBtn_left.isEnabled()!=routerBtn_left.isEnabled()){//左邊選router
							if((!switchBtn_right.isEnabled())&&routerBtn_right.isEnabled()) {//右邊選switch
								new Layer2Layer(WS_Server.routerLs.get(jl_left.getSelectedValue()).second,WS_Server.switchLs.get(jl_right.getSelectedValue()).second);
							}else if(switchBtn_right.isEnabled()!=routerBtn_right.isEnabled()){//右邊選router
								new Layer2Layer(WS_Server.routerLs.get(jl_left.getSelectedValue()).second,WS_Server.routerLs.get(jl_right.getSelectedValue()).second);
							}
						}
						
						CB.dispose();
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
						CB.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
