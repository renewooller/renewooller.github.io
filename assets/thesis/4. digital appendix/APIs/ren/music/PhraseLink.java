/*
 * Created on 15/10/2005
 *
 * @author Rene Wooller
 */
package ren.music;

import jm.music.data.Phrase;

class PhraseLink {
    
    public Phrase phr;
    
    public PhraseLink next;
    
    public PhraseLink prev;
    
    public PhraseLink() {
        super();
    }
    
    public PhraseLink(Phrase phr) {
        super();
        this.phr = phr;
    }

}