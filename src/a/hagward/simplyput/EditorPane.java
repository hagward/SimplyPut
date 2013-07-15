package a.hagward.simplyput;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Anders Hagward
 * TODO: Is it really cool to let the LineNumberBar be a part of
 * this class?
 */
@SuppressWarnings("serial")
public class EditorPane extends JPanel {
	// TODO: Use EditorKit instead?
	public JTextArea editor;
	private LineNumberBar lineNumbers;
	
	private boolean modified = false;
	
	public EditorPane(Font font, int tabSize) {
		setLayout(new BorderLayout());
		
		editor = new JTextArea();
		editor.setEditable(true);
		editor.setFont(font);
		editor.setTabSize(tabSize);
		
		add(editor, BorderLayout.CENTER);
		
		lineNumbers = new LineNumberBar(font);
		lineNumbers.setForeground(Color.white);
		editor.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				lineNumbers.setLines(editor.getLineCount());
				modified = true;
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// User typed a character, try to autocomplete.
				if (e.getLength() == 1)
					autoComplete();
				lineNumbers.setLines(editor.getLineCount());
				modified = true;
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {}
		});
		add(lineNumbers, BorderLayout.WEST);
	}
	
	private void autoComplete() {
		// TODO: Create my own Document/DocumentFilter, read in literals to
		// auto-complete from a properties file.
//		String text = editor.getText();
//		char lastChar = text.charAt(text.length() - 1);
//		if (lastChar == '{')
//			editor.append("}");
	}
	
	/**
	 * @return the current caret position in line and column.
	 */
	public int[] getLineAndCol() {
		int[] lc = {1, 1};
		try {
			int cp = editor.getCaretPosition();
			lc[0] = editor.getLineOfOffset(cp);
			lc[1] = cp - editor.getLineStartOffset(lc[0]++) + 1;
		} catch (Exception ex) {}
		return lc;
	}
	
	public void setModified(boolean modified) {
		this.modified = modified;
	}
	
	public boolean isModified() {
		return modified;
	}
	
	/**
	 * Deletes all lines between the left text position and the right.
	 * @param left the left text position
	 * @param right the right text position
	 */
	public void deleteLines(int left, int right) {
		try {
			String text = editor.getText();
			
			// Find the beginning of the line.
			while (left > 0 && text.charAt(--left) != '\n');
			
			// Find the end of the line.
			while (right < text.length() && text.charAt(right) != '\n')
				right++;
			// Include the newline on the current line if it exists.
			if (right < text.length()) right++;
			
			// Don't include the newline from the previous line except if on last line.
			if (left > 0) {
				if (right == text.length()) {
					if (text.charAt(left - 1) == '\r')
						left--;
				} else {
					left++;
				}
			}
			
			if (left != right)
				editor.getDocument().remove(left, right - left);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
