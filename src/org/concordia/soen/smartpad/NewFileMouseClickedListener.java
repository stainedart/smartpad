package org.concordia.soen.smartpad;

import javax.swing.text.StyledDocument;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by maysam on 14/04/18.
 */
public class NewFileMouseClickedListener implements MouseListener {

    SmartPad smartPad;


    public NewFileMouseClickedListener( SmartPad smartPad) {
        this.smartPad =smartPad;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("create a new file");
        StyledDocument styledDocument =smartPad.getNewDocument();
        smartPad.editor__.setDocument(styledDocument);
        String fileName= "newFile-"+smartPad.numberOfNewfiles++;
        smartPad.documents.put(fileName,styledDocument);
        smartPad.addLabelToLeftMenu(fileName);
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
