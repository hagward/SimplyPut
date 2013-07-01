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
	JLabel lineColLabel, charsetLabel;
	
	public StatusBar(ResourceBundle messages, String charset) {
		this.messages = messages;
		setLayout(new BorderLayout());
		
		JPanel leftPane = new JPanel();
		add(leftPane, BorderLayout.WEST);
		
		lineColLabel = new JLabel();
		leftPane.add(lineColLabel);
		
		JPanel rightPane = new JPanel();
		add(rightPane, BorderLayout.EAST);
		
		charsetLabel = new JLabel(charset);
		rightPane.add(charsetLabel);
		
		setLineCol(1, 1);
	}
	
	public void setLineCol(int line, int column) {
		lineColLabel.setText(messages.getString("statusBarLine")
				+ " " + line + ", " + messages.getString("statusBarColumn")
				+ " " + column);
	}
	
	public void setCharset(String charset) {
		charsetLabel.setText(charset);
	}
}
