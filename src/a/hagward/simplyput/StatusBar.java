package a.hagward.simplyput;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Anders
 */
@SuppressWarnings("serial")
public class StatusBar extends JPanel {
	ResourceBundle messages;
	JLabel lineColLabel, encodingLabel;
	
	public StatusBar(ResourceBundle messages) {
		this.messages = messages;
		setLayout(new BorderLayout());
		
		JPanel leftPane = new JPanel();
		add(leftPane, BorderLayout.WEST);
		
		lineColLabel = new JLabel();
		leftPane.add(lineColLabel);
		
		JPanel rightPane = new JPanel();
		add(rightPane, BorderLayout.EAST);
		
		encodingLabel = new JLabel("ANSI");
		rightPane.add(encodingLabel);
		
		setLineCol(1, 1);
	}
	
	public void setLineCol(int line, int column) {
		lineColLabel.setText(messages.getString("statusBarLine")
				+ " " + line + ", " + messages.getString("statusBarColumn")
				+ " " + column);
	}
	
	public void setEncoding(String encoding) {
		encodingLabel.setText(encoding);
	}
}
