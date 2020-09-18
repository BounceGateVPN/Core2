package org.skunion.BunceGateVPN.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.border.LineBorder;

import org.skunion.BunceGateVPN.core2.BGVConfig;
import com.github.smallru8.util.RegularExpression;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class ListenPortSetting extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JDialog jd = this;
	private JTextField portField;
	private JList portList;
	private JButton addButton;
	private JButton deleteButton;
	private DefaultListModel model = new DefaultListModel();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ListenPortSetting dialog = new ListenPortSetting();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ListenPortSetting() {
		setTitle("Listen port setting");
		setBounds(100, 100, 230, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			portList = new JList(model);
			portList.setBorder(new LineBorder(new Color(0, 0, 0)));
			portList.setBounds(10, 36, 93, 182);
			contentPanel.add(portList);
		}
		{
			JLabel lblNewLabel = new JLabel("Listen port");
			lblNewLabel.setBounds(10, 10, 93, 15);
			contentPanel.add(lblNewLabel);
		}
		{
			addButton = new JButton("Add");
			addButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {//Add port
					if(RegularExpression.isDigitOnly(portField.getText())) {
						model.addElement(portField.getText());
						String portLs = BGVConfig.bgvConf.getConf("Listen");
						portLs+=portField.getText()+",";
						BGVConfig.bgvConf.setConf("Listen", portLs);
					}
				}
			});
			addButton.setBounds(113, 66, 87, 23);
			contentPanel.add(addButton);
		}
		{
			deleteButton = new JButton("Delete");
			deleteButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {//delete port
					if(!portList.isSelectionEmpty()) {
						int index = portList.getSelectedIndex();
						String value = (String) portList.getSelectedValue();
						model.remove(index);
						ArrayList<String> portArray = new ArrayList<String>(Arrays.asList((BGVConfig.bgvConf.getConf("Listen")).split(",")));
						portArray.remove(value);
						String portLs = "";
						for(int i=0;i<portArray.size();i++)
							portLs+=portArray.get(i)+",";
						BGVConfig.bgvConf.setConf("Listen", portLs);
					}
				}
			});
			deleteButton.setBounds(113, 99, 87, 23);
			contentPanel.add(deleteButton);
		}
		{
			portField = new JTextField();
			portField.setToolTipText("1~65535");
			portField.setBounds(113, 35, 87, 21);
			contentPanel.add(portField);
			portField.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Close");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						jd.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		String portLs = BGVConfig.bgvConf.getConf("Listen");
		if(portLs!=null) {
			String[] portArray = portLs.split(",");
			for(int i=0;i<portArray.length;i++) {
				if(!portArray[i].equals(""))
					model.addElement(portArray[i]);
			}
		}
	}

}
