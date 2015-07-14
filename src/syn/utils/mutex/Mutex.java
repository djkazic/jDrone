package syn.utils.mutex;

import syn.main.Client;
import syn.utils.Utilities;

public class Mutex {

	private Utilities uc;
	
	public Mutex() {
		uc = new Utilities();
		init();
	}

	private void init() {
		if(uc.procOS(Client.getInstance().os).equals("WIN")) {
			try {
				WinMutex wm = new WinMutex();
				Client.getInstance().mutexStr = wm.getSerialNumber();
			} catch (Exception e) { e.printStackTrace(); }
		} else if(uc.procOS(Client.getInstance().os).equals("LINUX")) {
			try {
				NixMutex nm = new NixMutex();
				Client.getInstance().mutexStr = nm.getSerialNumber();
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
}