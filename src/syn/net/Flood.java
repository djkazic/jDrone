package syn.net;

public abstract class Flood implements Runnable {
	
	public static final int threadsToUse = Runtime.getRuntime().availableProcessors() * 2;
	private static boolean flooding;

	private String target;
	private int port;
	protected boolean resultsSent = false;
	
	public static boolean startFlood(Flood flood) {
		if(!flooding) {
			flooding = true;
			for(int i=0; i < threadsToUse; i++) {
				(new Thread(flood)).start();
			}
			return true;
		}
		return false;
	}
	
	public static void stopFlood() {
		flooding = false;
	}
	
	public static boolean isFlooding() {
		return flooding;
	}
	
	public Flood(String target) {
		this.target = target;
	}
	
	public Flood(String target, int port) {
		this.target = target;
		this.port = port;
	}
	
	public String getTarget() {
		return target;
	}
	
	public int getTargetPort() {
		return port;
	}
}