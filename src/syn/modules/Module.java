package syn.modules;

import java.util.ArrayList;

public abstract class Module {
	
	private String trigger;
	protected String cmd;
	protected String preCmd;
	public static ArrayList<Module> modulesList;
	
	public Module(String trigger) {
		this.trigger = trigger;
		modulesList.add(this);
	}
	
	public boolean filter(String cmd) {
		if(trigger.contains(" ")) {
			String[] split = trigger.split(" ");
			for(int i=0; i < split.length; i++) {
				if(split[i].equals(cmd)) {
					return true;
				}
			}
			return false;
		} else {
			return cmd.equals(trigger);
		}
	}
	
	public abstract void process();
	
	public static void init() {
		modulesList = new ArrayList<Module> ();
		
		new DLModule();
		new NetModule();
		new FunctionModule();
	}
	
	public static Module filterResults(String cmd, String preCmd) {
		Module currentModule = null;
		for(int i=0; i < modulesList.size(); i++) {
			currentModule = modulesList.get(i);
			if(currentModule.filter(cmd)) {
				currentModule.setCmd(cmd);
				currentModule.setPreCmd(preCmd);
				return currentModule;
			}
		}
		return null;
	}
	
	private void setCmd(String cmd) {
		this.cmd = cmd;
	}
	
	private void setPreCmd(String preCmd) {
		this.preCmd = preCmd;
	}
}