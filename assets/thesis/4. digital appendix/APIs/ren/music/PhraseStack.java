/*
 * Created on 15/10/2005
 *
 * @author Rene Wooller
 */
package ren.music;

import jm.music.data.Phrase;

public class PhraseStack {

    PhraseLink pl = new PhraseLink();
    
    PhraseLink curr = pl;

    public PhraseStack() {
        super();  
    }
    
    public void push(Phrase phr) {
        PhraseLink npl = new PhraseLink(phr);
        curr.next = npl;
        npl.prev = curr;
        curr = npl;
    }

    public Phrase pop() {
        if(curr.phr == null)
            return null;
        Phrase ret = curr.phr;
        curr = curr.prev;
        curr.next = null;
        return ret;
    }
    
    public Phrase peek() {
        return curr.phr;
    }
    
}