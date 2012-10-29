package wrm.server.controller;

import java.util.Map;

import wrm.HCController;

public class Commands {

	
	public static class CommandDesc{
		public String command;
	}

	
	
	public Object get(Map<String, String> params) {
		
		String appId = params.get("app");
		return HCController.getInstance().getRegistry().getCommands(appId);
	}

	public Object post(Object cmd) {
		
	
		
		return null;
	}
}
