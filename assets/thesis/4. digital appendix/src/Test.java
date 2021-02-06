import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.tools.Mod;
import music.LPart;
import music.morph.transearch.AddRem;
import music.morph.transearch.Harmonise;
import music.morph.transearch.KeyMoSearch;
import music.morph.transearch.Metric;
import music.morph.transearch.TransSearch;
import music.morph.transearch.TraseMorph;
import ren.tonal.TonalManager;
import ren.util.Gen;
import ren.util.PO;
import ren.util.RMath;
import ai.AllCompare;
import ai.An;
import ai.MTConverter;
import ai.PitchClassCompare;

/*
 * Created on 3/11/2005
 *
 * @author Rene Wooller
 */

public class Test {
	private static Metric met = new Metric();
	private static AllCompare ac = new AllCompare();
	private static MTConverter mtc = new MTConverter();
	
    public static void main(String [] args) {
    	allCompTest();
    	
    	//efficTest();
    	//ttest();
    	//genTest();
    	//testKeyMoSearch();
    	//harmoniseTest();
    	
    	//avhainTest();
    	
    //	stringIntTest();
    	
    //	depScaConvTest();
    	
    //	depaTest();
    	
       // 	testKeyMoSearch();
        	
       // 	pitchClassCompareTest();
        	
        	//difKeyTest();
        	
    	
    	
    	/*
    	int [][] tint = new int [3][];
    	int a = 0;
    	int b = 2;
    	
    	tint[a-b] = new int [b];
    	*/
    	
    	//int a = tint[-2][0];
    	
    	//int [] tint = new int [0];
    	
    	
    	
    	
        //Score s = new Score("A");
    	/*
    	//PO.p(".. " + (-10%10 +10)%10);
    	LPart p1 = lp(new double [] {0.0, 0.125, 0.25, 0.5, 0.75, 1.0, 1.5, 2.0,2.25
				},
				new int []   { 60, 62, 63, 64, 65, 67, 69, 71, 72});
    //	 0, 2, 4, 5, 7, 9, 11
    	LPart p2 = lp(new double [] {0,  0, 0.25, 0.25, 0.5, 0.5, 1.0, 1.0, 3.0, 3.0},
				new int []         { 60, 72, 62, 74,   64, 76,   65, 77,   67, 79});
    	
   
    	
    	
    //	PO.p("rot = " + p1.getTonalManager().getRoot());
    	
    	p1.getScope().setValue(4.0);
    	*/
    	//testKeyDistance();
    	
    //	PO.p(" " + 0*1.0/7*1.0);
    			
    
    	/*
    	p1.convertToDEPA();
    	
    	p2 = p1.copy();
    	Mod.invertRT(p2.getPart(), 0.25, p1.getTonalManager().getDEPsPerOctave(), 0, 127);
    	
    	
    	
    	/*
    	Part cpy = p1.getPart().copy();
    	Mod.transposeRT(cpy, 12);
    	
    	LPart poct = p1.copy();
    	
    	Mod.merge(poct.getPart(), cpy);
    	Mod.quickSort(p1.getPart());
    	*/
    	
    //	double avhain = An.avhain(p1.getPart(), 0.001);
    //	PO.p("avhain of p1 = " + avhain);
    	/*
    	p1.setScale(1);
    	p2.setScale(1);
    	
    //	PO.p("p 1 before ", p1.getPart(), 1);
    //	p1.convertToDEPA();
    //	p2.convertToDEPA();
    	    	
   
    	LPart found = null;
    	Inversions inv = new Inversions();
    	try {
    		double [] glopar = new double [] {0, 0, 1.0};
    		found = inv.find(p1, p2, 0, glopar);
    	} catch(Exception e) {}
    	//PO.p(" p1 DEPA ", p1.getPart(), 1);
    	//p1.getTonalManager().shiftRoot();
    	
    //	Mod.expandFunDEPAintRT(p1, An.fun(p1), 2.0, 12, 0, 1024);
    	//found.convertFromDEPA();
    	
    	p1.convertFromDEPA();
    	p2.convertFromDEPA();
    	found.convertFromDEPA();
    	
   // 	PO.p(" p1     ", p1.getPart(), 1);
    	PO.p(" p2 inv ", p2.getPart(), 1);
    	
    //	PO.p("found   ", found.getPart(), 1);
    	*/
    //	p1.setScale(2);
    	
    //	p1.convertFromDEPA();
    	
    //	PO.p(" p1 in dorian ", p1.getPart(), 1);
    	
    	//p2 = p1.copy();
    	//p2.getScope().setValue(4.0);
    	
    	//p2.setPart(p1.getPart().copy());
    	
    	//Mod.transposeRT(p2.getPart(), 1);
    	
    	//PO.p("dif = " + met.difnn(p1, p2, ac));
    	
    	
    	//p2.getPart().removeLastPhrase();
    //	p2.getPart().removeLastPhrase();
    //	p2.getPart().removeLastPhrase();
    	
    	//PO.p("2nd dif = " + met.difnn(p1, p2, ac));
    	/*
    	p0.getScope().setValue(4.0);
    	//Mod.expandFunIntRT(p2.getPart(), An.fun(p2), 2.0);
    
    //	PO.p("avhaim = " + An.avhain(p1.getPart(), 0.001));
    	
 //   	PO.p("harm clumps = ", An.harmonicClumps(p1.getPart(), 0.001));
    	
    	Harmonise ha = new Harmonise();
    	try {
    		double [] glopar = new double [] {0, 0, 1.0};
    		//glopar[-100];
    		ha.find(p1, p2, 0, glopar);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	*/
    	/*
    	TonalComposite tc = new TonalComposite();
    	tc.addPart(p1.getPart());
    	tc.addPart(p2.getPart());
    	
    	int [] scale = tc.extractScale(tc.pitchesToDegrees(0));
    	
    	PO.p(scale);
    	
    	Scales.getInstance().lockToScale(p0.getPart(), tc, 0, 12, 0, false);
    	
    	PO.p("p0 = ", p0.getPart(), 1); */
    	
    //	LPart temp = p2;
    ///	p2 = p1;
    //	p1 = temp;
    	
    	//PO.p("p1 = ", p1.getPart(), 1);
    //	PO.p("p1 = ", p1.getPart(), 0);
    //	PO.p("p2 = ", p2.getPart(), 1);
    	
    	//PO.p("diffnn = " + met.difnn(p0, p1, ac));
    	
   // 	TonalComposite t1 = new TonalComposite();
  //  	TonalComposite t2 = new TonalComposite();
  //  	t1.addPart(p1.getPart());
  //  	t2.addPart(p2.getPart());
    //	
   // 	PO.p(t1.toString());
   // 	PO.p(t2.toString());
    	
    	
   //	MTransformat m0 = mtc.conv(p0);
  //  	MTransformat m2 = mtc.conv(p2);
    
  // 	PO.p("dif = " + met.difioc(m0, m2));
    //	PitchStretch ps = new PitchStretch();
   // 	PO.p("p = " + ps.find(p1, p2).getPart());
        	
    	//p0 = p2.copy();
    	
    	//PO.p("no dif = " + met.difnn(p2, p0, ac));
    	
    //	TraseChain trac = new TraseChain();
    	
    //	LPart [] steps = trac.transformComplete(p1, p2);
    	
    	//PO.p(steps[1].getPart().toString());
    	//PO.p(steps[2].getPart().toString());
    	
    	
    	/*
    	for(int i=0; i< steps.length; i++) {
    		Mod.quickSort(steps[i].getPart());
    		PO.p("step " + i + " = ", 
    			 steps[i].getPart(), 0);
    	}*/
    	
    	
    	//Rate tsr = new Rate();
    //	PO.p("resulting part = ", tsr.find(p0, p2).getPart(), 1);
    	    	
    	
    	
    //	PO.p("p1 = ", p1.getPart(), 0);
    	//Mod.invertRT(p2.getPart(), 1.0, 12);
    	//Mod.transposeRT(p2.getPart(), -10);
    	//PO.p("p2 = " + p2.getPart());
    //	PO.p("p2 = ", p2.getPart(), 0);
    //	String scaleType = "pentatonic";
    	
    //	TonalComposite tc = new TonalComposite();
    //	tc.addPart(p2.getPart());
    	
  //  	Scales.getInstance().lockToScale(p1.getPart(), scaleType, 
  //  			p1.getTonalManager().getRoot(), p1.getTonalManager().getStepsPerOctave(), 
  //  			1, new DefaultNoteWeighter(), true, 
  //  			new double [][] {tc.getPCWeights()}, new double [] {1.0});
    	
   // 	PO.p("p1 locked to " + scaleType + " = ", p1.getPart(), 0);
    //	PO.p(" type        " + scaleType + " = \n ", Scales.getInstance().getScale(scaleType));
    	
    	// keep track of options and distances from current and choices made
    	// check new choices against old choices.  
    	// implement zero choices and make the other choices the smallest other
    	
    	
    	
    //	TonalComposite tc = new TonalComposite();
    //	tc.addPart(p1.getPart());
    //	PO.p("weights = ", tc.getPCWeights());
    	
    //	Scales.getInstance().lockToScale(p1.getPart(),
    //									 Scales.dorian, 
    //									 0, tc.getPCWeights(), 12);
    //	PO.p("locked p1 = ", p1.getPart(), 0);
    	//TonalComposite p1tc = new TonalComposite();
    	//p1tc.addPart(p1.getPart());
    	//PO.p("diff = " + met.diftc(p2.getPart(), p1tc));
    	
    	//Mod.transpose(p2.getPart(), 9);
    //	PO.p("fun = " + An.fun(p2));
    	
   // 	Mod.phaseShiftRT(p1.getPart(), 2.0, 4.0);
    	
    	//p2 = p1.copy();
    	//p2.getScope().setValue(p1.getScope().getValue());
    	
    	//PO.p("p1 = " + p1.getPart());
    //	PO.p("p2 = " + p2.getPart());
    //	Mod.expandFunIntRT(p2.getPart(), 60, 2.0);
    //	PO.p("scaled = " + p2.getPart());
    	
    //	
    	
    
    	
    	/*
    	DivMerge dm = new DivMerge();
    //	PO.p(" p = " + dm.find(p1, p2).getPart());
    	
    	Phase pha = new Phase();
    	//PO.p(" p = " + pha.find(p1, p2).getPart());
    	
    	PitchStretch ps = new PitchStretch();
    //	PO.p("p = " + ps.find(p1, p2).getPart());
    	
    	Inversions inv = new Inversions();
    //	PO.p(" found p = " + inv.find(p1, p2).getPart());
    	
    	Octave oct = new Octave();
    //	PO.p(" found p = " + oct.find(p1, p2).getPart());
    	
    	Transpose trp = new Transpose();
    	//PO.p(" found trp = ",  trp.find(p1, p2).getPart(), 0);
    	
    	ModeLock mlo = new ModeLock();
//    	PO.p(" found mode = ",  mlo.find(p1, p2).getPart(), 0);
    	
    	AddRem adr = new AddRem();
    	//Part np = 
    	//Mod.quickSort(np);
    	*/
    	
    	
    //	while(met.difnn(p1, p2, ac) > 0) {
    //		p1 = adr.find(p1, p2);
    //		Mod.quickSort(p1.getPart());	
    		//PO.p("p2 = ", p2.getPart(), 0);
    //		PO.p(" found add rem = ",  p1.getPart(), 1);
    		//PO.p("diff = " + met.difnn(p1, p2, ac));
    //	}
    	
    //	double m = met.nonor(p1, p2);
    	
    	
    //	PO.p("m r = " + m);
    //	PO.p("m n= " + met.nono(p1, p2));
    	    	
    //	MTConverter conv = new MTConverter();
   // 	PO.p("1");
    //	MTransformat mt1 = conv.conv(p1.getPart(), 
    //					p1.getQuantise().getValue(), 
    //					p1.getScope().getValue());
    	
    	//PO.p("mean 1 = " + mt1.computeMean(MTransformat.PIT));
    	//	PO.p("2");
    	/*
    	MTransformat mt2 = conv.conv(p2.getPart(), 
				p2.getQuantise().getValue(), 
				p2.getScope().getValue());
    	
    	PO.p("difioc = " + met.difioc(mt1, mt2));
    	PO.p("difpic = " + met.difpic(mt1, mt2));
    	
    	PO.p("av pitch = " + met.difpiav(p1, p2));
    	PO.p("av io = " + met.difioav(p1, p2));
    	
    	AllCompare ac = new AllCompare(); // considering just st and pitch
    	
    	PO.p("difnn = " + met.difnn(p1, p2, ac));
    	
    	TonalComposite p2tc = new TonalComposite();
    	p2tc.addPart(p2.getPartBounded());
    	
    	PO.p("difftc = " + met.diftc(p1.getPartBounded(), p2tc));
    	
    	*/
    	
    	//PO.p("nn)
    	
    	/*
    	Phrase phr = new Phrase();
        phr.setTitle("a");
        phr.addNote(new Note(60, 0.25, 100));
        phr.addNote(new Note(67, 1.75, 100));
        phr.addNote(new Note(68, 0.25, 100));
        phr.addNote(new Note(67, 0.25, 100));
        phr.addNote(new Note(65, 0.5, 100));
        phr.addNote(new Note(63, 0.5, 100));
        phr.addNote(new Note(59, 0.5, 100));
        Part p = new Part("A");
        p.addPhrase(phr);
        View.notate(p);*/
        
    	System.exit(0);
    	
    }
    
    private static void allCompTest() {
		Note n = new Note(60, 1.0);
		Note n2 = new Note(61, 1.0);
		PO.p("ac = " + ac.comp(new Phrase(n), new Phrase(n2), 4.0));
	}

	private static void genTest() {
	//	Part np = Gen.npart(10, 0, 4, 30, 60);
   // 	PO.p("np = ", np, 1);
	}

	private static void avhainTest() {
    	LPart m = new LPart();
    	m.construct(Gen.npart(new double [] {0.0, 0.0, 1.0, 1.0, 2.0, 2.0}, 
	            new int [] {60, 64, 60, 67, 60, 71}));
    	m.setScale(1);
    	m.convertToDEPA();
    	PO.p("m = ", m.getPart(), 1);
    	
    	LPart t = new LPart();
    	t.construct(Gen.npart(new double [] {0.0, 1.0, 2.0}, 
	            new int [] {60, 60, 60}));
    	
    	//PO.p("t =)
    	
    	PO.p("avhain m = " + An.avhain(m.getPart(), 0.001));
	}

	private static void harmoniseTest() {
		Harmonise h = new Harmonise();
		
    	LPart m = new LPart();
    	m.construct(Gen.npart(
    			new double [] {0.0, 0.0, 
    						   1.0, 1.0, 
    						   2.0, 2.0,
    						   3.0, 3.0, 
    						   4.0, 4.0, 
    						   5.0, 5.0,
    						   6.0}, 
	            new int [] {60, 64, 
    					    62, 67, 
    					    60, 67, 
    					    60, 69, 
    					    60, 71,
    					    60, 72,
    					    60}));
    	    	
    	m.setScale("ionian");
    	m.convertToDEPA();
    //	PO.p("m = ", m.getPart(), 1);
   // 	PO.p("passing steps = " + m.getTonalManager().getPassingSteps());
    	
    	LPart t = new LPart();
    	t.construct(Gen.npart(new double [] {0.0, 1.0, 2.0}, 
	            new int [] {60, 60, 60}));
    	
    //	int [][] mh = An.harmonicClumps(m.getPart(), 0.001);
    	//PO.p("mh = ", mh);
    	//PO.p("selfDif t = " + h.differenceFunction(t, t));
    	//PO.p("selfDif f = " + h.differenceFunction(m, m));
    	
    	
    	try {
    		LPart out = h.find(m, t, 1, new double [] {0, 1.0, 1.0, 2.0});
    		PO.p("out = ", out.getPart(), 1);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
   // 	PO.p("m abs  = " + m.getTonalManager().getABS(4) +
   // 			"  " + m.getTonalManager().getABS(8) +
   //			"  " + m.getTonalManager().getABS(12));
    	
	}

	private static void depScaConvTest() {
		int frost = 2;
		int frol = 7;
		int tost = 3;
		int toll = 7;
	//	int scal = 8; // harmonic minor
		
		TonalManager tm = new TonalManager();
		
		int [] baa = new int [3];
		for(int i=0; i< 14; i++) {
			baa[0] = i;
			baa[1] =  tm.convertDEPA(i, frost, frol, tost, toll);
			baa[2] =  tm.convertDEPA(baa[1], tost, toll, frost, frol);
			PO.p("bef = " + i + " aft = " + baa[1] + " and back = " + baa[2]);
		}
		
	}

	private static void depaTest() {
		Part p = npart(new double[] {0.0, 1.0, 2.0, 3.0},
				new int [] {62, 64, 71, 73});
		LPart lp = (new LPart()).construct(p);
		
		lp.getTonalManager().setRoot(2);
		
		lp.getTonalManager().setScale(1);
		
		lp.convertToDEPA();
		
		PO.p("converted = ", lp.getPart(),1);
		lp.convertFromDEPA();
		
		PO.p("back = ", lp.getPart(), 1);
		
		// in c 
		p = npart(new double[] {0.0, 1.0, 2.0, 3.0},
				new int [] {60, 62, 69, 71});
		lp = (new LPart()).construct(p);
		
		lp.getTonalManager().setRoot(0);
		
		lp.getTonalManager().setScale(1);
		
		lp.convertToDEPA();
		
		PO.p("c:   converted = ", lp.getPart(),1);
		lp.convertFromDEPA();
		PO.p("c:   back = ", lp.getPart(), 1);
		
	}

	private static void pitchClassCompareTest() {
		PitchClassCompare pcc = new PitchClassCompare();
		
		pcc.getCof().setValue(0.0);
		pcc.getLin().setValue(1.0);
		pcc.getOctEqu().setValue(0.0);
		
		for(int i=48; i<72; i++) {
			for(int j=48; j<72; j++) {
				PO.p("a = " + i + " b = " + j + 
				     " similarity = " + pcc.compare(i, j));
			}
		}
    	
		
    	
	}

	private static void difKeyTest() {
    	TonalManager t = new TonalManager();
    	t.setScale(1);
    	
    	TonalManager u = new TonalManager();
    	u.setScale(6);
    	u.setRoot(3);
    	
    	PO.p("dif t u = " + met.difKey(u, t));
    	
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
    
    public static LPart lp(double [] st) {
    	return  (new LPart()).construct(npart(st));
    	
    }
    
    public static LPart lp(double [] st, int [] pi) {
    	LPart ret =  (new LPart()).construct(npart(st, pi));
    	ret.getScope().setValue(4.0);
    	ret.getTonalManager().setScale(1);
    	return ret;
    }
    
    
    private static void testKeyDistance() {

    	// test the different keys distances
    	TonalManager a = new TonalManager();
    	a.setScale(1);
    	TonalManager b = new TonalManager();
    	b.setScale(1);
    	
    	for(int i=0; i< 12; i++) {
    		a.setRoot(i);
    		for(int j = 1; j <= 10; j++) {
    			a.setScale(j);
    			PO.p(" a root = " + a.getRoot() + " a scale = " + 
    					a.getScaleName() + " dif = " + met.difKey(a, b));
    			
    		}
    	}
    }
    
    private static void testKeyMoSearch() {
    	
    	KeyMoSearch kemos = new KeyMoSearch();
    	kemos.getSameLength().setValue(0);
    	
    	kemos.getTransformSpeed().setValue(0.25);
    	
    	kemos.getRootd().setValue(1.0);
    	kemos.getPitchClassCompare().getCof().setValue(1.0); // for root
    	kemos.getPitchClassCompare().getOctEqu().setValue(1.0);
    	
    	kemos.getKeyd().setValue(0.0);
    	
    	kemos.getScaled().setValue(1.0);
    	
    	kemos.getTrackVsConsistence().setValue(0.3);
    	
/*
		LPart mix1 = new LPart();
		TonalManager mit = new TonalManager();
		mit.setRoot(1);
		mit.setScale(5);
		mix1.setTonalManager(mit);
		
		TonalManager mit2 = new TonalManager();
		mit2 = mit.copy();
		
		LPart mix2 = new LPart();
		mix2.setTonalManager(mit2);
		
		PO.p("dif = " + kemos.differenceFunction(mix1, mix2));
    	*/
    	
    	TonalManager a = new TonalManager();
    	a.setScale(1);
    	TonalManager b = new TonalManager();
    	b.setScale(1);
    	b.setRoot(6);
    
		TonalManager [] tetoma = new TonalManager [10*12];
		int i=0;
		tetoma [i++] = a.copy();
		
		TonalManager toto = b.copy();
		LPart tef = new LPart();
		tef.setTonalManager(tetoma[0]);
		LPart teto = new LPart();
		teto.setTonalManager(toto);
		double dif = kemos.differenceFunction(tef, teto);// met.difKey(tetoma[0], toto);
		//PO.p("first dif = " + dif);
		
		
	
		int stepAt = 0;
		while (dif > 0) {
			//PO.p(" whiling " + i);
			if(i >= tetoma.length)
				break;
		
			tetoma [i] = kemos.find(tetoma[i-1], toto, stepAt++, null).getTonalManager();
			
			tef.setTonalManager(tetoma[i]);
			dif = kemos.differenceFunction(tef, teto);//met.difKey(tetoma[i], toto);
			
			i++;
		
		}
		
		
		LPart tefm1 = new LPart();
		for(int j = 0; j<i; j++) {
			tef.setTonalManager(tetoma[j]);
			
			PO.p("tetoma [" + j + "] = " + tetoma[j].getRoot() + " scale = " + tetoma[j].getScaleName());
			PO.p("scale = ", RMath.sumod(tetoma[j].getScale(), tetoma[j].getRoot(), 12));
			if(j>0) {
				tefm1.setTonalManager(tetoma[j-1]);
				PO.p("dif to prev " + kemos.differenceFunction(tef, tefm1) +
					 "dif to final " + kemos.differenceFunction(tef, teto));
			}
		}
		
	//	PO.p("dif to prev " + met.difKey(tetoma[j], tetoma[j]) +
	//			 "dif to final " + met.difKey(tetoma[j], toto));
		
	}
    
    private static void stringIntTest() {
    	int i = 11573;
    	PO.p("int = " + i);
    	String s = String.valueOf(i);
    	PO.p("String = " + s);
    	String s2 = Integer.toString(i);
    	PO.p("Integer.toString = " + s2);
    	
    	int i2 = Integer.parseInt(s);
    	PO.p("i2 = " + i2);
    	//String.
    	
    }
    
    private static void efficTest() {
    	TraseMorph tm = (new TraseMorph());
    	LPart src = (new LPart()).construct(new Part());
    	src.getScope().setValue(4.0);
    	Mod.quickSort(src.getPart());
    	
    	LPart trg = (new LPart()).construct(new Part());
    	trg.getScope().setValue(4.0);
    	Mod.quickSort(trg.getPart());
    	
    	src.getTonalManager().setScale(1); 
    	trg.getTonalManager().setScale(1);
    	tm.getTraseChain().getFrameLimit().setValue(100);
    	tm.getTraseChain().getGlobalTransformSpeed().setValue(2.0);
    	tm.getTraseChain().getSimCutOff().setValue(0.0);
    	tm.getTraseChain().getMutationLimit().setValue(8);
    	// set it to be monophonic
    	tm.getTraseChain().getChain()[tm.getTraseChain().getChain().length-1]
    	                              .getCostParams()[AddRem.POL].setValue(0.0);
    	//tm.getTraseChain().get
    	TransSearch [] tsa = tm.getTraseChain().getChain();
    	// just do Add/Remove
    	for(int i=0; i< tsa.length; i++) {
    		if(! (tsa[i] instanceof AddRem)) {
    	//		tsa[i].setBypass(true);
    			tsa[i].config(1);
    		} else {
    			((AddRem)tsa[i]).getRepeats().setValue(2); //1
    		}
    			
    	}
    	
    	int nn = 0;
    	int stnn = 1;//2;7. 8 .10. 13, 15 
    	
    	int steps = 1;
    	int maxnn = 16;
    	
    	double st = 0.0;
    	double en = 4.0;
    	int minp = 60;
    	int maxp = 71;
    	double q = 0.25;
    	boolean poly = false;
    	int samp = 50;
    	
    	int [][] dat = new int [samp*maxnn + 10][3];
    	int cnt = 0;
    	for(int i=0; nn<maxnn; i++) {
    		nn = i*steps+stnn;
    		int add =0;
    		int recAdd = 0;
    	//	System.out.println("i " + i + "\nj\n");
    		for(int j=0; j<samp; j++) {
    			
    		//	System.out.print(j + ", ");
    			//PO.p("------------------------- i" + i + " j"+ j + "\n");
    			
    			Gen.npartMono(src.getPart(), nn, st, en, minp-12, maxp-12, q);
    //			PO.p("src ", src.getPart(), 1);
    	    	Gen.npartMono(trg.getPart(), nn, st, en, minp, maxp, q);
    	    	tm.initParts(src, trg, null, 0);
    	    	tm.initTrase();
    	    	//
    	    	dat[cnt][0] = nn;
    	    	dat[cnt][1] = tm.getLength()-2;
    	    	dat[cnt][2] = tm.getTraseChain().getRecurCount();
    	    	
    	    	
    	    	add +=dat[cnt][1];
    	    	recAdd += dat[cnt][2];
    	    	
    	    	System.out.print("\n"+dat[cnt][0] + "," + 
    	    			dat[cnt][1] + "," + 
    	    			dat[cnt][2] + ",");
    	    	
    	    	cnt++;
    	    	
    		}
    		//
    		double mean = (add*1.0/samp*1.0);
    		double meanRec = (recAdd*1.0/samp*1.0);
    		double std = 0;
			// sum all the deviations
			for(int j=cnt-samp; j<cnt; j++) {
				std += Math.pow(dat[j][1]-mean, 2);
			}
			// divide by n-1
			std = std*1.0/(samp-1)*1.0;
			// square root
			std = Math.pow(std, 0.5);
			System.out.print(mean + "," + std + "," + meanRec);
    	//	System.out.print("\n");
    		
    	}
    	
    	
    	System.out.println("st = " + st + ". en = " + en + 
    			". minp = " + minp + ". maxp = " + maxp + ". q = " + q + ". maxnn = " + maxnn);
    	
    	System.out.println("notes,av,std");
    	System.out.print("\n");
    	
    	nn = stnn;
    	int add = 0;
    	int recAdd = 0;
    	// work out standard deviation etc.
    	// cnt +1 so the last one can be computed and break half way through
    	for(int i=0; i< cnt+1; i++) {
    		if(nn != dat[i][0]) { // if there is a change
    			double mean = add*1.0/samp*1.0;
    			double recMean = recAdd*1.0/samp*1.0;
    			double std = 0;
    			// sum all the deviations
    			for(int j=i-samp; j<i; j++) {
    				std += Math.pow(dat[j][1]-mean, 2);
    			}
    			// divide by n-1
    			std = std*1.0/(samp-1)*1.0;
    			// square root
    			std = Math.pow(std, 0.5);

    			System.out.print(nn + "," + mean + "," + std + "," + recMean +"\n");
    			add = 0;
    			recAdd = 0;
    		}
    		if(i== cnt)
    			break;
    		add += dat[i][1];
    		recAdd += dat[i][2];
    		
    		nn = dat[i][0];
    		//System.out.print(dat[i][0] + "," + dat[i][1] + "\n");
    	}
    	System.out.println("  ");
    	System.out.println("notes,frames");
    	for(int i=0; i< cnt; i++) {
    		
    		System.out.print(dat[i][0] + "," + dat[i][1] + "\n");
    	}
    }
    
    private static void ttest() {
    	//private AllCompare ac = new AllCompare();
    	
    	LPart midp = lp(new double [] {0.0, 0.25, 0.5,1.0,1.5,1.75,2.0,2.25,2.5,3.0,3.5}, 
    		  new int [] {78,77,80,73,74,72,73,74,81,71,81});
    	LPart targ = lp(new double [] {0.0,2.5,1.75,0.25,3.5,2.25,1.0,1.5,2.0,0.5,3.0},
    		  new int [] {78,74,72,77,81,81,73,74,73,80,71});
    	//LPart targ = lp(new double [] {0.0,0.25,0.5,2.5,1.75,,3.5,2.25,1.0,1.5,2.0,,3.0},
      	//	  new int [] {78,77,80,74,72,,81,81,73,74,73,,71});
    	Mod.quickSort(targ.getPart());
    	PO.p("midp", midp.getPart(), 1);
    	PO.p("targ", targ.getPart(), 1);
    	
    	
    	PO.p("midp nn dif = " + met.difnn(midp, targ, ac));
    	for(int i=0; i< midp.getPart().length(); i++) {
    		LPart cpy = midp.copy();
    		cpy.getPart().removePhrase(i);
    		double result = met.difnn(cpy, targ, ac);
    		PO.p("removed " + i + " and got " + result);
    		
    	}
    }

    
}
