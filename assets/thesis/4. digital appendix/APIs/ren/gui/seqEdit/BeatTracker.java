// rene wooller
package ren.gui.seqEdit;

import lplay.LPlayer;
import ren.gui.ParameterMap;
import ren.util.RMath;

public class BeatTracker {

	private ParameterMap scope;

    //deprecated
	private double res;

	private int bv;
    
    private BeatListener[] beatListeners = new BeatListener[256];

    private int blcount = 0;

    private LPlayer lplayer;
    
    /**
     * Increments by one resolution, and returns the resolution step that it was
     * previously at
     */
    double toRet;

    private boolean userMod = false;

    private int stbv = -1;
    
	public BeatTracker() {
	}

	public BeatTracker contruct() {
		return construct(new ParameterMap().construct(4, 16, 8, "scope"));
	}

	public BeatTracker construct(ParameterMap scope) {
		return construct(scope, 0.25);
	}

	public BeatTracker construct(ParameterMap scope, double res) {
		if (scope != null)
			this.scope = scope;
		this.res = res;
		return this;
	}

	public void setBeat(double beat) {
		/*
		 * if(beat > scope.getValue()){ Error e = new Error("beat above scope");
		 * e.fillInStackTrace();try{throw e; }catch(Error
		 * er){er.printStackTrace();}}
		 */
		beat = RMath.absMod(beat, scope.getValue());

        if(lplayer != null)
            res = lplayer.res();
		//set it
		bv = (int) (beat / res);

        fireBeatListeners(bv*res);
        
	//	System.out.println("bt set bt = " + beat);

	}

	public double at() {
        if(lplayer != null)
            res =  lplayer.res();
       
        return bv * res;
	}

	public double next() {
        if(lplayer != null)
            res = lplayer.res();
        
		return ((bv + 1) % (int) (scope.getValue() / res)) * res;
	}

	public double prev() {
        if(lplayer != null)
            res = lplayer.res();
        
		return RMath.absMod((bv - 1), (int) (scope.getValue() / res)) * res;
	}

	/*
	 * without applying the modulus
	 */
	public double nextNoMod() {
        if(lplayer != null)
            res = lplayer.res();
        
		return (bv + 1) * res;
	}

	

	public double increment() {
        if(lplayer != null)
            res = lplayer.res();
        

		toRet = bv * res;
		fireBeatListeners(toRet);

		bv++;
		bv = bv % (int) (scope.getValue() / res);
		return toRet;
	}

	public double decrement() {
        if(lplayer != null)
            res = lplayer.res();
        
        
		toRet = bv * res;
		bv--;
		bv = RMath.absMod(bv, (int) (scope.getValue() / res));
		return toRet;
	}

	public void setScope(ParameterMap scope) {
		this.scope = scope;
	}

    /**
     * this method is deprecated... useing setLPlayer now
     * to support automatic resolution changes
     * @param res
     */
	public void setRes(double res) {
        if(lplayer != null) {
            System.out.println("cannot set the resolution in beat tracker - happening automatically through lplayer");
        }
		this.res = res;
	}

	public void addBeatListener(BeatListener toAdd) {
		beatListeners[blcount++] = toAdd;
	}

	private void fireBeatListeners(double beat) {
		for (int i = 0; i < blcount; i++) {
			beatListeners[i].beatFired(beat);
		}
	}

    public void setLPlayer(LPlayer player) {
        this.lplayer = player;
    }

    public void userMod(boolean b) {
        this.userMod = b;
        if(b = true)
            stbv = this.bv;
         else
            stbv = -1;
        
        lplayer.userMod(b);
    }

    public void userModBeat(double beat) {
        
        beat = RMath.absMod(beat, scope.getValue());

        if(lplayer != null)
            res = lplayer.res();
        //set it
        bv = (int) (beat / res);

        lplayer.userModBeat((bv-stbv)*res);
        
        fireBeatListeners(bv*res);
    }

}

