/*
 * Created on 11/07/2005
 *
 * @author Rene Wooller
 */
package jmms;

import jm.music.data.Phrase;

/**
 * @author wooller
 *
 *11/07/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public interface MidiNoteListener {

    /**
     * it comes in with the start time from the current 
     * tick, but so that it can be scaled within whatever
     * scope it is being recorded in
     * @param p
     */
    public void noteRecieved(NoteEvent e);
}
