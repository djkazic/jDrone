package syn.main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import syn.net.NetworkThread;
import syn.utils.Settings;

public class Utilities {

	public String procOS(String str) {
		if(str.contains("Windows")) {
			return "WIN";
		} else {
			return "NIX";
		}
	}
	
	public int genCode() {
		return (int) (Math.random() * 90000 + 10000);
	}
	
	public boolean isCmd(String[] split) {
		if(split[1].contains("PRIVMSG ") && split[2].startsWith(Settings.prefix)) {
			return true;
		} else {
			return false;
		}
	}
	
	private String getCmdBlock(String[] split) {
		String theBlock = "";
		for(int i=2; i < split.length; i++) {
			if(i >= 3) {
				theBlock += ":" + split[i];
			} else {
				theBlock += split[i];
			}
		}
		return theBlock.substring(1, theBlock.length());
	}
	
	public String getCmd(String[] split) {
		if(getCmdBlock(split).contains(" ")) {
			String[] blockSplit = getCmdBlock(split).split(" ");
			return blockSplit[0];
		} else {
			return getCmdBlock(split);
		}
	}
	
	public String getCmdTxt(String[] split) {
		if(getCmdBlock(split).contains(" ")) {
			String[] blockSplit = getCmdBlock(split).split(" ");
			String output = "";
			for(int i=1; i < blockSplit.length; i++) {
				if(blockSplit.length != 2 && i > 1) {
					output += " " + blockSplit[i];
				} else {
					output += blockSplit[i];
				}
			}
			return output;
		} else {
			return "";
		}
	}
	
	public String getUser(String str) {
		if(str.contains("!")) {
			String[] split = str.split("!");
			return split[0].substring(1, split[0].length());
		} else {
			return "";
		}
	}
	
	public NetworkThread getNetworkThread() {
		return Client.getNetworkThread();
	}
	
	public String getChannel() {
		return getNetworkThread().getChannel();
	}
	
	public void write(String str) {
		try {
			getNetworkThread().getWriter().write("PRIVMSG "+ getChannel() + " :" + str + "\r\n");
			getNetworkThread().getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void rawwrite(String str) {
		try {
			getNetworkThread().getWriter().write(str);
			getNetworkThread().getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean authCheck(String str) {
		String[] split = str.split(":");
		if(isCmd(split) && (getCmd(split).equals("auth"))) {
			if(getCmdTxt(split).equals(Settings.authPhrase)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean testURL() throws Exception {
	    String strUrl = "http://google.com";
	    URL url = new URL(strUrl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	    try {
	        urlConn.connect();
	        System.out.println("[NET OK]");
	        return true;
	    } catch (Exception e) {
	    	if(e instanceof UnknownHostException) {
	    		System.out.println("[NET CONN DOWN]");
	    	} else {
	    		if(Settings.debugMode) { e.printStackTrace(); }
	    	}
	        return false;
	    }
	}
}
