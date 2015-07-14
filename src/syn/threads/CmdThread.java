package syn.threads;

import java.io.IOException;
import java.util.ArrayList;
import syn.main.Client;
import syn.modules.Module;
import syn.utils.Settings;
import syn.utils.Utilities;

public class CmdThread implements Runnable {

	private Utilities uc;
	private String curLine = null;
	private boolean processed = false;
	public void run() {
		uc = new Utilities();
		Client.getInstance().authedUsers = new ArrayList <String> ();
		while(true) {
			try {
				Thread.sleep(100);
				if(curLine != null && !processed) {
					if(Settings.debugMode) { System.out.println(curLine); }
					try {
						initCmdProcess(curLine);
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
	
	private void initCmdProcess(String str) throws IOException {
		if(str.startsWith("PING ")) {
			// We must respond to PINGs to avoid being disconnected.
			uc.rawwrite("PONG " + str.substring(5) + "\r\n");
		}
		if(!Client.getInstance().authedUsers.contains(uc.getUser(str)) && uc.authCheck(str)) {
			uc.write(uc.getUser(str) + " AUTHED");
			Client.getInstance().authedUsers.add(uc.getUser(str));
		}
		if(Client.getInstance().authedUsers.contains(uc.getUser(str))) { 
			authedCmdProcess(str); 
		}
	}
	
	private void authedCmdProcess(String str) throws IOException {
		String[] split = str.split(":");
		if(uc.isCmd(split)) {
			String cmd = uc.getCmd(split);
			
			Module triggeredModule = Module.filterResults(cmd, str);
			if(triggeredModule != null) {
				triggeredModule.process();
			}
		}
	}
}
