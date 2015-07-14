package syn.mutex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import syn.utils.Settings;

public class NixMutex {

	public String getSerialNumber() {
		
		if(Settings.serial != null) {
			return Settings.serial;
		}
		OutputStream os = null;
		InputStream is = null;
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		
		try {
			process = runtime.exec(new String[] { "dmidecode", "-t", "system" });
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		os = process.getOutputStream();
		is = process.getInputStream();
		
		try {
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		String marker = "Serial Number:";
		try {
			while ((line = br.readLine()) != null) {
				if (line.indexOf(marker) != -1) {
					Settings.serial = line.split(marker)[1].trim();
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		if(Settings.serial == null) {
			throw new RuntimeException("Cannot find computer SN");
		}
		return Settings.serial;
	}
}