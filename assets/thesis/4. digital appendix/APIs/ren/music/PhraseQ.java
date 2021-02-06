/*
 * Created on 14/10/2005
 *
 * @author Rene Wooller
 */
package ren.music;

/*
 * PhraseQ.java
 * 
 * Created on 14 November 2003, 19:50
 */

import java.util.Vector;

import jm.music.data.Part;
import jm.music.data.Phrase;
import ren.util.PO;

/**
 * 
 * @author Rene Wooller
 */
public class PhraseQ {

    private int pos = 0;

    private int highest = 0;

    private Phrase[] q;

    /** Creates a new instance of PhraseQ */
    public PhraseQ(int qlength) {
        q = new Phrase[qlength];
    }

    public PhraseQ() {
        this(300);
    }

    public Phrase getPhrase() {
    	Phrase ret = q[(pos-1+q.length)%q.length];
        if (ret == null)
            return null;
        return ret.copy();
    }

    public void putPhrase(Phrase p) {
        q[pos++] = p;
        if (pos > highest)
            highest++;
        pos = pos % q.length;
    }

    public boolean isEmpty() {
        return highest == 0;
    }

    public boolean isInSync(int toSyncWith) {
        return toSyncWith % q.length == pos;
    }

    public int getLength() {
        return q.length;
    }

    public Phrase[] getWhole() {
        if (highest < q.length) {
            System.out.println(" q hasn't been initialised yet");
            return null;
        }

        Phrase[] toRet = new Phrase[q.length];
        for (int i = 0; i < toRet.length; i++) {
            toRet[i] = q[i].copy();
            // System.out.println("toRet " +
            // toRet[i].toString());
        }
        return toRet;
    }

    /**
     * A useful side effect of this method is that it
     * links together phrases that have the same start
     * time, in reverse order
     * 
     * @param v
     * @param n
     *            is the number of previous note to put
     *            in the lpos. eg 1 is the most recent
     *            note that was put into the Q.
     * @param st
     *            wether or not to consider two phrases
     *            with the same start time as one
     */
    public void fillWithLast(Vector v, int n, boolean st) {
        if (n == 0)
            return;
        if (n > q.length) {
            try {
                Exception ex = new Exception(
                        "n exceeds q length");
                ex.fillInStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean toShort = false;
        if( n >= q.length)
            toShort = true;
        
        
        int lpos = -1;
        
        lpos = (pos - 1 + q.length) % q.length; // the
        // if there are no other previous phrases, just return this one
        if (q[lpos] == null) {
            v.add(this.q[pos]);
            return;
        } 
        
        // search back through to see how many have the
        // same start time and adjust the number of
        // phrases
        // to get accordingly.
        if(st) {
           
            int cnt = 0;
            int phrcnt = 0;
            double pst = q[lpos].getStartTime();
            int plp = lpos;

            while (cnt < n) {
                phrcnt++;
                lpos = (lpos - 1 + q.length) % q.length;
                if(q[lpos] == null)
                    break;
                
                if (pst != q[lpos].getStartTime()) {
                    cnt++;
                    pst = q[lpos].getStartTime();

                } else {
                    q[plp].setLinkedPhrase(q[lpos]);
                }
                plp = lpos;
            }

            if (phrcnt >= q.length) {
                toShort = true;
            }

        } else if (!st){
            lpos = pos - n - 1 + q.length;
        }
        lpos = (lpos) % q.length;

        int end = (pos - 1 + q.length) % q.length;

        if (!toShort) {
            while (lpos != end) {
                lpos = (lpos + 1) % q.length;
                if(q[lpos]!= null)
                    v.add(this.q[lpos]);
            }
        } else {
            fillWithWhole(v);
            
            System.out.println("the length of the PhraseQ is too small");
            System.out.println("increasing the capacity by half...");
            Phrase [] nq = new Phrase [(int)(q.length*1.5)];
            System.arraycopy(q, 0, nq, 0, q.length);
            this.q = nq;
            
        }

    }

    private void fillWithWhole(Vector v) {
        int tpos = (pos+1+q.length)%q.length;
        v.add(q[pos]);
        while(tpos != pos)  {
            if(q[tpos] != null)
                v.add(q[tpos]);
            
            tpos = (tpos +1 + q.length)%q.length;
            
        }
    }

    public Phrase[] getLastHalf() {
        if (highest < q.length) {
            System.out.println(" q hasn't been initialised yet");
            return null;
        }
        // if it's 0, copy the second half
        Phrase[] toRet = new Phrase[(int) (q.length / 2)];
        int tpos = pos;
        if (((int) (((tpos)) / (q.length / 2))) == 0) {
            for (int i = 0; i < toRet.length; i++) {
                toRet[i] = q[i + (int) (q.length / 2)].copy();
            }
        } else {
            for (int i = 0; i < toRet.length; i++) {
                toRet[i] = q[i].copy();
            }
        }
        return toRet;
    }

    /**
     * 
     * @param at the current beat (at must be absolute, not %scope)
     * @param sc the scope, used to modulus the start times
     * @param p the part to put the phrases into
     * @param modst the res, used to shift the start time for realtime application
     * @return
     */
	public void insertLatestPhraseAt(double at, double sc, Part p, double modst) {
		//PO.p("inserting at " + at);
		
		int i= (pos-1+q.length)%q.length;
		
		while(i > 0 && q[i].getStartTime() >= at-sc) {
			//PO.p("q[i].getStartTime() = " + q[i].getStartTime() + 
			//		" at-sc = " + (at-sc));
			if(q[i].getStartTime() == at-sc) {
				//PO.p("Adding " + q[i].)
				Phrase toAdd = q[i].copy();
				if(modst > 0)
					toAdd.setStartTime(toAdd.getStartTime()%modst);
				p.add(toAdd);
				//PO.p("added ", p, 2);
			}
			i--;
		}
		
	}
}
