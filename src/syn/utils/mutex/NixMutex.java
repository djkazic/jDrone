package syn.utils.mutex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import syn.main.Client;

public class NixMutex {

	public String getSerialNumber() {
		
		if(Client.getInstance().serial != null) {
			return Client.getInstance().serial;
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
					Client.getInstance().serial = line.split(marker)[1].trim();
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
		
		if(Client.getInstance().serial == null) {
			throw new RuntimeException("Cannot find computer SN");
		}
		return Client.getInstance().serial;
	}
}