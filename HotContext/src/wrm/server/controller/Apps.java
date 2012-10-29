package wrm.server.controller;

import java.util.List;
import java.util.Map;

import wrm.HCController;
import wrm.util.Interop;
import wrm.util.Interop.WindowInformation;



public class Apps {
	
	
	
	public static class RequestObject {
		public String commandId;
		public WindowInformation window;
	};

	
	
	public Object get(Map<String, String> params) {
		List<WindowInformation> visibleWindows = Interop.getVisibleWindows();
		return visibleWindows;
	}

	public Object post(RequestObject cmd) {
		
		switch(cmd.commandId){
		case "show":
			Interop.showWindow(cmd.window);
			break;
		default:
				HCController.getInstance().getRegistry().executeCommand(cmd.window.getExecutable(), cmd.commandId);
		}
		
		return null;
	}

	
}
