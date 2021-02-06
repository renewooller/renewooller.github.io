/*
 * Created on 26/02/2006
 *
 * @author Rene Wooller
 */
package ai;

import jm.music.data.Phrase;

public abstract class NoteWeighter {

	public NoteWeighter() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public abstract double weigh(Phrase p);
	
}
