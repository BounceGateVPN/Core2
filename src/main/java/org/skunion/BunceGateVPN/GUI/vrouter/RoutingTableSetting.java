package org.skunion.BunceGateVPN.GUI.vrouter;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.Mealf.BounceGateVPN.Router.VirtualRouter;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.Pair;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class RoutingTableSetting extends JFrame {

	private JPanel contentPane;
	private JFrame RTS = this;
	private JTable routingTable;
	
	private Pair<Config,VirtualRouter> roPair = null;
	private Vector<Vector> rowData = new Vector<Vector>();
	private Vector<String> routingTable_column = new Vector<String>();
	
	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RoutingTableSetting frame = new RoutingTableSetting();
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
	public RoutingTableSetting(Pair<Config,VirtualRouter>...roPairLs) {
		if(roPairLs.length==1)
			roPair = roPairLs[0];
		routingTable_column.addElement("DesIP");
		routingTable_column.addElement("Mask");
		routingTable_column.addElement("Gateway");
		routingTable_column.addElement("Interface/switch");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 534, 395);
		setTitle("Routing table");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		routingTable = new JTable(rowData,routingTable_column);
		routingTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(routingTable);
		scrollPane.setBounds(10, 10, 498, 297);
		contentPane.add(scrollPane);
		
		JButton closeButton = new JButton("Close");
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				RTS.dispose();
			}
		});
		closeButton.setBounds(421, 317, 87, 23);
		contentPane.add(closeButton);
		
		JButton editButton = new JButton("Edit");
		editButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = routingTable.getSelectedRow();
				if(row!=-1) {
					RoutingTableDataSetting dialog = new RoutingTableDataSetting(rowData.get(row),roPair);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				}
			}
		});
		editButton.setBounds(10, 317, 87, 23);
		contentPane.add(editButton);
		
		JButton addRowButton = new JButton("Add");
		addRowButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//Add a empty row
				RoutingTableDataSetting dialog = new RoutingTableDataSetting(null,roPair);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		});
		addRowButton.setBounds(107, 317, 87, 23);
		contentPane.add(addRowButton);
		
		JButton delRowButton = new JButton("Remove");
		delRowButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//delete row
				int row = routingTable.getSelectedRow();
				if(row!=-1) {
					String[] routingData = new String[3];
					for(int i=0;i<3;i++)
						routingData[i] = (String)routingTable.getValueAt(row, i);
					roPair.second.delRoutingTable(routingData[0], routingData[1], routingData[2]);//從router移除
					
					ArrayList<String> routingTable_raw = new ArrayList<String>();
					routingTable_raw.addAll(Arrays.asList(roPair.first.routingTable.split(";")));
					for(int i=0;i<routingTable_raw.size();i++) {//刪cfg data
						String[] routingDataTmp = routingTable_raw.get(i).split(",");
						if(routingDataTmp[0].equals(routingData[0])&&routingDataTmp[1].equals(routingData[1])&&routingDataTmp[2].equals(routingData[2])) {
							routingTable_raw.remove(i);
						}
					}
					String routingTable_str = "";
					for(int i=0;i<routingTable_raw.size();i++) 
						routingTable_str+=routingTable_raw.get(i)+";";
					roPair.first.pro.setProperty("routingTable", routingTable_str);//存回cfg
					roPair.first.saveConf();
					
					routingTable.repaint();
				}
			}
		});
		delRowButton.setBounds(204, 317, 87, 23);
		contentPane.add(delRowButton);
		
		JButton btnNewButton = new JButton("Refresh");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rowData.clear();
				loadData();
			}
		});
		btnNewButton.setBounds(324, 317, 87, 23);
		contentPane.add(btnNewButton);
		
		loadData();
	}
	
	private void loadData() {
		if(roPair!=null) {
			String[] routingTable_RawData = roPair.first.routingTable.split(";");
			if(routingTable_RawData.length>0) {
				for(int i=0;i<routingTable_RawData.length;i++) {
					routingTable_RawData[i].replace(" ", "");//過濾空白
					String[] routingData = routingTable_RawData[i].split(",");
					if(routingData.length!=4)//資料有問題
						continue;
					Vector<String> routingData_v = new Vector<String>();
					routingData_v.addAll(Arrays.asList(routingData));
					
					rowData.addElement(routingData_v);
				}
				routingTable.repaint();
			}
		}
	}
	
}
