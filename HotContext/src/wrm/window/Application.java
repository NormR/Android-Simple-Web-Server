package wrm.window;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import wrm.HCController;

public class Application {

	private JFrame frmHcServer;
//
//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Application window = new Application();
//					window.frmHcServer.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	public Application() {
		initialize();
	}
	
	public void show(){
		frmHcServer.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmHcServer = new JFrame();
		frmHcServer.setTitle("HC Server");
		frmHcServer.setBounds(100, 100, 450, 300);
		frmHcServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmHcServer.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		ContextEditor contextEditor = new ContextEditor();
		tabbedPane.addTab("Contexts", null, contextEditor, null);
		
		ServerSettings serverSettings = new ServerSettings();
		tabbedPane.addTab("Server", null, serverSettings, null);
	}

}
