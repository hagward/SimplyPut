package a.hagward.simplyput;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * @author Anders Hagward
 */
public class SimplyPut {
	public EditorPane editPane;
	
	private Locale currentLocale;
	private ResourceBundle messages;
	private ResourceBundle settings;
	private JScrollPane scrollPane;
	private StatusBar statusBar;
	
	/**
	 * Shows a popup asking if the user wants to save the document.
	 * @return 0, 1 and 2 for "Yes", "No" and "Cancel" respectively
	 */
	private int showSavePopup() {
		return JOptionPane.showConfirmDialog(null, messages.getString("savePopup"));
	}
	
	/**
	 * Loads the language and settings files.
	 * @param language the language to use
	 * @param country the country of the specified language
	 */
	private void initResources(String language, String country) {
		currentLocale = new Locale(language, country);
		messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);
		settings = ResourceBundle.getBundle("SettingsBundle");
	}
	
	/**
	 * Shows the "Do you want to save?" popup if the document is modified,
	 * otherwise it quits the program directly.
	 */
	public void exitProgram() {
		if (!editPane.isModified() || (editPane.isModified() && showSavePopup() < 2))
			System.exit(0);
	}
	
	/**
	 * Creates and returns the menu bar loaded with all the necessary menu
	 * items. It also set ups the action listeners for the items.
	 * @return a nice menu bar, complete with menu items and action listeners
	 */
	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu fileMenu, editMenu;
		JMenuItem newItem, openItem, saveItem, saveAsItem, exitItem;
		JMenuItem undoItem, redoItem, delLineItem;
		
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu(messages.getString("fileMenu"));
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
		newItem = new JMenuItem(messages.getString("fileMenuNew"), KeyEvent.VK_N);
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(showSavePopup());
			}
		});
		fileMenu.add(newItem);
		
		openItem = new JMenuItem(messages.getString("fileMenuOpen"), KeyEvent.VK_O);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(openItem);
		
		saveItem = new JMenuItem(messages.getString("fileMenuSave"), KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(saveItem);
		
		saveAsItem = new JMenuItem(messages.getString("fileMenuSaveAs"), KeyEvent.VK_A);
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
		fileMenu.add(saveAsItem);
		
		fileMenu.addSeparator();
		
		exitItem = new JMenuItem(messages.getString("fileMenuExit"), KeyEvent.VK_X);
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitProgram();
			}
		});
		fileMenu.add(exitItem);
		
		editMenu = new JMenu(messages.getString("editMenu"));
		editMenu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(editMenu);
		
		undoItem = new JMenuItem(messages.getString("editMenuUndo"), KeyEvent.VK_U);
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		editMenu.add(undoItem);
		
		redoItem = new JMenuItem(messages.getString("editMenuRedo"), KeyEvent.VK_R);
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		editMenu.add(redoItem);
		
		editMenu.addSeparator();
		
		delLineItem = new JMenuItem(messages.getString("editMenuDelLine"));
		delLineItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
		delLineItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editPane.deleteLines(editPane.editor.getSelectionStart(),
						editPane.editor.getSelectionEnd());
			}
		});
		editMenu.add(delLineItem);
		
		return menuBar;
	}
	
	/**
	 * Creates and returns the content pane and the items that resides there.
	 * @return a nice content pane with loads of content
	 */
	public Container createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		
		Font font = new Font(settings.getString("font"), Font.PLAIN, Integer.valueOf(settings.getString("fontSize")));
		editPane = new EditorPane(font);
		editPane.editor.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				int[] lc = editPane.getLineAndCol();
				statusBar.updateStatus(lc[0], lc[1]);
			}
		});
		
		scrollPane = new JScrollPane(editPane);
		scrollPane.setBorder(null);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		statusBar = new StatusBar(messages);
		contentPane.add(statusBar, BorderLayout.SOUTH);
		
		return contentPane;
	}
	
	/**
	 * Creates and shows the GUI!!!
	 */
	private static void createAndShowGUI() {
		JFrame frame = new JFrame("TextEd");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		final SimplyPut textEd = new SimplyPut();
		textEd.initResources("en", "US");
		frame.setJMenuBar(textEd.createMenuBar());
		frame.setContentPane(textEd.createContentPane());
		
		Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
		windowSize.width /= 1.5;
		windowSize.height /= 1.5;
		
		frame.setPreferredSize(windowSize);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				textEd.exitProgram();
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		frame.setVisible(true);
		
		textEd.editPane.editor.requestFocusInWindow();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}