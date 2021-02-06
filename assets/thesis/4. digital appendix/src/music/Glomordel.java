/*
 * Created on Mar 9, 2006
 *
 * @author Rene Wooller
 */
package music;

import gui.musicArea.MorphComponent;
import gui.musicArea.MusicArea;

import java.awt.Component;
import java.awt.Point;

import jm.music.data.Score;
import jmms.TickEvent;

import org.w3c.dom.Element;

import ren.util.PO;
import ren.util.RMath;

public class Glomordel extends MusicGenerator {

	private MusicArea ma;
	
	private MorphComponent  [] bamomugarr;
	private int bamonum = 0;
	
	// current index of bamomugarr
	private int cbam = 0;
	
	public Glomordel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Glomordel construct(MusicArea ma) {
		this.ma = ma;
		super.lscore = new LScore();
		return this;
	}

	public void select() {
		Component [] coms = ma.getComponents();
		bamomugarr = new MorphComponent [coms.length];
		for(int i=0; i< coms.length; i++) {
			if(coms[i] instanceof MorphComponent) {
				
				bamomugarr[bamonum] = ((MorphComponent)coms[i]);
				bamomugarr[bamonum].getMMG().select();
				bamomugarr[bamonum++].getMMG().getMorphTracker().setUpdating(false);
			}
			
		}
		
	}
	
	public Score generateTickScore(TickEvent e) {
		// will update the tempo in the lpart in the morph
		Score tor = this.bamomugarr[this.cbam].getMMG().tickScore(e);
		
		// change the tempo to be = to the updated one
		this.lscore.getTempoParam().setValue(
				this.bamomugarr[this.cbam].getMMG().getLScore().getTempoParam().getValue());
		
		return tor;
		
	}
	
	public void deselect() {
		for(int i=0; i<bamonum; i++) {
			bamomugarr[i].getMMG().getMorphTracker().setUpdating(true);
		}
		bamonum = 0;
	}

	public void dload(Element e) {
		// TODO Auto-generated method stub

	}

	public void dsave(Element e) {
		// TODO Auto-generated method stub

	}

	public void setMorphIndexes(int x, int y) {
		if(cbam == -1)
			return;
		
		// index of closest
		int ci = -1;
		
		// value of closest
		double cv = Double.MAX_VALUE;
		double cadj = Double.MAX_VALUE;
		
		// for current one (incase it doesn't change)
		double cvbam = Double.MAX_VALUE;
		double cvbamAdj = Double.MAX_VALUE;
		
		//PO.p(" x = " + x  + " y = " + y);
		
		// find the current closest morph
		for(int i=0; i< bamonum; i++) {
			Point fp = 
				((PatternMusicGenerator)bamomugarr[i].getMMG().getFrom()).getPointID();
			
			
			// unitVector to
			double [] uvt = RMath.toUnitVec(
								((Point)(((PatternMusicGenerator)(bamomugarr[i].getMMG().getTo())).getPointID().clone())), 
								fp);
			
			
			Point curp = new Point(x, y);
			//PO.p("curp 1 = " + curp.toString());
			
			// get unit vector at and centralise curp to fp
			double [] uva = RMath.toUnitVec(curp, fp);
			
			
		//	PO.p("curp 2 = " + curp.toString());
			double normfac = (RMath.norm(uva[0], uva[1])*
				   	 RMath.norm(uvt[0], uvt[1]));
	//		PO.p("norm fac should = 1.0 " + normfac);
			
			// find the angle
			double ang = Math.acos(
								   	RMath.dot(uvt, uva)/normfac
								   	
								   );
		//	PO.p("ang = " + ang);
			
			
			//find the opposite (distance to line) (op/hyp) = sin(a)
			double hyp = RMath.norm(curp);
			double opp = Math.sin(ang)*hyp;
			
			
			//PO.p("for " + i + " fp = " + fp.toString() + " curp = " + curp.toString() +
			//		"\nuvt = <" + uvt[0] + ", " + uvt[1] +
			//		"\n uva = <" + uva[0] + ", " + uva[1] + "\nhyp = " + hyp +
			//		" ang = " + ang + " opp = "  + opp);
			if(opp < cv) {
				cv = opp;
				ci = i;
				cadj = opp/Math.tan(ang);
			}
			
			// update the current
			if(i == this.cbam) {
				cvbam = opp;
				cvbamAdj = opp/Math.tan(ang);
			}
			
		}
		
	//	PO.p("cv = " + cv + " cvbam - 100 " + (cvbam-100));
		if(ci == cbam) {
		} else if(cv < cvbam) {
			cbam = ci;
			
		} else {
			cv = cvbam;
			cadj = cvbamAdj;
			ci = cbam;
		}
		
//	  change the morph position
		Point top = ((PatternMusicGenerator)this.bamomugarr[cbam].
				getMMG().getTo()).getPointID();
		Point frop = ((PatternMusicGenerator)this.bamomugarr[cbam].
				getMMG().getFrom()).getPointID();
		
		this.bamomugarr[cbam].getMMG().getMorphTracker().
			setPos(Math.min(Math.max(cadj/RMath.distance(frop.x, frop.y, top.x, top.y), 0), 1.0));
				
		this.bamomugarr[cbam].getMTC().repaint();
	//	PO.p("cv = " + cv + " ci = " + ci + " value = " + cadj);
	
		
	}

}
