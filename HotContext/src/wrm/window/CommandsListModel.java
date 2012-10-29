package wrm.window;

import java.util.List;

import javax.swing.AbstractListModel;

import wrm.model.CommandDescription;

public class CommandsListModel extends AbstractListModel<CommandDescription>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<CommandDescription> commands;
	
	
	public CommandsListModel(List<CommandDescription> commands) {
		super();
		this.commands = commands;
	}

	@Override
	public CommandDescription getElementAt(int arg0) {
		return commands.get(arg0);
	}

	@Override
	public int getSize() {
		return commands.size();
	}

}
