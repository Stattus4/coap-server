package coap.server;

public class CaliforniumLauncher {

	public static void main(String[] args) {
		new Thread(() -> {
			try {
				CaliforniumServer.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				CaliforniumDtlsServer.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
}
