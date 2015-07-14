package syn.main;

import syn.net.NetworkThread;
import syn.utils.Settings;

public class Core {
	
	@SuppressWarnings("unused")
	private static Client client;

	public static void main(String[] args) {

		System.setProperty("java.net.preferIPv4Stack", "true");

		try {
			while(true) {
				if(NetworkThread.testURL()) {
					client = new Client(Settings.server, Settings.channel, 7000);
					System.out.println("[NET OK]");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}