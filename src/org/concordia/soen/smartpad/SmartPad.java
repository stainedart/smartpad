package org.concordia.soen.smartpad;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/*
 * A text editor program with basic edit and format functions.
 */
public class SmartPad {

	
	 JFrame frame__;
	 JTextPane editor__;
	 UndoManager undoMgr__;
	 File file__;
     JMenuBar menuBar;


	// This flag checks true if the caret position within a bulleted para
	// is at the first text position after the bullet (bullet char + space).
	// Also see EditorCaretListener and BulletParaKeyListener.
	private boolean startPosPlusBullet__;

	// This flag checks true if the caret position within a numbered para
	// is at the first text position after the number (number + dot + space).
	// Alse see EditorCaretListener and NumbersParaKeyListener.		
	private boolean startPosPlusNum__;
	
	private static final String MAIN_TITLE = "SmartPad - ";
	private static final String DEFAULT_FONT_FAMILY = "SansSerif";
	private static final int DEFAULT_FONT_SIZE = 18;
	//private static final List<String> FONT_LIST = Arrays.asList(new String [] {"Arial", "Calibri", "Cambria", "Courier New", "Comic Sans MS", "Dialog", "Georgia", "Helevetica", "Lucida Sans", "Monospaced", "Tahoma", "Times New Roman", "Verdana"});
	//private static final String [] FONT_SIZES  = {"Font Size", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30"};
	//private static final String [] TEXT_ALIGNMENTS = {"Text Align", "Left", "Center", "Right", "Justified"};
	private static final char BULLET_CHAR = '\u2022';
	private static final String BULLET_STR = new String(new char [] {BULLET_CHAR});
	private static final String BULLET_STR_WITH_SPACE = BULLET_STR + " ";
	private static final int BULLET_LENGTH = BULLET_STR_WITH_SPACE.length();
	private static final String NUMBERS_ATTR = "NUMBERS";
	private static final String ELEM = AbstractDocument.ElementNameAttribute;
	private static final String COMP = StyleConstants.ComponentElementName;
    public static Map<String,StyledDocument> documents= new HashMap();
	public JPanel leftPanel ;
	public JPanel filesPanel;

	public static void main(String [] args)
			throws Exception {

		UIManager.put("TextPane.font",
				new Font(DEFAULT_FONT_FAMILY, Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.setLookAndFeel(new NimbusLookAndFeel());
		
		SwingUtilities.invokeLater(new Runnable() {
		
			@Override
			public void run() {
			
				new SmartPad().createAndShowGUI();
			}
		});
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path,
	                                           String description) {
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String USER_TYPE = "Beginner";

	//Average user buttons
    private JButton hiddenCharacterButton;
    private JButton searchButton;

	//TODO this is the main point of entry in the application
	private void createAndShowGUI() {
	    setLookAndFeel();
		frame__ = new JFrame();
		setFrameTitleWithExtn("New file");
		editor__ = new JTextPane();
		JScrollPane editorScrollPane = new JScrollPane(editor__);
		StyledDocument styledDocument =getNewDocument();
		editor__.setDocument(styledDocument);
		documents.put("newFile",styledDocument);
		editor__.addKeyListener(new BulletParaKeyListener());
		editor__.addKeyListener(new NumbersParaKeyListener());
		editor__.addCaretListener(new EditorCaretListener());
		undoMgr__ = new UndoManager();

		//Icon bar with the images. Just put the images in the resources folder icons should be 24 x 24 to be the same size as the other ones.
		ImageIcon copyIcon = createImageIcon("/resources/copy.png", "Copy");
		JButton copyButton = new JButton(copyIcon);
		ImageIcon pasteIcon = createImageIcon("/resources/paste.png", "Paste");
		JButton pasteButton = new JButton(pasteIcon);
		ImageIcon printIcon = createImageIcon("/resources/print.png", "Print");
		JButton printButton = new JButton(printIcon);
        ImageIcon hiddenCharacterIcon = createImageIcon("/resources/show_hidden.png", "Show hidden characters");
        hiddenCharacterButton = new JButton(hiddenCharacterIcon);
        hiddenCharacterButton.setVisible(false);
        String[] userTypeStrings = {"Beginner", "Average", "Expert"};
		JComboBox userType = new JComboBox(userTypeStrings);
		userType.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        JComboBox combo = (JComboBox)e.getSource();
                        String currentUsertype = (String)combo.getSelectedItem();
                        System.out.println("Currently selecting user type:" + currentUsertype);
                        if( currentUsertype.equals("Average")){
                            USER_TYPE = "Average";
                            loadAverageUI();
                            unloadExpertUI();
                        } else if (currentUsertype.equals("Expert")){
                            USER_TYPE = "Expert";
                            loadAverageUI();
                            loadExpertUI();
                        } else {// beginner user case
                            System.out.println("Loading Beginner UI");
                            USER_TYPE = "Beginner";
                            unloadExpertUI();
                            unloadAverageUI();
                        }
                    }
                }
        );
        JTextField searchField = new JTextField();
        searchField.setText("Search              ");
        searchField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                searchField.setText("");
            }

            public void focusLost(FocusEvent e) {
                if(searchField.getText().isEmpty()) {
                    searchField.setText("Search              ");
                }
            }
        });
        ImageIcon searchIcon = createImageIcon("/resources/find.png", "Search");
        searchButton = new JButton(searchIcon);
        searchButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Create the search popup
                JFrame searchDialog = new JFrame();
                searchDialog.setLocationRelativeTo(frame__);
                searchDialog.setVisible(true);
                searchDialog.setSize(300,200);
                searchDialog.setTitle("Search");

                JPanel averageUserPanel = new JPanel();
                averageUserPanel.setLayout(new BoxLayout(averageUserPanel, BoxLayout.PAGE_AXIS));
                JPanel searchPanel = new JPanel();
                JTextField searchField = new JTextField();
                searchField.setText("Search              ");
                searchField.addFocusListener(new FocusListener() {
                    public void focusGained(FocusEvent e) {
                        searchField.setText("");
                    }

                    public void focusLost(FocusEvent e) {
                        if(searchField.getText().isEmpty()) {
                            searchField.setText("Search              ");
                        }
                    }
                });
                ImageIcon leftIcon = createImageIcon("/resources/left.png", "Previous");
                JButton leftButton = new JButton(leftIcon);
                ImageIcon nextIcon = createImageIcon("/resources/right.png", "Next");
                JButton nextButton = new JButton(nextIcon);

                JPanel replacePanel = new JPanel();
                JTextField replaceField = new JTextField();
                replaceField.setText("Replace              ");
                replaceField.addFocusListener(new FocusListener() {
                    public void focusGained(FocusEvent e) {
                        replaceField.setText("");
                    }

                    public void focusLost(FocusEvent e) {
                        if(replaceField.getText().isEmpty()) {
                            replaceField.setText("Replace              ");
                        }
                    }
                });
                JButton replaceButton = new JButton("Replace");
                JButton allButton = new JButton("Replace All");

                searchPanel.add(searchField);
                searchPanel.add(leftButton);
                searchPanel.add(nextButton);

                replacePanel.add(replaceField);
                replacePanel.add(replaceButton);


                averageUserPanel.add(searchPanel);
                averageUserPanel.add(replacePanel);
                averageUserPanel.add(allButton);

                JPanel expertUserPanel = new JPanel();
                expertUserPanel.setLayout(new BoxLayout(expertUserPanel, BoxLayout.PAGE_AXIS));
                if(USER_TYPE.equals("Expert")){
                    JTextField filesToIncludeField = new JTextField();
                    filesToIncludeField.setText("File to include");
                    filesToIncludeField.addFocusListener(new FocusListener() {
                        public void focusGained(FocusEvent e) {
                            filesToIncludeField.setText("");
                        }

                        public void focusLost(FocusEvent e) {
                            if(filesToIncludeField.getText().isEmpty()) {
                                filesToIncludeField.setText("File to include");
                            }
                        }
                    });
                    JTextField filesToExcludeField = new JTextField();
                    filesToExcludeField.setText("File to exclude");
                    filesToExcludeField.addFocusListener(new FocusListener() {
                        public void focusGained(FocusEvent e) {
                            filesToExcludeField.setText("");
                        }

                        public void focusLost(FocusEvent e) {
                            if(filesToExcludeField.getText().isEmpty()){
                                filesToExcludeField.setText("File to Exclude");
                            }
                        }
                    });
                    averageUserPanel.add(filesToIncludeField);
                    averageUserPanel.add(filesToExcludeField);
                }
                searchDialog.add(averageUserPanel);
            }
        });
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel1.add(copyButton);
		panel1.add(pasteButton);
		panel1.add(printButton);
		panel1.add(hiddenCharacterButton);
		panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(userType);
        panel1.add(searchField);
        panel1.add(searchButton);
        // to be activated by the average user type
        searchButton.setVisible(false);

		JPanel toolBarPanel = new JPanel();
		toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.PAGE_AXIS));
		toolBarPanel.add(panel1);


		//This is the left side pane with where the file selector and new file button is located.
		JTextField newFile1 = new JTextField();
		newFile1.setText("newFile");
		newFile1.addMouseListener(new FileClickedMouseListener("newFile" , this));
		ImageIcon newIcon = createImageIcon("/resources/plus.png", "New");
        JButton newButton = new JButton(newIcon);
         filesPanel = new JPanel();
		filesPanel.add(newFile1);
        JPanel newButtonPanel = new JPanel();
        newButtonPanel.add(newButton);
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        leftPanel.add(filesPanel);
        leftPanel.add(newButtonPanel);


        frame__.add(toolBarPanel, BorderLayout.NORTH);
		frame__.add(editorScrollPane, BorderLayout.CENTER);
		frame__.add(leftPanel, BorderLayout.WEST);


		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		JMenuItem newItem	= new JMenuItem("New");
		newItem.setMnemonic(KeyEvent.VK_N);
		newItem.addActionListener(new NewFileListener());
		JMenuItem openItem	= new JMenuItem("Open...");
		openItem.setMnemonic(KeyEvent.VK_O);
		openItem.addActionListener(new OpenFileListener());
		JMenuItem saveItem	= new JMenuItem("Save (...)");
		saveItem.setMnemonic(KeyEvent.VK_S);
		saveItem.addActionListener(new SaveFileListener());
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.exit(0);
			}
		});

		fileMenu.add(newItem);
		fileMenu.addSeparator();
		fileMenu.add(openItem);
		fileMenu.add(saveItem);

		fileMenu.addSeparator();
		fileMenu.add(exitItem);
        menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		frame__.setJMenuBar(menuBar);
		
		frame__.setSize(900, 500);
		frame__.setLocation(150, 80);
		frame__.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame__.setVisible(true);

		//Create and hide average and expert user UIs
        createAverageUserUI();
        createExpertUserUI();

		editor__.requestFocusInWindow();

	}

    private JMenu encodingMenu;
	private JMenu macroMenu;
	private void createAverageUserUI() {
        //Average user UI element creation
        encodingMenu = new JMenu("Encoding");
        encodingMenu.setMnemonic(KeyEvent.VK_E);
        JMenuItem encodeInAnsi	= new JMenuItem("Encode in ANSI");
        JMenuItem encodeInUTF8	= new JMenuItem("Encode in UTF-8");
        JMenuItem encodeInUCS2	= new JMenuItem("Encode in UCS-2");
        JMenuItem convertToAnsi	= new JMenuItem("Convert to ANSI");
        JMenuItem convertToUTF8	= new JMenuItem("Convert to UTF-8");
        JMenuItem convertToUCS2	= new JMenuItem("Convert to UCS-2");

        encodingMenu.add(encodeInAnsi);
        encodingMenu.add(encodeInUCS2);
        encodingMenu.add(encodeInUTF8);
        encodingMenu.add(convertToUCS2);
        encodingMenu.add(convertToAnsi);
        encodingMenu.add(convertToUTF8);

        macroMenu = new JMenu("Macro");
        macroMenu.setMnemonic(KeyEvent.VK_M);
        JMenuItem recordMacro = new JMenuItem("Record Macro");
        JMenuItem repeatMacro = new JMenuItem("Repeat Macro");
        JMenuItem stopRecording = new JMenuItem("Stop Recording");
        JMenuItem runOtherMacro = new JMenuItem("Run Other Macro");

        macroMenu.add(recordMacro);
        macroMenu.add(repeatMacro);
        macroMenu.add(stopRecording);
        macroMenu.add(runOtherMacro);

        menuBar.add(encodingMenu);
        menuBar.add(macroMenu);
        encodingMenu.setVisible(false);
        macroMenu.setVisible(false);
    }

    //TODO fill this method
    private void loadAverageUI() {
        System.out.println("Loading Average UI");
        encodingMenu.setVisible(true);
        macroMenu.setVisible(true);
        hiddenCharacterButton.setVisible(true);
        searchButton.setVisible(true);
    }

	//TODO fill this method
    private void unloadAverageUI() {
	    System.out.println("Unloading Average UI");
        encodingMenu.setVisible(false);
        macroMenu.setVisible(false);
        hiddenCharacterButton.setVisible(false);
        searchButton.setVisible(false);
    }

    private JMenu viewMenu;
    private void createExpertUserUI(){
        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_M);

        JMenuItem selectAll = new JMenuItem("Select All");
        JMenuItem foldAll = new JMenuItem("Fold All");
        JMenuItem unFoldAll = new JMenuItem("Unfold All");
        JMenuItem synchronizeVerticalScroll = new JMenuItem("Synchronize Vertical Scroll");
        JMenuItem wordWrap = new JMenuItem("Word Wrap");

        viewMenu.add(selectAll);
        viewMenu.add(foldAll);
        viewMenu.add(unFoldAll);
        viewMenu.add(synchronizeVerticalScroll);
        viewMenu.add(wordWrap);

        menuBar.add(viewMenu);
        viewMenu.setVisible(false);
    }

    //TODO fill this method
    private void unloadExpertUI() {
        System.out.println("Unloading Expert UI");
        viewMenu.setVisible(false);
    }

    //TODO fill this method
    private void loadExpertUI() {
        System.out.println("Loading Expert UI");
        viewMenu.setVisible(true);
    }

    private void setFrameTitleWithExtn(String titleExtn) {

		frame__.setTitle(MAIN_TITLE + titleExtn);
	}
	
	private StyledDocument getNewDocument() {
	
		StyledDocument doc = new DefaultStyledDocument();
		doc.addUndoableEditListener(new UndoEditListener());
		return doc;
	}
	
	private class UndoEditListener implements UndoableEditListener {

		@Override
		public void undoableEditHappened(UndoableEditEvent e) {

			undoMgr__.addEdit(e.getEdit()); // remember the edit
		}
	}
	
	private class PictureFocusListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {

			JButton button = (JButton) e.getComponent();
			button.setBorder(new LineBorder(Color.GRAY));
			button.getName();
		}
		
		@Override
		public void focusLost(FocusEvent e) {

			((JButton) e.getComponent()).setBorder(new LineBorder(Color.WHITE));
		}
	}

	private StyledDocument getEditorDocument() {
	
		StyledDocument doc = (DefaultStyledDocument) editor__.getDocument();
		return doc;
	}

	private boolean isBulletedPara(int paraEleStart) {

		return getParaFirstCharacter(paraEleStart) == BULLET_CHAR;

	}
	
	private char getParaFirstCharacter(int paraEleStart) {
		
		String firstChar = "";
			
		try {
			firstChar = editor__.getText(paraEleStart, 1);
		}
		catch (BadLocationException ex) {
			
			throw new RuntimeException(ex);
		}
			
		return firstChar.charAt(0);
	}
	
	private boolean isNumberedPara(int paraEleStart) {

		AttributeSet attrSet = getParaStartAttributes(paraEleStart);		
		Integer paraNum = (Integer) attrSet.getAttribute(NUMBERS_ATTR);

		return (paraNum != null) && (isFirstCharNumber(paraEleStart));
	}
		
	private boolean isFirstCharNumber(int paraEleStart) {

		return Character.isDigit(getParaFirstCharacter(paraEleStart));

	}

	/*
	 * The insert bullet routine; inserts the bullet in the editor document. This
	 * routine is used from the insert action (ActionListener) as well as bullet
	 * para key press actions (keyPressed or keyReleased methods of KeyListener).
	 *
	 * The parameter insertPos is the position at which the bullet is to be
	 * inserted. The parameter attributesPos is the position from which the bullet
	 * is to get its attributes (like color, font, size, etc.). The two parameter
	 * values are derived differently for bullet insert and bullet para Enter
	 * key press actions. 
	 *
	 * Bullet insert action: the insertPos and attributesPos is the same,
	 * the paraEleStart.
	 * Enter key press: the insertPos is the current caret position of keyReleased(),
	 * and the attributesPos is the previous paraEleStart position from
	 * keyPressed() method.
	 */
	private void insertBullet(int insertPos, int attributesPos) {
								
		try {
			getEditorDocument().insertString(insertPos,
												BULLET_STR_WITH_SPACE,
												getParaStartAttributes(attributesPos));
		}
		catch(BadLocationException ex) {
				
			throw new RuntimeException(ex);
		}
	}

	private AttributeSet getParaStartAttributes(int pos) {
	
		StyledDocument doc = (DefaultStyledDocument) editor__.getDocument();
		Element	charEle = doc.getCharacterElement(pos);
		return charEle.getAttributes();
	}
	
	/*
	 * The remove bullet routine; removes the bullet in the editor document. This
	 * routine is used from the delete action (ActionListener) as well as bullet
	 * para key press actions (keyPressed or keyRemoved methods of KeyListener).
	 * The keys include the Enter, Backspace, Delete keys.
	 *
	 * The parameter removePos is the start position and the length is the length
	 * of text to be removed. Length of characters removed is: BULLET_LENGTH
	 * or +1 (includes carriage return folowing the BULLET_LENGTH). The two
	 * parameter values are derived differently for bullet remove and bullet
	 * para key press actions. 
	 *
	 * Bullet remove action: removePos is paraEleStart and the BULLET_LENGTH.
	 * Delete key press: removePos is current caret pos of keyPressed() and
	 * the BULLET_LENGTH.
	 * Backspace key press: removePos is paraEleStart of keyPressed() and
	 * the length is BULLET_LENGTH.
	 * Enter key press: removePos is previous paraEleStart of keyPressed() and
	 * the length is BULLET_LENGTH + 1 (+1 includes CR).
	 */
	private void removeBullet(int removePos, int length) {

		try {
			getEditorDocument().remove(removePos, length);
		}
		catch(BadLocationException ex) {
				
			throw new RuntimeException(ex);
		}
	}
	
	/*
	 * Key listener class for key press and release actions within a bulleted
	 * para. The keys include Enter, Backspace, Delete and Left. The Enter press
	 * is implemented with both the keyPressed and keyReleased methods. The Delete,
	 * Backspace and Left key press is implemented within the keyPressed.
	 */
	public class BulletParaKeyListener implements KeyListener {
	
		// These two variables are derived in the keyPressed and are used in
		// keyReleased method.
		private String prevParaText_;
		private int prevParaEleStart_;
		
		// Identifies if a key is pressed in a bulleted para. 
		// This is required to distinguish from the numbered para.
		private boolean bulletedPara_; 


		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			
			bulletedPara_ = false;
			int pos = editor__.getCaretPosition();
			
			if (! isBulletedParaForPos(pos)) {
			
				return;
			}
			
			Element paraEle = getEditorDocument().getParagraphElement(pos);
			switch (e.getKeyCode()) {
			
				case KeyEvent.VK_LEFT: // same as that of VK_KP_LEFT
				case KeyEvent.VK_KP_LEFT: int newPos = pos - (BULLET_LENGTH + 1);
										doLeftArrowKeyRoutine(newPos, startPosPlusBullet__);
										break;			
				case KeyEvent.VK_DELETE: doDeleteKeyRoutine(paraEle, pos);
										break;
				case KeyEvent.VK_BACK_SPACE: doBackspaceKeyRoutine(paraEle);
										break;
				case KeyEvent.VK_ENTER: getPrevParaDetails(pos);
			}

		} // keyPressed()
		
		private boolean isBulletedParaForPos(int caretPos) {

			Element paraEle = getEditorDocument().getParagraphElement(caretPos);

			return isBulletedPara(paraEle.getStartOffset());

		}
		
		// This method is used with Enter key press routine.
		// Two instance variable values are derived here and are used
		// in the keyReleased() method: prevParaEleStart_ and prevParaText_
		private void getPrevParaDetails(int pos) {
		
			pos =  pos - 1;
			
			if (isBulletedParaForPos(pos)) {
			
				bulletedPara_ = true;
				Element paraEle = getEditorDocument().getParagraphElement(pos);
				prevParaEleStart_ = paraEle.getStartOffset();
				prevParaText_ =
						getPrevParaText(prevParaEleStart_, paraEle.getEndOffset());
			}
		}
		
		// Delete key press routine within bulleted para.
		private void doDeleteKeyRoutine(Element paraEle, int pos) {

			int paraEleEnd = paraEle.getEndOffset();
			
			if (paraEleEnd > getEditorDocument().getLength()) {

				return; // no next para, end of document text
			}
				
			if (pos == (paraEleEnd - 1)) { // last char of para; -1 is for CR
				
				if (isBulletedParaForPos(paraEleEnd + 1)) {

					// following para is bulleted, remove
					removeBullet(pos, BULLET_LENGTH);
				}
				// else, not a bulleted para
				// delete happens normally (one char)
			}
		}
		
		// Backspace key press routine within a bulleted para.
		// Also, see EditorCaretListener.
		private void doBackspaceKeyRoutine(Element paraEle) {
			
			// In case the position of cursor at the backspace is just 
			// before the bullet (that is BULLET_LENGTH).
			if (startPosPlusBullet__) {

				removeBullet(paraEle.getStartOffset(), BULLET_LENGTH);
				startPosPlusBullet__ = false;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		
			if (! bulletedPara_) {

				return;
			}
				
			switch (e.getKeyCode()) {
		
				case KeyEvent.VK_ENTER: doEnterKeyRoutine();
										break;
			}
		}

		// Enter key press routine within a bulleted para.
		// Also, see keyPressed().
		private void doEnterKeyRoutine() {

			String prevParaText = prevParaText_;
			int prevParaEleStart = prevParaEleStart_;

			// Check if prev para with bullet has text					
			if (prevParaText.length() < 4) {
				
				// Para has bullet and no text, remove bullet+CR from para
				removeBullet(prevParaEleStart, (BULLET_LENGTH + 1));
				editor__.setCaretPosition(prevParaEleStart);
				return;
			}
			// Prev para with bullet and text
			
			// Insert bullet for next para (current position), and
			// prev para attributes are used for this bullet.	
			insertBullet(editor__.getCaretPosition(), prevParaEleStart); 
		}
		
	} // BulletParaKeyListener

	private String getPrevParaText(int prevParaEleStart, int prevParaEleEnd) {
			
		String prevParaText = "";
			
		try {
			prevParaText = getEditorDocument().getText(prevParaEleStart, 
											(prevParaEleEnd -  prevParaEleStart));
		}
		catch(BadLocationException ex) {
				
			throw new RuntimeException(ex);
		}
			
		return prevParaText;
	}
	
	/*
	 * Left arrow key press routine within a bulleted and numbered paras.
	 * Moves the cursor when caret is at position startPosPlusBullet__ or at
	 * startPosPlusNum__ for bullets or numbers respectively.
	 * Also see EditorCaretListener.
	 *
	 * The parameter startTextPos indicates if startPosPlusBullet__ or
	 * startPosPlusNum__. pos is the present caret postion.
	 */
	private void doLeftArrowKeyRoutine(int pos, boolean startTextPos) {
	
		if (! startTextPos) {
		
			return;
		}
		
		// Check if this is start of document
		Element paraEle =
				getEditorDocument().getParagraphElement(editor__.getCaretPosition());
		int newPos = (paraEle.getStartOffset() == 0) ? 0 : pos;
		
		// Position the caret in an EDT, otherwise the caret is
		// positioned at one less position than intended.
		SwingUtilities.invokeLater(new Runnable() {
		
			public void run() {
			
				editor__.setCaretPosition(newPos);
			}
		});
	}

	/*
	 * This listener is used with the bulleted and numbered para actions.
	 * The bulleted item's bullet is made of bullet + space. The cursor (caret)
	 * is not allowed to position at the bullet para's first position and at 
	 * the space after the bullet. This listener controls the cursor position
	 * in such cases; the cursor jumps/moves to the after bullet+space position
	 * (indicated by startPosPlusBullet__ boolean instance variable).
	 *
	 * Also, the backspace and left-arrow key usage requires startPosPlusBullet__
	 * to perform the routines (see doLeftArrowKeyRoutine() and BulletParaKeyListener).
	 *
	 * This is also similar for numbered paras (see startPosPlusNum__ and
	 * NumbersParaKeyListener).
	 */
	private class EditorCaretListener implements CaretListener {
	
		@Override
		public void caretUpdate(CaretEvent e) {

			startPosPlusBullet__ = false;
			startPosPlusNum__ = false;
			Element paraEle = 
				getEditorDocument().getParagraphElement(editor__.getCaretPosition());
			int paraEleStart = paraEle.getStartOffset();
	
			if (isBulletedPara(paraEleStart)) {

				if (e.getDot() == (paraEleStart + BULLET_LENGTH)) {

					startPosPlusBullet__ = true;
				}
				else if (e.getDot() < (paraEleStart + BULLET_LENGTH)) {
			
					editor__.setCaretPosition(paraEleStart + BULLET_LENGTH);
				}
				else {
					// continue
				}
			}
			else if (isNumberedPara(paraEleStart)) {
			
				int numLen = getNumberLength(paraEleStart);

				if (e.getDot() < (paraEleStart + numLen)) {
			
					editor__.setCaretPosition(paraEleStart + numLen);
				}
				else if (e.getDot() == (paraEleStart + numLen)) {

					startPosPlusNum__ = true;
				}
				else {
					// continue
				}
			}
			else {
				// not a bulleted or numbered para
			}
		}
	}
	
	/*
	 * Returns the numbered para's number length. This length includes
	 * the number + dot + space. For example, the text "12. A Numbered para..."
	 * has the number length of 4.
	 */
	private int getNumberLength(int paraEleStart) {
	
		Integer num = getParaNumber(paraEleStart);
		int len = num.toString().length() + 2; // 2 = dot + space after number
		return len;
	}
	
	private Integer getParaNumber(int paraEleStart) {
		
		AttributeSet attrSet = getParaStartAttributes(paraEleStart);		
		Integer paraNum = (Integer) attrSet.getAttribute(NUMBERS_ATTR);
		return paraNum;
	}

	/*
	 * The insert number routine; inserts the number in the editor document. This
	 * routine is used from the insert action (ActionListener) as well as number
	 * para key press actions (keyPressed or keyReleased methods of KeyListener).
	 *
	 * The parameter insertPos is the position at which the number is to be
	 * inserted. The parameter attributesPos is the position from which the number
	 * is to get its attributes (like color, font, size, etc.). The two parameter
	 * values are derived differently for the insert and the number para key press
	 * actions. The patameter num is the number being inserted.
	 *
	 * Number insert action: the insertPos and attributesPos is the same,
	 * the paraEleStart.
	 * Enter key press: the insertPos is the current caret position of keyReleased(),
	 * and the attributesPos is the previous paraEleStart position from
	 * keyPressed() method.
	 */
	private void insertNumber(int insertPos, int attributesPos, Integer num) {

		try {
			getEditorDocument().insertString(insertPos,
								getNumberString(num),
								getNumbersAttributes(attributesPos, num));
		}
		catch(BadLocationException ex) {

			throw new RuntimeException(ex);
		}
	}

	private String getNumberString(Integer nextNumber) {
		
		return new String(nextNumber.toString() + "." + " ");
	}
		
	private AttributeSet getNumbersAttributes(int paraEleStart, Integer number) {
		
		AttributeSet attrs1 = getParaStartAttributes(paraEleStart);
		SimpleAttributeSet attrs2 = new SimpleAttributeSet(attrs1);
		attrs2.addAttribute(NUMBERS_ATTR, number);
		return attrs2;
	}	

	/*
	 * The remove number routine; removes the number in the editor document. This
	 * routine is used from the delete action (ActionListener) as well as the number
	 * para key press actions (keyPressed or keyRemoved methods of KeyListener).
	 * The keys include the Enter, Backspace, Delete keys.
	 *
	 * The parameter removePos is the start position and the length is the length
	 * of text to be removed. Length of characters removed is derived from the
	 * method getNumberLength() or +1 (includes carriage return folowing the
	 * number length). The two parameter values are derived differently for
	 * number remove action and number para key press actions. 
	 *
	 * Number remove action: removePos is paraEleStart and the length from
	 * the method getNumberLength().
	 * Delete key press: removePos is current caret pos of keyPressed() and
	 * the length from the method getNumberLength().
	 * Backspace key press: removePos is paraEleStart of keyPressed() and
	 * the length from the method getNumberLength().
	 * Enter key press: removePos is previous paraEleStart of keyPressed() and
	 * the length from the method getNumberLength() + 1 (+1 includes CR).
	 */
	private void removeNumber(int removePos, int length) {
				
		try {
			getEditorDocument().remove(removePos, length);
		}
		catch(BadLocationException ex) {
				
			throw new RuntimeException(ex);
		}
	}
	
	/*
	 * Key listener class for key press and release actions within a numbered
	 * para. The keys include Enter, Backspace, Delete and Left. The Enter press
	 * is implemented with both the keyPressed and keyReleased methods. The Delete,
	 * Backspace and Left key press is implemented within the keyPressed.
	 *
	 * This also includes key press actions (backspace, enter and delete) for
	 * the text selected within the numbered paras.
	 */
	public class NumbersParaKeyListener implements KeyListener {
	
		// These two variables are derived in the keyPressed and are used in
		// keyReleased method.
		private String prevParaText_;
		private int prevParaEleStart_;
		
		// Identifies if a key is pressed in a numbered para.
		// This is required to distinguish from the bulleted para.
		private boolean numberedPara_; 


		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		
			String selectedText = editor__.getSelectedText();
			
			if ((selectedText == null) || (selectedText.trim().isEmpty())) {

				// continue, processing key press without any selected text
			}
			else {
				// text is selected within numbered para and a key is pressed
				doReplaceSelectionRoutine();
				return;
			}
			
			numberedPara_ = false;
			int pos = editor__.getCaretPosition();
			
			if (! isNumberedParaForPos(pos)) {
			
				return;
			}
			
			Element paraEle = getEditorDocument().getParagraphElement(pos);
			int paraEleStart = paraEle.getStartOffset();
			
			switch (e.getKeyCode()) {
			
				case KeyEvent.VK_LEFT: // same as that of VK_KP_LEFT
				case KeyEvent.VK_KP_LEFT: int newPos = pos - 
													(getNumberLength(paraEleStart) + 1);
										doLeftArrowKeyRoutine(newPos, startPosPlusNum__);
										break;			
				case KeyEvent.VK_DELETE: doDeleteKeyRoutine(paraEle, pos);
										break;
				case KeyEvent.VK_BACK_SPACE: doBackspaceKeyRoutine(paraEle);
										break;
				case KeyEvent.VK_ENTER: getPrevParaDetails(pos);
										break;
			}

		} // keyPressed()

		private boolean isNumberedParaForPos(int caretPos) {

			Element paraEle = getEditorDocument().getParagraphElement(caretPos);

			return isNumberedPara(paraEle.getStartOffset());

		}
		
		/*
		 * Routine for processing selected text with numbered paras
		 * after pressing Enter, Backspace or Delete keys, and the
		 * paste insert replacing the selected text.
		 */
		private void doReplaceSelectionRoutine() {
			
			// Get selection start and end para details.
			// Check if there are numbered paras at top and bottom
			// of the selection. Re-number if needed i.e., when selection
			// is replaced in the middle of numbered paras or at the top
			// items of the numbered paras.
			
			StyledDocument doc = getEditorDocument();			
			Element topParaEle = doc.getParagraphElement(editor__.getSelectionStart());
			Element bottomParaEle = doc.getParagraphElement(editor__.getSelectionEnd());

			int bottomParaEleStart = bottomParaEle.getStartOffset();			
			int bottomParaEleEnd = bottomParaEle.getEndOffset();
			
			// No numbered text at bottom, no processing required -or-
			// no next para after selection end (end of document text).
			if ((! isNumberedPara(bottomParaEleStart)) ||
					(bottomParaEleEnd > doc.getLength())) {

				return;
			}
			
			// Check if para following the selection end is numbered or not.
			Element paraEle = doc.getParagraphElement(bottomParaEleEnd + 1);
			int paraEleStart = paraEle.getStartOffset();
			
			if (! isNumberedPara(paraEleStart)) {
			
				return;
			}
			
			// Process re-numbering

			Integer numTop = getParaNumber(topParaEle.getStartOffset());
			
			if (numTop != null) {
				
				// There are numbered items above the removed para, and
				// there are numbered items following the removed para;
				// bottom numbers start from numTop + 1.
				doNewNumbers(paraEleStart, numTop);
			}
			else { 
				// numTop == null
				// There are no numbered items above the removed para, and
				// there are numbered items following the removed para;
				// bottom numbers start from 1.
				doNewNumbers(paraEleStart, 0);
			}

		} // doReplaceSelectionRoutine()
		
		/*
		 * Common routine to arrive at new numbers and replace the previous
		 * ones after the following actions within numbered para list:
		 * - Enter, Delete, Backspace key press.
		 * - Delete, Backspace and paste-insert selected text.
		 */
		private void doNewNumbers(int nextParaEleStart, Integer newNum) {
	
			StyledDocument doc = getEditorDocument();
			Element nextParaEle = doc.getParagraphElement(nextParaEleStart);
			boolean nextParaIsNumbered = true;

			NUMBERED_PARA_LOOP:
			while (nextParaIsNumbered) {

				Integer oldNum = getParaNumber(nextParaEleStart);
				newNum++;				
				replaceNumbers(nextParaEleStart, oldNum, newNum);

				nextParaIsNumbered = false;
				
				// Get following para details after number is replaced for a para

				int nextParaEleEnd = nextParaEle.getEndOffset();
				int nextParaPos = nextParaEleEnd + 1;
				
				if (nextParaPos > doc.getLength()) {

					break NUMBERED_PARA_LOOP; // no next para, end of document text
				}
				
				nextParaEle = doc.getParagraphElement(nextParaPos);
				nextParaEleStart = nextParaEle.getStartOffset();
				nextParaIsNumbered = isNumberedPara(nextParaEleStart);			
			}
			// NUMBERED_PARA_LOOP
			
		} // doNewNumbers()

		private void replaceNumbers(int nextParaEleStart, Integer prevNum,
									Integer newNum) {
				
			try {
				((DefaultStyledDocument) getEditorDocument()).replace(
													nextParaEleStart,
													getNumberString(prevNum).length(), 
													getNumberString(newNum), 
								getNumbersAttributes(nextParaEleStart, newNum));
			}
			catch(BadLocationException ex) {
				
				throw new RuntimeException(ex);
			}
		}	
	
		// Delete key press routine within a numbered para.
		private void doDeleteKeyRoutine(Element paraEle, int pos) {

			int paraEleEnd = paraEle.getEndOffset();
			
			if (paraEleEnd > getEditorDocument().getLength()) {

				return; // no next para, end of document text
			}
				
			if (pos == (paraEleEnd - 1)) { // last char of para; -1 is for CR
			
				Element nextParaEle =
						getEditorDocument().getParagraphElement(paraEleEnd + 1);
				int nextParaEleStart = nextParaEle.getStartOffset();
				
				if (isNumberedPara(nextParaEleStart)) {

					removeNumber(pos, getNumberLength(nextParaEleStart));
					doReNumberingForDeleteKey(paraEleEnd + 1);
				}
				// else, not a numbered para
				// delete happens normally (one char)
			}
		}

		private void doReNumberingForDeleteKey(int delParaPos) {
	
			// Get para element details where delete key is pressed
			StyledDocument doc = getEditorDocument();
			Element paraEle = doc.getParagraphElement(delParaPos);
			int paraEleStart = paraEle.getStartOffset();
			int paraEleEnd = paraEle.getEndOffset();

			// Get bottom para element details
			Element bottomParaEle = doc.getParagraphElement(paraEleEnd + 1);
			int bottomParaEleStart = bottomParaEle .getStartOffset();

			// In case bottom para is not numbered or end of document,
			// no re-numbering is required.
			if ((paraEleEnd > doc.getLength()) || 
					(! isNumberedPara(bottomParaEleStart))) {
			
				return;
			}
			
			Integer n = getParaNumber(paraEleStart);
			doNewNumbers(bottomParaEleStart, n);	
		}

		// Backspace key press routine within a numbered para.
		// Also, see EditorCaretListener.
		private void doBackspaceKeyRoutine(Element paraEle) {
			
			// In case the position of cursor at the backspace is just after
			// the number: remove the number and re-number the following ones.
			if (startPosPlusNum__) {

				int startOffset = paraEle.getStartOffset();
				removeNumber(startOffset, getNumberLength(startOffset));
				doReNumberingForBackspaceKey(paraEle, startOffset);				
				startPosPlusNum__ = false;
			}
		}
		
		private void doReNumberingForBackspaceKey(Element paraEle, int paraEleStart) {
			
			// Get bottom para element and check if numbered.
			StyledDocument doc = getEditorDocument();		
			Element bottomParaEle = doc.getParagraphElement(paraEle.getEndOffset() + 1);
			int bottomParaEleStart = bottomParaEle.getStartOffset();
		
			if (! isNumberedPara(bottomParaEleStart)) {
		
				return; // there are no numbers following this para, and
						// no re-numbering required.
			}
		
			// Get top para element and number
			
			Integer numTop = null;
			
			if (paraEleStart == 0) {
			
				// beginning of document, no top para exists
				// before the document start; numTop = null
			}
			else {	
				Element topParaEle = doc.getParagraphElement(paraEleStart - 1);
				numTop = getParaNumber(topParaEle.getStartOffset());
			}
		
			if (numTop == null) {
		
				// There are no numbered items above the removed para, and
				// there are numbered items following the removed para;
				// bottom numbers start from 1.
				doNewNumbers(bottomParaEleStart, 0);
			}
			else { 
				// numTop != null
				// There are numbered items above the removed para, and
				// there are numbered items following the removed para;
				// bottom numbers start from numTop + 1.
				doNewNumbers(bottomParaEleStart, numTop);
			}
		}
		
		// This method is used with Enter key press routine.
		// Two instance variable values are derived here and are used
		// in the keyReleased() method: prevParaEleStart_ and prevParaText_
		private void getPrevParaDetails(int pos) {
		
			pos =  pos - 1;
			
			if (isNumberedParaForPos(pos)) {
			
				numberedPara_ = true;
				Element paraEle = getEditorDocument().getParagraphElement(pos);
				prevParaEleStart_ = paraEle.getStartOffset();
				prevParaText_ =
						getPrevParaText(prevParaEleStart_, paraEle.getEndOffset());
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		
			if (! numberedPara_) {

				return;
			}
		
			switch (e.getKeyCode()) {
			
				case KeyEvent.VK_ENTER: doEnterKeyRoutine();
										break;
			}
		}
		
		// Enter key press routine within a numbered para.
		// Also, see keyPressed().
		private void doEnterKeyRoutine() {
			
			String prevParaText = prevParaText_;
			int prevParaEleStart = prevParaEleStart_;					
			int len = getNumberLength(prevParaEleStart) + 1; // +1 for CR

			// Check if prev para with numbers has text					
			if (prevParaText.length() == len) {
				
				// Para has numbers and no text, remove number from para
				removeNumber(prevParaEleStart, len);		
				editor__.setCaretPosition(prevParaEleStart);
				return;
			}
			// Prev para with number and text,			
			// insert number for new para (current position)
			Integer num = getParaNumber(prevParaEleStart);
			num++;
			insertNumber(editor__.getCaretPosition(), prevParaEleStart, num);
			
			// After insert, check for numbered paras following the newly
			// inserted numberd para; and re-number those paras.
			
			// Get newly inserted number para details
			StyledDocument doc = getEditorDocument();	
			Element newParaEle = doc.getParagraphElement(editor__.getCaretPosition());
			int newParaEleEnd = newParaEle.getEndOffset();

			if (newParaEleEnd > doc.getLength()) {

				return; // no next para, end of document text
			}

			// Get next para (following the newly inserted one) and
			// re-number para only if already numered.
			Element nextParaEle = doc.getParagraphElement(newParaEleEnd + 1);
			int nextParaEleStart = nextParaEle.getStartOffset();
			
			if (isNumberedPara(nextParaEleStart)) {

				doNewNumbers(nextParaEleStart, num);
			}

		} // doEnterKeyRoutine()
		
	} // NumbersParaKeyListener

	private class NewFileListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			initEditorAttributes();
			StyledDocument styledDocument =getNewDocument();
			editor__.setDocument(styledDocument);
			documents.put("New file",styledDocument);
			file__ = null;
			setFrameTitleWithExtn("New file");
		}
		
		private void initEditorAttributes() {
		
			AttributeSet attrs1 = editor__.getCharacterAttributes();
			SimpleAttributeSet attrs2 = new SimpleAttributeSet(attrs1);
			attrs2.removeAttributes(attrs1);
			editor__.setCharacterAttributes(attrs2, true);
		}
	}
	
	private class OpenFileListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
		
			file__ = chooseFile();
			
			if (file__ == null) {
			
				return;
			}
			
			readFile(file__);
			setFrameTitleWithExtn(file__.getName());
			addLabelToLeftMenu(file__.getName());
		}
		
		private File chooseFile() {
		
			JFileChooser chooser = new JFileChooser();
			
			if (chooser.showOpenDialog(frame__) == JFileChooser.APPROVE_OPTION) {
			
				return chooser.getSelectedFile();
			}
			else {
				return null;
			}
		}
		
		private StyledDocument readFile(File file) {
	
			StyledDocument doc = getNewDocument();
			String fileAsString= "";
			try {
				InputStream fis = new FileInputStream(file);

			BufferedReader buf = new BufferedReader(new InputStreamReader(fis));
			String line = buf.readLine(); StringBuilder sb = new StringBuilder();
				while(line != null){
					sb.append(line).append("\n");
					line = buf.readLine(); }
				 fileAsString = sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Style style = doc.addStyle("StyleName", null);

			try {
				doc.insertString(doc.getLength(), fileAsString,style);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			editor__.setDocument(doc);
			doc.addUndoableEditListener(new UndoEditListener());
			applyFocusListenerToPictures(doc);
			documents.put(file.getName(),doc);
			return doc;
		}
		
		private void applyFocusListenerToPictures(StyledDocument doc) {

			ElementIterator iterator = new ElementIterator(doc);
			Element element;
			
			while ((element = iterator.next()) != null) {
			
				AttributeSet attrs = element.getAttributes();
			
				if (attrs.containsAttribute(ELEM, COMP)) {

					JButton picButton = (JButton) StyleConstants.getComponent(attrs);
					picButton.addFocusListener(new PictureFocusListener());
				}
			}
		}
	}

	private void addLabelToLeftMenu(String name) {
		JTextField newFile1 = new JTextField();
		newFile1.addMouseListener(new FileClickedMouseListener(name, this));
		newFile1.setText(name);
		filesPanel.add(newFile1);
		filesPanel.revalidate();
	}

	private class SaveFileListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
		
			if (file__ == null) {
		
				file__ = chooseFile();
			
				if (file__ == null) {
			
					return;
				}
			}
			
			DefaultStyledDocument doc = (DefaultStyledDocument) getEditorDocument();
			
			try (OutputStream fos = new FileOutputStream(file__);
					ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				
				oos.writeObject(doc);
			}
			catch (IOException ex) {

				throw new RuntimeException(ex);
			}
			
			setFrameTitleWithExtn(file__.getName());
		}

		private File chooseFile() {
		
			JFileChooser chooser = new JFileChooser();
			
			if (chooser.showSaveDialog(frame__) == JFileChooser.APPROVE_OPTION) {

				return chooser.getSelectedFile();
			}
			else {
				return null;
			}
		}
	}
}