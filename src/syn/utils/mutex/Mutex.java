package syn.utils.mutex;

import syn.main.Client;
import syn.utils.Settings;
import syn.utils.Utilities;

public class Mutex {

	private Utilities uc;
	
	public Mutex() {
		uc = new Utilities();
		init();
	}

	private void init() {
		if(uc.procOS(Client.os).equals("WIN")) {
			try {
				WinMutex wm = new WinMutex();
				Settings.clientMutex = wm.getSerialNumber();
			} catch (Exception e) { e.printStackTrace(); }
		} else if(uc.procOS(Client.os).equals("LINUX")) {
			try {
				NixMutex nm = new NixMutex();
				Settings.clientMutex = nm.getSerialNumber();
			} catch (Exception e) { e.printStackTrace(); }
		}
	}

}
