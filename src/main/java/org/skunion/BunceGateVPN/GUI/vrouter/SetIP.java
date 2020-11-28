package org.skunion.BunceGateVPN.GUI.vrouter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import com.github.Mealf.BounceGateVPN.Router.VirtualRouter;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.Pair;
import com.github.smallru8.util.RegularExpression;
import com.github.smallru8.util.log.EventSender;

import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

public class SetIP extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JDialog SETIP = this;
	private Pair<Config,VirtualRouter> roPair = null;
	
	private JTextField[] IPField;
	private JTextField[] MaskField;
	
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;

	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			SetIP dialog = new SetIP();
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
	public SetIP(Pair<Config,VirtualRouter>...i_roPair) {
		if(i_roPair.length==1)
			roPair = i_roPair[0];
		setTitle("Router IP");
		setBounds(100, 100, 505, 143);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		try{
			IPField = new JTextField[4];
			MaskField = new JTextField[4];
			{
				IPField[0] = new JFormattedTextField(new MaskFormatter("***"));
				IPField[0].setBounds(65, 10, 96, 21);
				contentPanel.add(IPField[0]);
				IPField[0].setColumns(10);
			}
			{
				IPField[1] = new JFormattedTextField(new MaskFormatter("***"));
				IPField[1].setBounds(171, 10, 96, 21);
				contentPanel.add(IPField[1]);
				IPField[1].setColumns(10);
			}
			{
				IPField[2] = new JFormattedTextField(new MaskFormatter("***"));
				IPField[2].setBounds(277, 10, 96, 21);
				contentPanel.add(IPField[2]);
				IPField[2].setColumns(10);
			}
			{
				IPField[3] = new JFormattedTextField(new MaskFormatter("***"));
				IPField[3].setBounds(383, 10, 96, 21);
				contentPanel.add(IPField[3]);
				IPField[3].setColumns(10);
			}
			{
				MaskField[0] = new JFormattedTextField(new MaskFormatter("***"));
				MaskField[0].setBounds(65, 41, 96, 21);
				contentPanel.add(MaskField[0]);
				MaskField[0].setColumns(10);
			}
			{
				MaskField[1] = new JFormattedTextField(new MaskFormatter("***"));
				MaskField[1].setBounds(171, 41, 96, 21);
				contentPanel.add(MaskField[1]);
				MaskField[1].setColumns(10);
			}
			{
				MaskField[2] = new JFormattedTextField(new MaskFormatter("***"));
				MaskField[2].setBounds(277, 41, 96, 21);
				contentPanel.add(MaskField[2]);
				MaskField[2].setColumns(10);
			}
			{
				MaskField[3] = new JFormattedTextField(new MaskFormatter("***"));
				MaskField[3].setBounds(383, 41, 96, 21);
				contentPanel.add(MaskField[3]);
				MaskField[3].setColumns(10);
			}
			{
				lblNewLabel = new JLabel("IP");
				lblNewLabel.setBounds(10, 13, 46, 15);
				contentPanel.add(lblNewLabel);
			}
			{
				lblNewLabel_1 = new JLabel("Mask");
				lblNewLabel_1.setBounds(10, 44, 46, 15);
				contentPanel.add(lblNewLabel_1);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						boolean passFlag = true;
						for(int i=0;i<4;i++) {
							if(!(isIPFormat(IPField[i].getText())||isIPFormat(MaskField[i].getText()))) {
								passFlag = false;
								break;
							}
						}
						if(passFlag) {//存檔
							String ip_str = "";
							String mask_str = "";
							for(int i=0;i<4;i++) {
								ip_str+=IPField[i].getText();
								mask_str+=MaskField[i].getText();
							}
							roPair.first.pro.setProperty("ip", ip_str);
							roPair.first.pro.setProperty("netmask",mask_str);
							roPair.first.saveConf();
							
							roPair.second.setIP(ip_str);
							roPair.second.setMask(mask_str);
							EventSender.sendLog("Change router:"+roPair.first.confName+"'s IP to\n"+ip_str+", "+mask_str);
							SETIP.dispose();
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
					public void mouseClicked(MouseEvent e) {
						SETIP.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		loadData();
	}
	
	private void loadData() {
		if(roPair!=null) {
			String[] ip_str = roPair.first.ip.split("\\.");
			String[] mask_str = roPair.first.netmask.split("\\.");
			for(int i=0;i<4;i++) {
				IPField[i].setText(ip_str[i]);
				MaskField[i].setText(mask_str[i]);
			}	
		}
	}
	
	private boolean isIPFormat(String str) {
		if(RegularExpression.isDigitOnly(str)&&Integer.parseInt(str)>=0&&Integer.parseInt(str)<=255)
			return true;
		return false;
	}
	
}
