/*
 * Created on 11/01/2006
 *
 * @author Rene Wooller
 */
package ai;

import jm.music.data.Part;
import jm.music.tools.Mod;
import music.LPart;
import ren.util.PO;

/**
 * testing: 
 * first note offset: done
 * first note offset more than half:  done
 * single note: done
 * no note: done
 * polyphonic: done
 * polyphonic on the last note: done	
 * unsorted: done	
 * notes occuring after scope: done
 * 
 * 
 * @author wooller
 *
 *13/01/2006
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class MTConverter {

	public MTConverter() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public MTransformat conv(LPart lp) {
		return conv(lp.getPart(),
					lp.getQuantise().getValue(), 
					lp.getScope().getValue());
	}

	/**
	 * uses realtime jmusic format (single note phrases) only deals with
	 * monophonic music.
	 * 
	 * @param part
	 * @param quan
	 * @param sco
	 * @return
	 */
	public MTransformat conv(Part part, double quan, double sco) {
		try {
		MTransformat mt = new MTransformat();
		mt.setQuantise(quan);
		mt.setScope(sco);
		Mod.quickSort(part);

		// don't operate on empty phrases
		if (part.length() <= 0 || part.getPhrase(0).getStartTime() >= sco)
			return mt;

		double phase = 0;
		// find the first phase shift from the start
		if (part.getPhrase(0).getStartTime() < sco / 2) {
			phase = part.getPhrase(0).getStartTime();	
		}else {
			phase = part.getPhrase(0).getStartTime() - sco;
		}
		// set it
		mt.setPhase(0, phase);
		
		double pno = phase;
		double cno = phase;
		
		int i=0;
		
		for (; i +1 < part.size(); i++) {
			//if(i+1 >= part.size()) /// totally not needed! DUH!
			//	break;
			
			// update the previous note onset for this next bit
			pno = cno;
			cno = part.getPhrase(i+1).getStartTime();
			
			if(cno >= sco)  {
				// this means it is the last note in the loop, 
				// which is dealt with outside of the loop
				break;
			} 
			
			if (cno == pno) {
				// add polyphonic note
				mt.setPol((pno-phase)%sco,part.getPhrase(i));
			} else {	
				// add a new rate value, from previous note to this one
				// the location is at the previous note, ofset by phase and 
				// torused
				mt.setAllNote((pno-phase)%sco, cno - pno, part.getPhrase(i).getNote(0));
				
			}
		}
		
		// find the interval between the last note(i) and the first note (0)
		// in the loop
		pno = part.getPhrase(i).getStartTime();
		cno = part.getPhrase(0).getStartTime() + sco;
		
		mt.setAllNote((pno - phase)%sco, cno-pno, part.getPhrase(i).getNote(0));
		
		return mt;
		} catch(Exception e) {
			PO.p(" part that cause the error: ", part, 2);
			
			e.printStackTrace();
			
		}
		
		return null;
		
	}

	
	/**
	 * don't really need this method foro the purposes of morphing.
	 * @param mt
	 * @param quan
	 * @param scope
	 * @return
	 */
	public Part deconv(MTransformat mt, double quan, double scope) {
		return deconv(mt, quan, scope, new Part());
	}
	
	public Part deconv(MTransformat mt, double quan, double scope, Part p) {
		p.empty();
		//while
		//	make a note phrase at the current position 
		//	
		//  set the properties of the note to the amounts in the break point
		//  envelope
		//
		//	use rate to determine the next (current position + phase + scope) % scope
		// 
		//	if it is beyond the 
		
		
		
		return p;
	}
	
	
	
}
