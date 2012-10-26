package wrm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pmw.tinylog.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import wrm.util.RobotDsl;

public class CommandRegistry {

	
	private static CommandRegistry _instance = new CommandRegistry();
	public static CommandRegistry getInstance(){
		return _instance ;
	}
	
	

	
	public static class CommandDescription{
		@JsonView(View.Private.class)
		public String command;
		
		@JsonView(View.Public.class)
		public String description;
		@JsonView(View.Public.class)
		public String id;
		
		public CommandDescription(String command, String description, String id) {
			super();
			this.command = command;
			this.description = description;
			this.id = id;
		}
		public CommandDescription() {
		}
		
		public void execute(){
			RobotDsl.evalCommandLine(command);
		}
	}
	
	private Map<String, List<CommandDescription>> commandMap = new HashMap<>();
	
	
	public CommandRegistry() {
		
		List<CommandDescription> descs = new LinkedList<>();

		
//		PrettyPrinter pp = new DefaultPrettyPrinter();
		ObjectMapper mapper = new ObjectMapper();
		 TypeReference<HashMap<String,List<CommandDescription>>> typeRef 
         = new TypeReference< 
                HashMap<String,List<CommandDescription>> 
              >() {}; 
              
		try {
			commandMap = mapper.readValue(new File("commands.json"), typeRef);
//			mapper.writer(pp).withView(View.Private.class).writeValue(new File("commands.json"), commandMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public List<CommandDescription> getCommands(String appId){
		return commandMap.get(appId);
	}
	
	
	public void executeCommand(String appId, String commandId) {
		if (commandMap.get(appId) == null)
		{
			Logger.warn("Command not found: Id [{0}]  Application [{1}]", commandId, appId);
			return;
		}
		
		Logger.info("Executing command: Id [{0}]  Application [{1}]", commandId, appId);
		for(CommandDescription desc : commandMap.get(appId))
			if (desc.id.equals(commandId))
				desc.execute();
	}
	
}
