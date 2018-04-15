package org.concordia.soen.smartpad;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by maysam on 15/04/18.
 */


        import javax.swing.text.StyledDocument;
        import java.awt.event.MouseEvent;
        import java.awt.event.MouseListener;
import java.util.Map;

/**
 * Created by maysam on 14/04/18.
 */
public class FileCloseClickedMouseListener implements MouseListener {
    String fileName;
    SmartPad smartPad;
    JPanel jPanel;


    public FileCloseClickedMouseListener(String name, SmartPad smartPad, JPanel jPanel) {
        this.fileName = name;
        this.smartPad =smartPad;
        this.jPanel=jPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.print("mouseClicked to delete" + this.fileName);
        StyledDocument styledDocument=null;
        smartPad.documents.remove(this.fileName);
        smartPad.filesPanel.remove(jPanel);
        smartPad.filesPanel.revalidate();

        String newFileNameOnLayout="";
        if(!smartPad.documents.isEmpty()){
        Map.Entry<String,StyledDocument> entry = smartPad.documents.entrySet().iterator().next();
        styledDocument = entry.getValue();
            newFileNameOnLayout=entry.getKey();
       }
        smartPad.editor__.setDocument(styledDocument);
        smartPad.frame__.setTitle(newFileNameOnLayout);
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
