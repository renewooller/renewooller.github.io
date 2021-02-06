/*
 * Created on 12/01/2006
 *
 * @author Rene Wooller
 */
package ren.util;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.tools.Mod;

public class Gen {

	public Gen() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static Phrase nphr(double st) {
    	return nphr(st, 60);
    }
    
    public static Phrase nphr(double st, int pi) {
    	Phrase phr = new Phrase(st);
    	Note n = new Note(pi, 1.0, 100);
    	phr.add(n);
    	return phr;
    }
    
    public static Part npart(double [] st) {
    	int [] pi = new int [st.length];
    	for(int i=0; i<pi.length; i++) {
    		pi[i] = (int)( Math.random()*20 + 40);
    	}
    	return npart(st, pi);
    }
    
    public static Part npart(double [] st, int [] pi) {
    	Part p = new Part("npart");
    	for(int i=0; i< st.length; i++) {
    		p.add(nphr(st[i], pi[i]));
    	}
    	return p;
    }

    /**
     * emptys the parts
     * adds random notes, within the bounds
     * specified tbe start (st) end (en) minimum pith (minp) and maximum pitch (maxp)
     * notes are quantised
     * no to notes will occupy the same point
     * @param numNotes
     * @param st 
     * @param en
     * @param minp
     * @param maxp
     * @param quantise level
     * @return
     */
    public static Part npartPol(Part p, int numNotes, double st, 
    		double en, int minp, int maxp, double q) {
    	p.empty();
    //	Part p = new Part("npart");
    	int pim = maxp-minp;
    	int oim = (int)((en-st)/q);
    	
    	if(numNotes >= pim*oim) {
    		PO.p("num notes " + numNotes + " too big for limits! pim " + 
    			 pim + " oim " + oim + " pim*oim = " + (pim*oim));
    		
    		return null;
    	}
    	boolean [][] notesDone = new boolean [pim][oim];
    	// [] pitchs [] start times
    //	PO.p("oim = " + oim + " pim = " + pim);
    	for(int i=0; i<numNotes; i++) {
    		int onset = (int)(Math.random()*oim);
    		int pitch = (int)(Math.random()*pim);
    	//	PO.p("onset = " + onset + " pitch = " + pitch);
    		if(notesDone[pitch][onset] == false) {
    			Phrase phr = new Phrase((onset*1.0)*q + st);
    			phr.add(new Note(pitch+minp, 0.25, 100));
    			p.add(phr);
    			notesDone[pitch][onset] = true;
    		} else {
    			i--; // keep on going until one is found that is not taken
    			
    		}
    	}
    	return p;
    }
    
    public static Part npartMono(Part p, int numNotes, double st, 
    		double en, int minp, int maxp, double q) {
    	p.empty();
    //	Part p = new Part("npart");
    	int pim = maxp-minp;
    	int oim = (int)((en-st)/q);
    	
    	if(numNotes > oim) {
    		PO.p("num notes " + numNotes + " too big for limits! pim " + 
       			 pim + " oim " + oim);
       		
    		return null;
    	}
    	boolean [] notesDone = new boolean [oim];
    	// [] pitchs [] start times
    //	PO.p("oim = " + oim + " pim = " + pim);
    	for(int i=0; i<numNotes; i++) {
    		int onset = (int)(Math.random()*oim);
    		int pitch = (int)(Math.random()*pim);
    	//	PO.p("onset = " + onset + " pitch = " + pitch);
    		if(notesDone[onset] == false) {
    			Phrase phr = new Phrase((onset*1.0)*q + st);
    			phr.add(new Note(pitch+minp, 0.25, 100));
    			p.add(phr);
    			notesDone[onset] = true;
    		} else {
    			i--; // keep on going until one is found that is not taken
    			
    		}
    	}
    	return p;
    }
    
    
	/*
    public static void npart(Part p, int numNotes, double st, 
    		double en, int minp, int maxp, double q) {
    	p.removeAllPhrases();
    	Gen.addnpart(p, numNotes, st, en, minp, maxp);
    	Mod.quantise(p, q, true, true, false);
    	Mod.quickSort(p);
    	Mod.removeSameRT(p);
    	
    }*/
    
    public static void addnpart(Part p, int numNotes, double st, 
    		double en, int minp, int maxp) {
    	for(int i=0; i<numNotes; i++) {
    		double onset = Math.random()*(en-st) + st;
    		Phrase phr = new Phrase(onset);
    		phr.add(new Note((int)((Math.random()*(maxp-minp)) + minp), 0.25, 100));
    		
    		
    		
    		p.add(phr);
    	}
    	
    }
}
