package org.skunion.BunceGateVPN.GUI.vswitch;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.skunion.BunceGateVPN.core2.Layer2Layer;

import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeleteBr extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JDialog DB = this;
	private String switchName = null;
	private String routerName = null;//
	private JList<String> targetLs;
	private ArrayList<Layer2Layer> swL2L;
	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			DeleteBr dialog = new DeleteBr();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Create the dialog.
	 * @param strs switch name
	 */
	public DeleteBr(String...strs) {
		if(strs.length==1)
			switchName = strs[0];
		
		setBounds(100, 100, 326, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		targetLs = new JList<String>();
		JScrollPane scrollPane = new JScrollPane(targetLs);
		scrollPane.setBounds(10, 10, 207, 208);
		contentPanel.add(scrollPane);
		

		JButton btnNewButton = new JButton("Remove");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//Remove
				int index = targetLs.getSelectedIndex();
				if(index!=-1) {
					swL2L.get(index).deleteBr();
					loadSwitchData();//refresh
				}
			}
		});
		btnNewButton.setBounds(227, 10, 73, 23);
		contentPanel.add(btnNewButton);
		
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton cancelButton = new JButton("Close");
			cancelButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					DB.dispose();
				}
			});
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
		}
		
		loadSwitchData();
	}
	
	private void loadSwitchData() {
		if(switchName!=null) {
			swL2L = Layer2Layer.getBrbyName(switchName);
			String[] strLs = new String[swL2L.size()];
			
			for(int i=0;i<strLs.length;i++) {
				String tmp = "";
				if(swL2L.get(i).vswitch!=null) {
					for(int j=0;j<swL2L.get(i).vswitch.size();j++) {
						tmp+="s_"+swL2L.get(i).vswitch.get(j).name+",";
					}
				}
				if(swL2L.get(i).vrouter!=null) {
					for(int j=0;j<swL2L.get(i).vrouter.size();j++) {
						tmp+="s_"+swL2L.get(i).vrouter.get(j).name+",";
					}
				}
				strLs[i] = tmp;
			}
			targetLs.setListData(strLs);
		}
	}

}
