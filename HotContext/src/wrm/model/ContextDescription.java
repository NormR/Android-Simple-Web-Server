package wrm.model;

import java.util.List;

public class ContextDescription {

	
	private String name;
	private List<CommandDescription> commands;
	public ContextDescription(String name, List<CommandDescription> commands) {
		super();
		this.name = name;
		this.commands = commands;
	}
	
	
	public ContextDescription() {
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<CommandDescription> getCommands() {
		return commands;
	}


	public void setCommands(List<CommandDescription> commands) {
		this.commands = commands;
	}
	
	
	@Override
	public String toString() {
		return name;
	}

}
