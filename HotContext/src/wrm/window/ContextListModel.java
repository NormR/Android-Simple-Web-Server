package wrm.window;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import wrm.HCController;
import wrm.model.ContextDescription;

public final class ContextListModel extends AbstractListModel<ContextDescription>
{
	private static final long serialVersionUID = -3141414614330741371L;
	
	
	
	List<ContextDescription> contexts = HCController.getInstance().getRegistry().getContexts();

	@Override
	public ContextDescription getElementAt(int index) {
		return contexts.get(index);
	}

	@Override
	public int getSize() {
		return contexts.size();
	}

}