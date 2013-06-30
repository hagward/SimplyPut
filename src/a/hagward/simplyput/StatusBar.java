package a.hagward.simplyput;

import java.util.ResourceBundle;

import javax.swing.JTextField;

/**
 * @author Anders
 */
@SuppressWarnings("serial")
public class StatusBar extends JTextField {
	ResourceBundle messages;
	
	public StatusBar(ResourceBundle messages) {
		this.messages = messages;
		
		setEditable(false);
		updateStatus(1, 1);
	}
	
	public void updateStatus(int line, int column) {
		setText(messages.getString("statusBarLine")
				+ " " + line + ", " + messages.getString("statusBarColumn")
				+ " " + column);
	}
}
