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

import wrm.model.CommandDescription;
import wrm.model.ContextDescription;
import wrm.server.View;
import wrm.util.RobotDsl;

public class CommandRegistry {

	
	
	
	
	private List<ContextDescription> commandMap = new LinkedList<>();
	
	
	public List<ContextDescription> getContexts() {
		return commandMap;
	}


	public CommandRegistry() {
		

		
//		PrettyPrinter pp = new DefaultPrettyPrinter();
		ObjectMapper mapper = new ObjectMapper();
		 TypeReference<List<ContextDescription>> typeRef 
         = new TypeReference<List<ContextDescription>>() {}; 
              
		try {
			commandMap = mapper.readValue(new File("commands.json"), typeRef);
//			mapper.writer(pp).withView(View.Private.class).writeValue(new File("commands.json"), commandMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public List<CommandDescription> getCommands(String appId){
		for(ContextDescription c : commandMap)
			if (c.getName().equals(appId))
				return c.getCommands();
		
		return null;
	}
	
	
	public void executeCommand(String appId, String commandId) {
		List<CommandDescription> commands = getCommands(appId);
		if (commands == null)
		{
			Logger.warn("Command not found: Id [{0}]  Application [{1}]", commandId, appId);
			return;
		}
		
		
		for(CommandDescription desc : commands)
			if (desc.id.equals(commandId)){
				Logger.info("Executing command: Id [{0}]  Application [{1}]", commandId, appId);
				desc.execute();
			}
	}
	
}
