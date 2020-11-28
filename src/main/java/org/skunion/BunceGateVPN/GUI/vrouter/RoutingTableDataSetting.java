package org.skunion.BunceGateVPN.GUI.vrouter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.Mealf.BounceGateVPN.Router.VirtualRouter;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.Pair;
import com.github.smallru8.util.RegularExpression;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoutingTableDataSetting extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JDialog RTDS = this;
	
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JComboBox<String> comboBox;

	private Pair<Config,VirtualRouter> roPair = null;
	private Vector<String> data = null;
	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			RoutingTableDataSetting dialog = new RoutingTableDataSetting(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/

	/**
	 * 
	 * @param data_i
	 * @param roPairLs
	 */
	public RoutingTableDataSetting(Vector<String> data_i,Pair<Config,VirtualRouter>...roPairLs) {
		if(roPairLs.length==1)
			roPair = roPairLs[0];
		if(data_i!=null)
			data = data_i;
		else {
			data = new Vector<String>();
			data.addElement("");
			data.addElement("");
			data.addElement("");
			data.addElement("");
		}
		setBounds(100, 100, 450, 144);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			textField = new JTextField();
			textField.setBounds(10, 40, 96, 21);
			contentPanel.add(textField);
			textField.setColumns(10);
		}
		{
			textField_1 = new JTextField();
			textField_1.setColumns(10);
			textField_1.setBounds(116, 40, 96, 21);
			contentPanel.add(textField_1);
		}
		{
			textField_2 = new JTextField();
			textField_2.setColumns(10);
			textField_2.setBounds(222, 40, 96, 21);
			contentPanel.add(textField_2);
		}
		
		comboBox = new JComboBox<String>();
		comboBox.setBounds(328, 39, 96, 23);
		comboBox.insertItemAt("switch", 0);
		comboBox.insertItemAt("interface", 1);
		comboBox.setSelectedIndex(0);
		contentPanel.add(comboBox);
		
		JLabel lblNewLabel = new JLabel("DesIP");
		lblNewLabel.setBounds(36, 15, 46, 15);
		contentPanel.add(lblNewLabel);
		
		JLabel lblMask = new JLabel("Mask");
		lblMask.setBounds(139, 15, 46, 15);
		contentPanel.add(lblMask);
		
		JLabel lblGateway = new JLabel("Gateway");
		lblGateway.setBounds(247, 15, 46, 15);
		contentPanel.add(lblGateway);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(357, 15, 46, 15);
		contentPanel.add(lblPort);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {//save
						if(dataVerify()) {
							saveData();
							RTDS.dispose();
						}
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
					public void mouseClicked(MouseEvent e) {//close
						RTDS.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		loadData();
	}
	
	private void loadData() {
		if(data!=null&&roPair!=null) {
			textField.setText(data.get(0));
			textField_1.setText(data.get(1));
			textField_2.setText(data.get(2));
			if(data.get(3).equalsIgnoreCase("switch"))
				comboBox.setSelectedIndex(0);
			else
				comboBox.setSelectedIndex(1);
		}
	}
	
	/**
	 * 驗證輸入的值是合法IPv4格式
	 * @return
	 */
	private boolean dataVerify() {
		return RegularExpression.isIPAddress(textField.getText())&&RegularExpression.isIPAddress(textField_1.getText())&&RegularExpression.isIPAddress(textField_2.getText());
	}
	
	/**
	 * 更新到router並存檔
	 * 先刪除router舊紀錄再寫入
	 * 從cgf刪除再寫入存檔
	 */
	private void saveData() {
		roPair.second.delRoutingTable(data.get(0), data.get(1), data.get(2));//刪router data
		
		ArrayList<String> routingTable_raw = new ArrayList<String>();
		routingTable_raw.addAll(Arrays.asList(roPair.first.routingTable.split(";")));
		
		for(int i=0;i<routingTable_raw.size();i++) {//刪cfg data
			String[] routingData = routingTable_raw.get(i).split(",");
			if(routingData[0].equals(data.get(0))&&routingData[1].equals(data.get(1))&&routingData[2].equals(data.get(2))) {
				routingTable_raw.remove(i);
			}
		}
		
		data.set(0, textField.getText());
		data.set(1, textField_1.getText());
		data.set(2, textField_2.getText());
		data.set(3, (String)comboBox.getSelectedItem());
		
		roPair.second.addRoutingTable(data.get(0), data.get(1), data.get(2), data.get(3));//加入路由
		String routingData = data.get(0)+","+data.get(1)+","+data.get(2)+","+data.get(3);
		routingTable_raw.add(routingData);
		
		String routingTable_str = "";
		for(int i=0;i<routingTable_raw.size();i++) 
			routingTable_str+=routingTable_raw.get(i)+";";
		
		roPair.first.pro.setProperty("routingTable", routingTable_str);//存回cfg
		roPair.first.saveConf();
	}
}
