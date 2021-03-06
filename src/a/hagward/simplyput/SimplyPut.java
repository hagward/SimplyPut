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
import java.io.File;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
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

import org.apache.commons.io.FileUtils;

/**
 * @author Anders Hagward
 */
public class SimplyPut {
	public EditorPane editPane;
	
	private JScrollPane scrollPane;
	private StatusBar statusBar;
	
	private Locale currentLocale;
	private ResourceBundle messages;
	private ResourceBundle settings;
	private Charset charset;
	private File openFile;
	
	/**
	 * Shows a meaningless 'about' dialog.
	 */
	private void showAboutDialog() {
		JOptionPane.showMessageDialog(null, "SimplyPut, by Anders Hagward");
	}
	
	/**
	 * Shows a dialog asking if the user wants to save the document.
	 * @return 0, 1 and 2 for "Yes", "No" and "Cancel" respectively
	 */
	private int showDoYouWantToSaveDialog() {
		return JOptionPane.showConfirmDialog(null, messages.getString("doYouWantToSaveDialog"));
	}
	
	/**
	 * Possibly shows a file chooser dialog (if the file has not been saved
	 * before or if forceDialog is set to true) and then writes the content of
	 * the editor to the current open file. It also sets the chosen file as the
	 * currently opened file by setting openFile.
	 * @param forceDialog always show a file chooser dialog
	 * @return true if saving was successful, false otherwise
	 */
	private boolean saveCurrentFile(boolean forceDialog) {
		if (openFile == null || forceDialog) {
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showSaveDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION)
				return false;
			openFile = chooser.getSelectedFile();
		}
		
		try {
			FileUtils.writeStringToFile(openFile, editPane.editor.getText(), charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		editPane.setModified(false);
		return true;
	}
	
	/**
	 * Shows a file chooser dialog and loads the contents of the selected file
	 * into the editor. It also marks that file as the currently opened file by
	 * setting openFile.
	 * @return true if opening a file was successful, false otherwise
	 */
	private boolean openNewFile() {
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return false;
		
		// TODO: This is quite slow when opening large files for some reason.
		try {
			editPane.editor.setText(FileUtils.readFileToString(
					chooser.getSelectedFile(), charset));
			openFile = chooser.getSelectedFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		editPane.setModified(false);
		return true;
	}
	
	/**
	 * Loads the language and settings files and initializes the class
	 * variables.
	 * @param language the language to use
	 * @param country the country of the specified language
	 */
	private void initResources(String language, String country) {
		currentLocale = new Locale(language, country);
		messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);
		settings = ResourceBundle.getBundle("SettingsBundle");
		
		openFile = null;
		charset = Charset.forName(settings.getString("defaultCharset"));
	}
	
	/**
	 * Shows the "Do you want to save?" dialog if the document is modified,
	 * otherwise it quits the program directly.
	 */
	public void exitProgram() {
		if (!editPane.isModified()) {
			System.exit(0);
		} else if (editPane.isModified()) {
			int returnVal = showDoYouWantToSaveDialog();
			switch (returnVal) {
			case 0:
				saveCurrentFile(false);
			case 1:
				System.exit(0);
				break;
			}
		}
	}
	
	/**
	 * Creates and returns the menu bar loaded with all the necessary menu
	 * items. It also set ups the action listeners for the items.
	 * @return a nice menu bar, complete with menu items and action listeners
	 */
	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu fileMenu, editMenu, helpMenu;
		JMenuItem newItem, openItem, saveItem, saveAsItem, exitItem,
				undoItem, redoItem, copyItem, cutItem, pasteItem,
				delLineItem, aboutItem;
		
		menuBar = new JMenuBar();
		
		// Here follows the File menu.
		fileMenu = new JMenu(messages.getString("fileMenu"));
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
		newItem = new JMenuItem(messages.getString("fileMenuNew"), KeyEvent.VK_N);
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("New File!");
			}
		});
		fileMenu.add(newItem);
		
		openItem = new JMenuItem(messages.getString("fileMenuOpen"), KeyEvent.VK_O);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openNewFile();
			}
		});
		fileMenu.add(openItem);
		
		saveItem = new JMenuItem(messages.getString("fileMenuSave"), KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCurrentFile(false);
			}
		});
		fileMenu.add(saveItem);
		
		saveAsItem = new JMenuItem(messages.getString("fileMenuSaveAs"), KeyEvent.VK_A);
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCurrentFile(true);
			}
		});
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
		
		// Here follows the Edit menu.
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
		
		copyItem = new JMenuItem(messages.getString("editMenuCopy"));
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		editMenu.add(copyItem);
		
		cutItem = new JMenuItem(messages.getString("editMenuCut"));
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		editMenu.add(cutItem);
		
		pasteItem = new JMenuItem(messages.getString("editMenuPaste"));
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		editMenu.add(pasteItem);
		
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
		
		helpMenu = new JMenu(messages.getString("helpMenu"));
		helpMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMenu);
		
		aboutItem = new JMenuItem(messages.getString("helpMenuAbout"), KeyEvent.VK_A);
		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showAboutDialog();
			}
		});
		helpMenu.add(aboutItem);
		
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
		int tabSize = Integer.valueOf(settings.getString("tabSize"));
		editPane = new EditorPane(font, tabSize);
		editPane.editor.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
//				int[] lc = editPane.getLineAndCol();
//				statusBar.setLineCol(lc[0], lc[1]);
			}
		});
		
		scrollPane = new JScrollPane(editPane);
		scrollPane.setBorder(null);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		statusBar = new StatusBar(messages, charset.name());
		contentPane.add(statusBar, BorderLayout.SOUTH);
		
		return contentPane;
	}
	
	/**
	 * Creates and shows the amazing GUI!!!
	 */
	private static void createAndShowGUI() {
		JFrame frame = new JFrame("SimplyPut");
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
