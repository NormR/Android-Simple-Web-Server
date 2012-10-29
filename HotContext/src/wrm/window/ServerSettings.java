package wrm.window;

import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.Action;

import wrm.actions.ServerStartStop;


public class ServerSettings extends JPanel {
	private JTextField textField;
	private final Action action = new ServerStartStop();

	/**
	 * Create the panel.
	 */
	public ServerSettings() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{87, 225, 0};
		gridBagLayout.rowHeights = new int[]{33, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblServerStatus = new JLabel("Server Status");
		GridBagConstraints gbc_lblServerStatus = new GridBagConstraints();
		gbc_lblServerStatus.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblServerStatus.insets = new Insets(0, 0, 5, 5);
		gbc_lblServerStatus.gridx = 0;
		gbc_lblServerStatus.gridy = 0;
		add(lblServerStatus, gbc_lblServerStatus);
		
		JToggleButton tbtn_serverState = new JToggleButton("Server is down");
		tbtn_serverState.setAction(action);
		GridBagConstraints gbc_tbtn_serverState = new GridBagConstraints();
		gbc_tbtn_serverState.insets = new Insets(0, 0, 5, 0);
		gbc_tbtn_serverState.anchor = GridBagConstraints.NORTH;
		gbc_tbtn_serverState.fill = GridBagConstraints.HORIZONTAL;
		gbc_tbtn_serverState.gridx = 1;
		gbc_tbtn_serverState.gridy = 0;
		add(tbtn_serverState, gbc_tbtn_serverState);
		
		JLabel lblNewLabel = new JLabel("Server Port");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		add(lblNewLabel, gbc_lblNewLabel);
		
		textField = new JTextField();
		textField.setText("8080");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		add(textField, gbc_textField);
		textField.setColumns(10);

	}
}
