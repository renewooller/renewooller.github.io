/**
 * includes editing methods and mouselisteners and key listeners so that edits
 * to notes can be made
 */
package ren.gui.seqEdit;

import java.awt.event.*;
import jm.music.data.*;
import javax.swing.*;

public class NotePanelEditor extends ParamNotePanel implements MouseListener {

	public NotePanelEditor() {
	}

	public NotePanelEditor(NoteToGraphicsConverter pntgc) {
		super.construct(pntgc);
		notePanel.addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == e.BUTTON1) {
			this.addPhrase(new Phrase(new Note(paramConverter
					.getPitch(e.getY()), paramConverter.getQuantise()),
					paramConverter.getBeatsX(startx)));
		}
		//else if(e.getButton() == e.BUTTON2) {

	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	private int startx, xdif;

	public void mousePressed(MouseEvent e) {
		startx = e.getX();
	}

	public void mouseReleased(MouseEvent e) {
		this.addPhrase(new Phrase(new Note(paramConverter.getPitch(e.getY()),
				paramConverter.getBeatsX(e.getX() - startx)), paramConverter
				.getBeatsX(startx)));
	}
}