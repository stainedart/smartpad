package org.concordia.soen.smartpad;

import javax.swing.text.StyledDocument;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by maysam on 14/04/18.
 */
public class FileClickedMouseListener implements MouseListener{
     String fileName;
     SmartPad smartPad;


    public FileClickedMouseListener(String name, SmartPad smartPad) {
        this.fileName = name;
        this.smartPad =smartPad;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.print("mouseClicked" + this.fileName);
        StyledDocument styledDocument=smartPad.documents.get(this.fileName);
        smartPad.editor__.setDocument(styledDocument);

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
