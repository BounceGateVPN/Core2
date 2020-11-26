package org.skunion.BunceGateVPN.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RouterInterfaceList extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private JDialog RI = this;
	
	private JList<String> ifLs;//ifName.conf
	
	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			RouterInterfaceList dialog = new RouterInterfaceList();
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
	public RouterInterfaceList() {
		setBounds(100, 100, 259, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		ifLs = new JList<String>();
		ifLs.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = ifLs.locationToIndex(e.getPoint());
				if (SwingUtilities.isRightMouseButton(e)&&index!=-1) {//右鍵且有選擇項目
					ifLs.setSelectedIndex(index);
	                JPopupMenu menu = new JPopupMenu();
	                JMenuItem item;//Delete
	                
	                item = new JMenuItem("Delete");
	                item.addMouseListener(new MouseAdapter() {
                		@Override
            			public void mousePressed(MouseEvent e) {//Delete
                			File f = new File("config/interface/"+(String) ifLs.getSelectedValue());
                			f.delete();
                			ifLs.remove(ifLs.getSelectedIndex());
                		}
	                });
	                menu.add(item);
	                menu.show(ifLs, e.getPoint().x, e.getPoint().y);
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(ifLs);
		scrollPane.setBounds(10, 10, 223, 208);
		contentPanel.add(scrollPane);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						RI.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		loadData();
	}
	
	private void loadData() {
		if(new File("config/interface/").exists()) {
			File f = new File("config/interface/");
			String[] fLs = f.list();
			ifLs.setListData(fLs);
		}
	}
}
