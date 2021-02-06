/*
 * Created on May 24, 2006
 *
 * @author Rene Wooller
 */
package music;

import gui.musicArea.SepamoHolder;
import jm.music.data.Part;
import jm.music.data.Score;
import jmms.TickEvent;

import org.w3c.dom.Element;

import ren.util.PO;


/**
 * to get it working, you need to load in the morphs one by one giving each one
 * a differeny variation number (under the file menu in the mainframe menubar
 * 
 * @author wooller
 *
 *18/08/2006
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class MultiTableMorph extends MusicGenerator {

	//private PartMorph [] pms = new PartMorph [20];
	//private int npms = 0;
	private SepamoHolder sepho;
	
	public MultiTableMorph construct(SepamoHolder sh) {
		this.sepho = sh;
		
		return this;
	}
	
	public MultiTableMorph() {
		super();
		
		// TODO Auto-generated constructor stub
	}
	
	public Score generateTickScore(TickEvent e) {
		updateTickScore(e); 
		
		
		//PO.p("2 lscore " + this.lscore.toString());
		
		Score tr= lscore.getJMScore();
		
		//PO.p(tr.toString());
		
		return tr;//lscore.getJMScore();
	}
	
	private void updateTickScore(TickEvent e) {
		boolean [] plapa = new boolean [this.sepho.getSepNum()]; // if true, play this part
        // check to see if one of them is solo
        boolean onesolo = false;
        // assume one of them is soloing
        for(int i = 0; i< this.sepho.getSepNum(); i++) {
        	plapa[i] = false;
        	
        	// if one of them is soloing
        	if(this.sepho.getSepamo(i).getCurrentVariation().getSolo()) {
        		onesolo = true;
        		plapa[i] = true; // play this one
        	} else { // otherwise they are all false anyway
        		plapa[i] = false;
        	}   	
        }
      //  PO.p(plapa);
        // holds the output from the part morph
        Part[] lcp = new Part[2];
  //    PO.p("number of pms = " + this.sepho.getSepNum());
        for (int i=0; i < this.sepho.getSepNum(); i++) {
        	
        	if((onesolo && plapa[i]) || !onesolo) {// mute's already taken care of
        		// handle cases where the partmorph has been set to null
        		if(sepho.getSepamo(i) == null) { 
        			lcp = null;
        		} else {
     //   			PO.p("pms " + i + " tracker " + this.mtracker.getPos());
        			// 0.5/watever - it isn't used because it is individual
        			lcp = this.sepho.getSepamo(i).getCurrentVariation().getTickMorph(0.5, e);
        		}
        		
        		if(lcp != null) {
        			//TODO this is not a fix, 
        		//	lcp[0].setIdChannel(5);
        		//	lcp[1].setIdChannel(lcp[0].getIDChannel());
        			
        			this.lscore.getLPart(i * 2)
                		.setPart(lcp[0].copy());
        			this.lscore.getLPart(i * 2 + 1)
                		.setPart(lcp[1].copy());
        				
        		//	PO.p("lcp not null");
        			if(i == this.sepho.getSepNum() -1) {
        				
        //				PO.p("1 lscore " + this.lscore.toString());
        //				PO.p(" the part 0????? = " + 
        //						this.lscore.getPart(i*2).toString());
        //				PO.p(" the part 1????? = " + 
        //						this.lscore.getPart(i*2+1).toString());
        			}
        		} else {
        		//	PO.p("lcp null");
        			this.lscore.getLPart(i * 2).getPart().empty();
        			this.lscore.getLPart(i * 2 + 1).getPart().empty();
        		}
        	}
        	
        	//if(i == this.sepho.getSepNum()-1) {
        	//	PO.p("layer = " + i);
        //		PO.p("lcp [0] = "  + lcp[0].toString());
        //		PO.p("lcp [1] = " + lcp[1].toString());
        		
        	
        	
        		
        	//}
        	
        }	
        
    	
	}
	
	/**
	 * 
	 * @param morph
	 * @param layer the index of the layer
	 */
	public void setPartMorph(PartMorph morph, int layer) {
	//	PO.p("set part morph " + layer);
		if (morph != null) {
			// initialise the carrier of attributes
	//		PO.p("mm set part morph. from null = " + (morph.getFrom() == null));
			
			this.lscore.setLPart(morph.getFrom().copyEmpty(), layer * 2);
			this.lscore.setLPart(morph.getTo().copyEmpty(), layer * 2 + 1);
			
	//		PO.p("set part morph.  lscore size = " + this.lscore.size());
		}
	}

	public void select() {
		// TODO Auto-generated method stub

	}

	public void dload(Element e) {
		
		
		
	}

	private static String PMO = "partmorphs";
	public void dsave(Element e) {
		
	}

//	public void setMuteLayer(int layer, boolean m) {
//		if(layer < npms)
	//		pms[layer].setMute(m);
	//}

}
