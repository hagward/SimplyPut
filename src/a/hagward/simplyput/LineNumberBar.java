package a.hagward.simplyput;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JTextArea;

/**
 * @author Anders Hagward
 */
@SuppressWarnings("serial")
public class LineNumberBar extends JTextArea {
	private StringBuilder text;
	private int numLines = 1;
	
	public LineNumberBar(Font font) {
		text = new StringBuilder("1\n");
		setEditable(false);
		setOpaque(true);
		setBackground(new Color(155, 65, 99));
		setFont(font);
		setMargin(new Insets(0, 10, 0, 10));
		setText(text.toString());
	}
	
	public void setLines(int lines) {
		if (lines > numLines) {
			while (numLines < lines) {
				numLines++;
				text.append(numLines);
				text.append('\n');
			}
			setText(text.toString());
		} else if (lines < numLines) {
			while (numLines > lines) {
				text.setLength(text.length() - String.valueOf(numLines).length() - 1);
				numLines--;
			}
			setText(text.toString());
		}
		numLines = lines;
	}
}
