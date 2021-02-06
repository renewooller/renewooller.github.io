/*
 * Created on 15/10/2005
 *
 * @author Rene Wooller
 */
package ren.music;

import jm.music.data.Phrase;
import ren.util.PO;

public class PhraseLL {

    private PhraseLink st;
    private PhraseLink en;
    private PhraseLink cur;
    private Phrase start;
    private int len;
    public PhraseLL() {
        clear();
    }
    
    public void clear() {
        st = new PhraseLink();
        en = st;
        cur = st;
        start = null;
        st.next = en;
        len = 0;
    }
    
    public void add(Phrase phr) {
        len++;
        if(st.phr == null) {
            st.phr = phr;
            return;
        }
        en.next = new PhraseLink(phr);
        en = en.next;
        en.next = st;  
    }
    
    public Phrase en() {
        return en.phr;
    }
    
    public Phrase st() {
        return st.phr;
    }
    
    public Phrase iter() {
        Phrase ret = cur.phr;
        cur = cur.next;
        return ret;
    }
    
    public void resetIter() {
        cur = st;
        
    }
    
    public int length() {
        return len;
    }

    public Phrase iterAt() {
        return this.cur.phr;
    }

    public boolean remove(Phrase phr) {
 
        
        if(st == en) {
            if(phr == st.phr) {
                this.clear();
                
                return true;
            }
            return false;
        }
        
        resetIter();
        
        PhraseLink pre = this.en;
        PhraseLink now = this.st;
        Phrase at = null;
        while(at != en.phr){
            at = now.phr;
           
            if(at == phr) {
              
                if(now == st) { 
                    st = now.next;
                }
                if(now == en) {
                    en = pre;
                }
               
                pre.next = now.next;
                
                this.len--;
                
                resetIter();
                return true;
            }
            pre = now;
            now = now.next;  
        }
        return false;
    }

}
