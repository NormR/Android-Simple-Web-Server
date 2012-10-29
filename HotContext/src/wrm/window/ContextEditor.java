package wrm.window;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextPane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JToolBar;
import javax.swing.JButton;

import wrm.model.CommandDescription;
import wrm.model.ContextDescription;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ContextEditor extends JPanel {
	private JList<ContextDescription> contexts;
	private JList<CommandDescription> commands;

	/**
	 * Create the panel.
	 */
	public ContextEditor() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		panel.add(splitPane);
		splitPane.setResizeWeight(0.2);
		
		JPanel panel_1 = new JPanel();
		splitPane.setLeftComponent(panel_1);
				panel_1.setLayout(new BorderLayout(0, 0));
		
				contexts = new JList<ContextDescription>();
				panel_1.add(contexts);
				contexts.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent sel) {
						
						ContextDescription selectedValue = contexts.getSelectedValue();
						System.out.println(selectedValue);
						commands.setModel(new CommandsListModel(selectedValue.getCommands()));
						
					}
				});
				contexts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
						JToolBar toolBar = new JToolBar();
						toolBar.setFloatable(false);
						panel_1.add(toolBar, BorderLayout.NORTH);
						
								JButton b_addContext = new JButton("+");
								toolBar.add(b_addContext);
								
										JButton b_delContext = new JButton("-");
										toolBar.add(b_delContext);
		
		JPanel panel_2 = new JPanel();
		splitPane.setRightComponent(panel_2);
				panel_2.setLayout(new BorderLayout(0, 0));
		
				commands = new JList<CommandDescription>();
				panel_2.add(commands);
				
				JToolBar toolBar_1 = new JToolBar();
				toolBar_1.setFloatable(false);
				panel_2.add(toolBar_1, BorderLayout.NORTH);
				
				JButton b_addCommand = new JButton("+");
				b_addCommand.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						if (contexts.getSelectedValue() != null)
							addCommand(contexts.getSelectedValue());
					}
				});
				toolBar_1.add(b_addCommand);
				
				JButton b_delCommand = new JButton("-");
				b_delCommand.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (contexts.getSelectedValue() != null)
							if (commands.getSelectedValue() != null)
								delCommand(contexts.getSelectedValue(), commands.getSelectedValue());
					}
				});
				toolBar_1.add(b_delCommand);

				
				
		b_addCommand.setBorderPainted(false);
		b_addContext.setBorderPainted(false);
		b_delCommand.setBorderPainted(false);
		b_delContext.setBorderPainted(false);
		
		initialize();
	}

	

	private void initialize() {
		
		
		contexts.setModel(new ContextListModel());
		commands.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CommandDescription value = commands.getSelectedValue();
				if (e.getClickCount() == 2 && value != null){
					editCommand(value);
				}
			}
		});
		
		
	}
	
	
	protected void addCommand(ContextDescription ctx) {
		
		
		EditCommandDialog dialog = new EditCommandDialog(new CommandDescription(), false);
		dialog.setModal(true);
		dialog.setVisible(true);
		
		//TODO: validation of entries;
		CommandDescription result = dialog.getResult();
		if (result != null){
			ctx.getCommands().add(result);
			commands.updateUI();
		}
	}
	
	protected void delCommand(ContextDescription ctx, CommandDescription desc) {
		
		ctx.getCommands().remove(desc);
		commands.updateUI();
	}
	

	private void editCommand(CommandDescription value) {
		EditCommandDialog dialog = new EditCommandDialog(value, true);
		dialog.setModal(true);
		dialog.setVisible(true);
		
		//TODO: validation of entries;
		CommandDescription result = dialog.getResult();
		if (result != null){
			value.id = result.id;
			value.command = result.command;
			value.description = result.description;
		}
	}
	
}
