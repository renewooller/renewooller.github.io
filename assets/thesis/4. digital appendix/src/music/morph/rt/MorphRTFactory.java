/*
 * Created on 12/01/2005
 *
 * @author Rene Wooller
 */
package music.morph.rt;

import music.morph.InterpolateParams;
import music.morph.ParamMorpher;
import music.morph.transearch.TraseMorph;
import ren.util.PO;

/**
 * @author wooller
 * 
 * 12/01/2005
 * 
 * Copyright JEDI/Rene Wooller
 *  
 */
public class MorphRTFactory {

	/**
	 *  
	 */
	public MorphRTFactory() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final MorpherRT [] strucMorphers = new MorpherRT [] { 
        new MarkovMorph2RT(), 
        new WeightedMorphRT(),
        new CrossFadeRT(),
        new PriorityMorph(),
        new MTransMorph(),
        new TraseMorph()};

    private static final ParamMorpher [] paramMorphers = new ParamMorpher [] { new InterpolateParams()};
	
	public static MorpherRT createStruc(String type) {
		for (int i = 0; i < strucMorphers.length; i++) {
			if (strucMorphers[i].getType().equals(type)) {
				try {
					return (MorpherRT) (strucMorphers[i].getClass()).newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static ParamMorpher createParam (String type) {
		for (int i = 0; i < paramMorphers.length; i++) {
			if (paramMorphers[i].getType().equals(type)) {
				try {
					return (ParamMorpher) (paramMorphers[i].getClass()).newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static MorpherRT[] createAllStruc() {
		MorpherRT[] toRet = new MorpherRT[strucMorphers.length];
		int i=0;
		try {
			
			for (i = 0; i < toRet.length; i++) {
				toRet[i] = (MorpherRT) (strucMorphers[i].getClass()).newInstance();
			
			}
		} catch (Exception e) {
			PO.p(" the one is " + strucMorphers[i].getType());
			e.getCause().printStackTrace();
		}
		return toRet;
	}

	public static ParamMorpher [] createAllParam() {
		ParamMorpher[] toRet = new ParamMorpher[paramMorphers.length];
		try {
			for (int i = 0; i < toRet.length; i++) {
				toRet[i] = (ParamMorpher) (paramMorphers[i].getClass()).newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toRet;
	}
	
	public static String[] getStrucTypes() {
		String[] toRet = new String[strucMorphers.length];
		for (int i = 0; i < strucMorphers.length; i++) {
			toRet[i] = strucMorphers[i].getType();
		}
		return toRet;
	}
	
	public static String[] getParamTypes() {
		String[] toRet = new String[paramMorphers.length];
		for (int i = 0; i < paramMorphers.length; i++) {
			toRet[i] = paramMorphers[i].getType();
		}
		return toRet;
	}
	
}