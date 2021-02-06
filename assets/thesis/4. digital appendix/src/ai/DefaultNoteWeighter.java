/*
 * Created on 26/02/2006
 *
 * @author Rene Wooller
 */
package ai;

import jm.music.data.Phrase;

public class DefaultNoteWeighter extends NoteWeighter {

	public static DefaultNoteWeighter di = new DefaultNoteWeighter();// the default instance
	
	public DefaultNoteWeighter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * simply uses duration
	 */
	public double weigh(Phrase p) {
		return p.getNote(0).getDuration();
	}

}
