package org.skunion.BunceGateVPN.core2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.skunion.BunceGateVPN.core2.websocket.WS_Server;

import com.github.Mealf.BounceGateVPN.Router.VirtualRouter;
import com.github.smallru8.BounceGateVPN.Switch.VirtualSwitch;
import com.github.smallru8.BounceGateVPN.bridge.Bridge;

/**
 * 新增、建立Bridge
 * 建立時自動檢查br是否已存在，建立成功自動存檔
 * 
 * 程式啟動時須執行loadData();
 * @author smallru8
 *
 */
public class Layer2Layer {

	//記錄所有橋接器
	public static ArrayList<Layer2Layer> L2L = new ArrayList<Layer2Layer>();
	
	private int type = 0;
	public ArrayList<VirtualSwitch> vswitch = null;
	public ArrayList<VirtualRouter> vrouter = null;
	public Bridge br;
	
	public Layer2Layer(VirtualSwitch vs1,VirtualSwitch vs2) {//0
		for(Layer2Layer e : L2L) {
			if(e.type==0&&(e.vswitch.get(0).equals(vs1)&&e.vswitch.get(1).equals(vs2))||(e.vswitch.get(0).equals(vs2)&&e.vswitch.get(1).equals(vs1))) {
				type = -1;//already exist
				break;
			}
		}
		if(type!=-1) {
			vswitch = new ArrayList<VirtualSwitch>();
			vswitch.add(vs1);
			vswitch.add(vs2);
			createBr();
		}
	}
	public Layer2Layer(VirtualRouter vr1,VirtualRouter vr2) {//1
		for(Layer2Layer e : L2L) {
			if(e.type==0&&(e.vrouter.get(0).equals(vr1)&&e.vrouter.get(1).equals(vr2))||(e.vrouter.get(0).equals(vr2)&&e.vrouter.get(1).equals(vr1))) {
				type = -1;//already exist
				break;
			}
		}
		if(type!=-1) {
			vrouter = new ArrayList<VirtualRouter>();
			vrouter.add(vr1);
			vrouter.add(vr2);
			type = 1;
			createBr();
		}
	}
	public Layer2Layer(VirtualSwitch vs,VirtualRouter vr) {//2
		for(Layer2Layer e : L2L) {
			if(e.type==0&&e.vswitch.equals(vs)&&e.vrouter.equals(vr)) {
				type = -1;//already exist
				break;
			}
		}
		if(type!=-1) {
			vswitch = new ArrayList<VirtualSwitch>();
			vswitch.add(vs);
			vrouter = new ArrayList<VirtualRouter>();
			vrouter.add(vr);
			type = 2;
			createBr();
		}
	}
	public Layer2Layer(VirtualRouter vr,VirtualSwitch vs) {//2
		for(Layer2Layer e : L2L) {
			if(e.type==0&&e.vswitch.equals(vs)&&e.vrouter.equals(vr)) {
				type = -1;//already exist
				break;
			}
		}
		if(type!=-1) {
			vswitch = new ArrayList<VirtualSwitch>();
			vswitch.add(vs);
			vrouter = new ArrayList<VirtualRouter>();
			vrouter.add(vr);
			type = 2;
		createBr();
		}
	}
	
	/**
	 * 比對輸入的names是否都符合,查這組是否已經存在
	 * @param strings
	 * @return
	 */
	public boolean containName(String... strings) {
		int counter = 0;
		if(type==0)
			for(int i=0;i<strings.length;i++) {
				if(strings[i].equals(vswitch.get(0).name)||strings[i].equals(vswitch.get(1).name))
					counter++;
			}
		else if(type==1) {
			for(int i=0;i<strings.length;i++) {
				if(strings[i].equals(vrouter.get(0).name)||strings[i].equals(vrouter.get(1).name))
					counter++;
			}
		}else {
			for(int i=0;i<strings.length;i++) {
				if(strings[i].equals(vswitch.get(0).name)||strings[i].equals(vrouter.get(0).name))
					counter++;
			}
		}
		
		if(counter==strings.length)
			return true;
		else
			return false;
	}
	
	/**
	 * 刪除橋接器
	 */
	public void deleteBr() {
		br.close();
		L2L.remove(this);
		saveData();
	}
	
	
	/**
	 * 建立橋接器
	 */
	private void createBr() {
		if(type==0) {//switch to switch
			br = new Bridge(vswitch.get(0),vswitch.get(1));
		}else if(type==1) {
			//暫不支援router to router
		}else {//switch to router
			br = new Bridge(vswitch.get(0),vrouter.get(0));
		}
		L2L.add(this);
		saveData();
	}
	
	/**
	 * 查特定設備的br
	 * @param name
	 * @return
	 */
	public static ArrayList<Layer2Layer> getBrbyName(String name){
		ArrayList<Layer2Layer> l2lLs = new ArrayList<Layer2Layer>();
		for(int i=0;i<L2L.size();i++) {
			if(L2L.get(i).containName(name))
				l2lLs.add(L2L.get(i));
		}
		return l2lLs;
	}
	
	private static void saveData() {
		File f = new File("config/L2L.conf");
		if(!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		try {
			FileWriter fw = new FileWriter(f);//格式 : s,n1,r,n2
			for(int i=0;i<L2L.size();i++) {
				if(L2L.get(i).type == 0) {//s,n1,s,n2
					fw.write("s,"+L2L.get(i).vswitch.get(0).name+",");
					fw.write("s,"+L2L.get(i).vswitch.get(1).name+"\n");
				}else if(L2L.get(i).type == 1){//r,n1,r,n2
					fw.write("r,"+L2L.get(i).vrouter.get(0).name+",");
					fw.write("r,"+L2L.get(i).vrouter.get(1).name+"\n");
				}else {//s,n1,r,n2
					fw.write("s,"+L2L.get(i).vswitch.get(0).name+",");
					fw.write("r,"+L2L.get(i).vrouter.get(0).name+"\n");
				}
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 所有switch,router都啟動後再執行
	 * 載入bridge
	 */
	public static void loadData() {
		File f = new File("config/L2L.conf");
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				ArrayList<String> strs = new ArrayList<String>();
				String tmp = null;
				while((tmp = br.readLine())!=null)
					strs.add(tmp);
				br.close();
				fr.close();
				
				for(int i=0;i<strs.size();i++) {
					String[] tmpLs = strs.get(0).split(",");
					if(tmpLs.length!=4)
						continue;
	
					if(tmpLs[0].equalsIgnoreCase("s")&&tmpLs[2].equalsIgnoreCase("s")) {//switch to switch
						
						VirtualSwitch vs1 = WS_Server.switchLs.get(tmpLs[1]).second;
						VirtualSwitch vs2 = WS_Server.switchLs.get(tmpLs[3]).second;
						if(vs1!=null&&vs2!=null) {
							L2L.add(new Layer2Layer(vs1,vs2));
						}
					}else if(tmpLs[0].equalsIgnoreCase("r")&&tmpLs[2].equalsIgnoreCase("r")){//router to router
						VirtualRouter vr1 = WS_Server.routerLs.get(tmpLs[1]).second;
						VirtualRouter vr2 = WS_Server.routerLs.get(tmpLs[3]).second;
						if(vr1!=null&&vr2!=null) {
							L2L.add(new Layer2Layer(vr1,vr2));
						}
					}else {//switch to router
						VirtualSwitch vs = null;
						VirtualRouter vr = null;
						if(tmpLs[0].equalsIgnoreCase("s")) {
							vs = WS_Server.switchLs.get(tmpLs[1]).second;
						}else {
							vr = WS_Server.routerLs.get(tmpLs[1]).second;
						}
						if(tmpLs[2].equalsIgnoreCase("s")) {
							vs = WS_Server.switchLs.get(tmpLs[3]).second;
						}else {
							vr = WS_Server.routerLs.get(tmpLs[3]).second;
						}
						if(vr!=null&&vs!=null) {
							L2L.add(new Layer2Layer(vr,vs));
						}
					}
					
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
