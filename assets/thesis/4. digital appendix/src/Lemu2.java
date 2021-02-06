/**
 * Lemu 2 Rene Wooller
 * 
 * todo:
 * 
 * change the button in the part editor so that it can mute 
 * tracks and also select between midi controllers and piano roll
 * and automate controllers
 * 
 * look up sponge fork
 * 
 * drop down menu for channel and instrument
 *  - think about cueing the music generators - interpolate scope
 *  
 * TODO
 * 
 * The problem only occurs when we have two parts.
 * It works fine with one part.  This implies that MorphMusicGenerator is the place with the problem
 * 
 * 
 * 
 * 
 * V  the readout next to the title in LebelledView
 * 
 * V make the green ball invisible when not being used or
 * 
 * V channel changing in patternEditor when new parts are added?
 * 
 * instead of being able to morph to another morph:
 * 
 * make it so that you can grab whats playing in the morph and create a pattern from it (you could
 * make it get rid of the morph and create two, and link them at the current position of the green
 * 
 * or make it do that automatically when you try and morph to a morph
 * 
 * make scope longer
 * 
 * make it so that you can edit velocity
 * 
 * fix slow response of realtime pattern transformers and visual display
 * 
 * For ande
 * saving and loading
 * 
 * midi input into patterns
 * 
 * contact ande foster and arrange a time
 * 
 * Frederic:
 * 
 * consider the manifold pattern created between the different patterns, if the patterns are represented as parameters
 * (vectors). 
 * 
 * hypersurface (harmony contraints) follow a path that follows the constraints.
 * 
 * how do you represent them as paramters (distance is needed)
 * discrete to linear:
 * word processing: edit distance between strings. edit distance is usually deterministic, but with music maybe it 
 * would be cool to define the edit distance within a model that is probabilistic 
 * 
 * create probabilistic models for the two patterns and then merge them (the models) as the morph proceeds.
 * 
 * consider markov chain of higher order also.
 * 
 * morph between contraints - eg, chord structures
 * 
 * maybe see if it goes into a game.  case study?
 * 
 * algorithm EM (expected maximisation) for induction of hidden markov models
 * 
 * TODO: get saving working
 * 
 * 
 */
//preted error

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import jm.music.data.Score;

//import com.thoughtworks.xstream.XStream;

public class Lemu2 {

	private MainFrame mf;
	
	public static void main(String[] args) {
		
		new Lemu2(args);
	
	}

	public Lemu2(String [] args) {
		mf = new MainFrame(this);
		
		//if(args.length >= 1 && args[0].equals("-t")) {
		//	mf.getTableMusicArea().enableTableMode(true);
		//}
	//	mf.doStuff();
	}

}