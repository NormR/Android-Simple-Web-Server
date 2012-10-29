package wrm.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import wrm.HCController;

public class ServerStartStop extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean isServerUp = false;
	public ServerStartStop() {
		setActionName();
		putValue(SHORT_DESCRIPTION, "Server Status");
	}
	public void actionPerformed(ActionEvent e) {
		if (isServerUp)
			HCController.getInstance().stopServer();
		else
			HCController.getInstance().startServer();
				
		isServerUp = !isServerUp;
		setActionName();
	}
	
	private void setActionName(){
		if (isServerUp)
			putValue(NAME, "Server is up");
		else
			putValue(NAME, "Server is down");
	}
}