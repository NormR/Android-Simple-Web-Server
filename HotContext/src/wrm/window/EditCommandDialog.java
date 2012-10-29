package wrm.window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import wrm.model.CommandDescription;

public class EditCommandDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txt_cmd;
	private final CommandDescription command;
	private final boolean isEdit;
	private JTextField txt_id;
	private JTextArea txt_desc;
	private CommandDescription result;

	
	/**
	 * Create the dialog.
	 */
	public EditCommandDialog(CommandDescription command, boolean isEdit) {
		this.command = command;
		this.isEdit = isEdit;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{81, 212, 0};
		gbl_contentPanel.rowHeights = new int[]{34, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblNewLabel = new JLabel("Id:");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 0;
			contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		}
		{
			txt_id = new JTextField();
			GridBagConstraints gbc_txt_id = new GridBagConstraints();
			gbc_txt_id.insets = new Insets(0, 0, 5, 0);
			gbc_txt_id.fill = GridBagConstraints.BOTH;
			gbc_txt_id.gridx = 1;
			gbc_txt_id.gridy = 0;
			contentPanel.add(txt_id, gbc_txt_id);
			txt_id.setColumns(10);
		}
		{
			JLabel lblCommand = new JLabel("Command");
			GridBagConstraints gbc_lblCommand = new GridBagConstraints();
			gbc_lblCommand.insets = new Insets(0, 0, 5, 5);
			gbc_lblCommand.anchor = GridBagConstraints.WEST;
			gbc_lblCommand.gridx = 0;
			gbc_lblCommand.gridy = 1;
			contentPanel.add(lblCommand, gbc_lblCommand);
		}
		{
			txt_cmd = new JTextField();
			GridBagConstraints gbc_txt_cmd = new GridBagConstraints();
			gbc_txt_cmd.insets = new Insets(0, 0, 5, 0);
			gbc_txt_cmd.fill = GridBagConstraints.HORIZONTAL;
			gbc_txt_cmd.gridx = 1;
			gbc_txt_cmd.gridy = 1;
			contentPanel.add(txt_cmd, gbc_txt_cmd);
			txt_cmd.setColumns(10);
		}
		{
			JLabel lblDescription = new JLabel("Description");
			GridBagConstraints gbc_lblDescription = new GridBagConstraints();
			gbc_lblDescription.anchor = GridBagConstraints.NORTHWEST;
			gbc_lblDescription.insets = new Insets(0, 0, 0, 5);
			gbc_lblDescription.gridx = 0;
			gbc_lblDescription.gridy = 2;
			contentPanel.add(lblDescription, gbc_lblDescription);
		}
		{
			txt_desc = new JTextArea();
			GridBagConstraints gbc_txt_desc = new GridBagConstraints();
			gbc_txt_desc.fill = GridBagConstraints.BOTH;
			gbc_txt_desc.gridx = 1;
			gbc_txt_desc.gridy = 2;
			contentPanel.add(txt_desc, gbc_txt_desc);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						computeResult();
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		initialize();
	}


	private void initialize() {
		if (isEdit)
		{	
			setTitle("Edit Command"); 
			txt_id.setText(command.id);
			txt_cmd.setText(command.command);
			txt_desc.setText(command.description);
		}
		else
			setTitle("Add New Command");
		
	}
	
	public CommandDescription computeResult(){
		result = new CommandDescription();
		
		result.id = txt_id.getText();
		result.command = txt_cmd.getText();
		result.description = txt_desc.getText();
		
		return result;
	}

	
	public CommandDescription getResult(){
		return result;
	}

}
