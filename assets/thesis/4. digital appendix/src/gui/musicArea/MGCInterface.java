package gui.musicArea;
//import lplay.LPlayer;
import jmms.TickListener;
import music.MusicGenerator;

/**
 
 */

public interface MGCInterface extends TickListener {

	public MusicGenerator getMusicGenerator();
	// so that morph can grab the music generators that are in this
	 
	
}