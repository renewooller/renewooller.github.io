/*
 * Created on Jun 3, 2006
 *
 * @author Rene Wooller
 */
package lplay;

import music.MusicGenerator;

public interface CueManager {

	public void mgCue(MusicGenerator mg);
	public void removeCue();
	public LPlayer getLPlayer();
	public void trackerEndCue(MusicGenerator current, MusicGenerator next);
}
