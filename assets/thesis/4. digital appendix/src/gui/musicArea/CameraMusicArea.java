/*
 * Created on May 24, 2006
 *
 * @author Rene Wooller
 */
package gui.musicArea;

import java.awt.event.MouseEvent;

import music.Glomordel;
import music.MusicGenerator;
import ren.tuio.TuioClient;
import ren.util.PO;

public class CameraMusicArea extends MusicArea {

	protected TuioClient tc = new TuioClient();
	private Glomor glomor;
	private Glomordel gmod;
	private boolean cameraIn = false; // keeps track of wether we are in camera mode
	private MusicGenerator curmugenBeforeCamera;
	
	public CameraMusicArea() {
		super();
		
		gmod = (new Glomordel()).construct(this);
		glomor = (new Glomor()).construct(gmod, this);
	
		//tc.addTuioListener(glomor);
	}

	public boolean isCameraIn() {
		return cameraIn;
	}

	public void clickNoCreate(MGComponent mgc, MouseEvent e) {
		if(!isCameraIn()) {
			super.clickNoCreate(mgc, e);
		}
	}

	/**
	 * enables the use of a single fiducial to control the global morph position
	 * it will snap to each 2-source morph
	 * 
	 * @param state
	 */
	public void enableCameraInput(boolean state) {
		if(state) {
			this.gmod.select();
			this.add(glomor);
			glomor.repaint();
			tc.addTuioListener(glomor);
			tc.connect();
			this.curmugenBeforeCamera = this.lplay.getCurrentMusicGenerator();
			this.lplay.setCurrentMusicGenerator(gmod);
			PO.p("camera on");
			this.cameraIn = true;
		} else {
			this.gmod.deselect();
			this.remove(glomor);
			tc.disconnect();
			if(this.curmugenBeforeCamera != null)
				this.lplay.setCurrentMusicGenerator(this.curmugenBeforeCamera);
			PO.p("camera off");
			this.cameraIn = false;
		}
		this.repaint();
	}
	
	
	
}
