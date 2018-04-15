package org.concordia.soen.smartpad;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
        System.out.print("mouseClicked" + this.fileName);
        StyledDocument styledDocument=smartPad.documents.get(this.fileName);
        smartPad.editor__.setDocument(styledDocument);
        smartPad.frame__.setTitle(this.fileName);
        smartPad.filesPanel.revalidate();
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
