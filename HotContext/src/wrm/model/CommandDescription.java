package wrm.model;

import wrm.server.View;
import wrm.util.RobotDsl;

import com.fasterxml.jackson.annotation.JsonView;

public class CommandDescription{
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
		
		
		@Override
		public String toString() {
			return command + " (" + description + ")";
		}
	}