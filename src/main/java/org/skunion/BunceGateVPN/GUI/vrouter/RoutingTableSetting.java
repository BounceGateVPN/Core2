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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
		
		JButton saveButton = new JButton("Save");
		saveButton.setBounds(324, 317, 87, 23);
		contentPane.add(saveButton);
		
		JButton addRowButton = new JButton("Add");
		addRowButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//Add a empty row
				Vector<String> v_tmp = new Vector<String>();
				v_tmp.addElement("");
				v_tmp.addElement("");
				v_tmp.addElement("");
				v_tmp.addElement("");
				rowData.addElement(v_tmp);
				routingTable.repaint();
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
					rowData.remove(row);//移除Table顯示
					saveTable();//存檔
					routingTable.repaint();
				}
			}
		});
		delRowButton.setBounds(10, 317, 87, 23);
		contentPane.add(delRowButton);
		
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
	
	private void saveTable() {
		String routingTable_raw = "";
		for(Vector<String> v:rowData) {
			if(v.elementAt(0).length()>0&&v.elementAt(1).length()>0&&v.elementAt(2).length()>0&&v.elementAt(3).length()>0) {
				routingTable_raw+=v.elementAt(0)+",";
				routingTable_raw+=v.elementAt(1)+",";
				routingTable_raw+=v.elementAt(2)+",";
				routingTable_raw+=v.elementAt(3)+";";
			}
		}
		roPair.first.pro.setProperty("routingTable", routingTable_raw);
		roPair.first.saveConf();
	}
	
}
