package wrm.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pmw.tinylog.Logger;

import wrm.CommandRegistry;
import wrm.util.Interop;
import wrm.util.Interop.WindowInformation;

public class Commands {

	
	public static class CommandDesc{
		public String command;
	}

	
	
	public Object get(Map<String, String> params) {
		
		String appId = params.get("app");
		return CommandRegistry.getInstance().getCommands(appId);
	}

	public Object post(Object cmd) {
		
	
		
		return null;
	}
}
