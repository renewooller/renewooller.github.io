/*
 * Created on 31/10/2005
 *
 * @author Rene Wooller
 */
package ren.gui.components;

import javax.swing.JPanel;
import javax.swing.Popup;

public class PopupPanel extends JPanel {

    protected Popup popup;
    
    public PopupPanel() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

}
