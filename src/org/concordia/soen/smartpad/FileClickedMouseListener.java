package org.concordia.soen.smartpad;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by maysam on 14/04/18.
 */
public class FileClickedMouseListener implements MouseListener{
    String fileName;
    SmartPad smartPad;
    JLabel jLabel;


    public FileClickedMouseListener(String name, SmartPad smartPad,JLabel jLabel) {
        this.fileName = name;
        this.smartPad =smartPad;
        this.jLabel=jLabel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == 1) {
            System.out.println("mouseClicked" + this.fileName + " " + e.getButton());
            StyledDocument styledDocument = smartPad.documents.get(this.fileName);
            smartPad.editor__.setDocument(styledDocument);
            smartPad.frame__.setTitle(this.fileName);
            smartPad.filesPanel.revalidate();
        } else if (e.getButton() ==3){
            if(smartPad.USER_TYPE == "Expert") {
                System.out.println("This is a right click opening menu");
                System.out.println("mouseClicked" + this.fileName + " " + e.getButton());
                JPopupMenu popup = new JPopupMenu();
                JMenuItem menuItem = new JMenuItem("Side by side editing");
                menuItem.addActionListener(
                        new ActionListener() {
                            public void actionPerformed(ActionEvent ev) {
                                System.out.println("Opening side by side editor");
                                JTextPane sideEditor = smartPad.createEditor();
                                StyledDocument styledDocument = smartPad.documents.get(fileName);
                                smartPad.sideEditor__.setDocument(styledDocument);
                                smartPad.sideEditorScrollPane.setVisible(true);
                                smartPad.splitEditorPanel.setDividerLocation(0.5);
                                smartPad.frame__.revalidate();
                            }
                        });
                JMenuItem menuItem2 = new JMenuItem("Close side by side editing");
                menuItem2.addActionListener(
                        new ActionListener() {
                            public void actionPerformed(ActionEvent ev) {
                                System.out.println("Closing side by side editor");
                                smartPad.sideEditorScrollPane.setVisible(false);
                                //smartPad.splitEditorPanel.setDividerLocation(0);
                                smartPad.frame__.revalidate();
                            }
                        });
                popup.add(menuItem);
                popup.add(menuItem2);
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
