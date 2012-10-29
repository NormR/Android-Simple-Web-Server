package wrm;

import java.io.IOException;

import org.pmw.tinylog.Logger;

import wrm.server.ApplicationServer;
import wrm.window.Application;

public class HCController {

	ApplicationServer server = new ApplicationServer();
	CommandRegistry registry = new CommandRegistry();
	
	private static HCController _instance = new HCController();

	

	private Thread serverThread;
	static {
		_instance.initialize();
	}
	
	
	public static HCController getInstance(){
		return _instance;
	}
	
	


	public static void main(String[] args) throws IOException {
		Logger.setLoggingFormat("{level} {date:HH:mm:ss} [{thread}] {class}.{method}():\t{message}");
	
		Application app = new Application(); 
		app.show();
	}



	private void initialize() {
		
	}
	
	
	public void startServer(){
		Logger.info("Starting Embedded Server");
		serverThread = new Thread(
			new Runnable() {
				
				@Override
				public void run() {
					try {
						server.start(8080);
					} catch (IOException e) {
						Logger.error(e);
					}
				}
			});
		serverThread.setDaemon(true);
		serverThread.start();
	}
	
	public void stopServer(){
		server.stopRunning();
//		serverThread.interrupt();
	}
	
	
	public CommandRegistry getRegistry() {
		return registry;
	}
	
}
