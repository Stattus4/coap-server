package coapproxy.server;

public class ServerLauncher {

	public static void main(String[] args) {
		new Thread(() -> {
			try {
				CfServer.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				CfSecureServer.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
}
