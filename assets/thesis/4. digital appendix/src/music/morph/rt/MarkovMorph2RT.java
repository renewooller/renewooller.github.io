/*
 * Created on 17/06/2005
 *
 * @author Rene Wooller
 */
package music.morph.rt;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jmms.TickEvent;
import lplay.LPlayer;
import music.LPart;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.music.PhraseLL;
import ren.music.PhraseQ;
import ren.util.PO;
import ren.util.RMath;
import ai.PitchClassCompare;
import ai.RhythmCompare;
import ai.StartTimeCompare;

/**
 * @author wooller
 * 
 * 17/06/2005
 * 
 * Copyright JEDI/Rene Wooller
 * 
 */
public class MarkovMorph2RT extends MorpherRT {

	private boolean VB = false; // verbose output
	
	private double cutOff = 0.01;  // how small before 0
	private int decp = 2;
	
    private WeightedMorphRT weightedMorph;

    private CrossFadeRT crossFadeMorph;

    //private PitchCompare pcom = new PitchCompare();
    private PitchClassCompare pcom = new PitchClassCompare();
    
    
    private RhythmCompare rcom = new RhythmCompare();

    private StartTimeCompare scom = new StartTimeCompare();

    private Part[] toRetMorph;

  //  private PhraseLL loggedPred;

    private double tempst = 0;

    private double gap = 0; // between at and most prev note

    private int morphChannel = 0;
    
    private double largestInterval;
    
    private int streamLostCount;

    private int nullPredictionCount;

 //   private int playLoggedCount;

    private  int count;
    
    private ParameterMap[] params;

    private Part seed;
    
    /**
     * 
     */
    public MarkovMorph2RT() {
        super();
        toRetMorph = new Part[2];
        toRetMorph[0] = new Part();
        toRetMorph[1] = new Part();
    //   empty = new Part();
        weightedMorph = new WeightedMorphRT();
        crossFadeMorph = new CrossFadeRT();
        
        this.defineParams();
        seed = new Part();
    }


    protected void defineParams() {
        int numParams = 6;
        params = new ParameterMap[numParams + scom.getParams().length
                + pcom.getParams().length];
        params[0] = (new ParameterMap()).construct(0, 100, 0.0, 1.0, 1.0,
            "pitch weight");
        params[1] = (new ParameterMap()).construct(0, 100, 0.0, 1.0, 0.0,
            "rhythm weight");
        params[2] = (new ParameterMap()).construct(0, 100, 0.0, 1.0, 0.0,
            "start weight");
        params[3] = (new ParameterMap()).construct(1, 16, 2, "markov depth");
        
        params[4] = (new ParameterMap()).construct(0, 1000, 1.0, 100, 1.0,
        	"distinction");
        
        params[5] = (new ParameterMap()).construct(0, 1000, 0.25, 1.5, 1.0,
    	"anticipation");
        
        //params[6] = this.p
        
        System.arraycopy(scom.getParams(), 0, params, numParams,
            scom.getParams().length);
        System.arraycopy(pcom.getParams(), 0, params, numParams
                + scom.getParams().length, pcom.getParams().length);
    }
    
    transient int streamLossBar = 0;
    transient double simMovAvg = 0;
    transient int nullPredBar = 0;
    /**
     * 
     * takes both parts and the history makes a
     * prediction for the next note at this particular
     * time-step, incorporating it into the root part
     */
    public Part[] morphRT(final LPart[] toFrom, final LPart[] mlparts, double morphIndex, TickEvent e,
            PhraseQ hist) {
    	if(count%16  == 0 && count != 0) {
    		if(VB) {
        	PO.p("stream loss this bar : " +  
        		((this.streamLostCount-this.streamLossBar)*1.0)/16.0 + 
        		" rate: " + (this.streamLostCount*1.0)/(count*1.0)); // +
        	//	" null pred = " + 
        	//	((this.nullPredictionCount-this.nullPredBar)*1.0)/16.0);
    		}
        	streamLossBar = streamLostCount;
        	nullPredBar = this.nullPredictionCount;
    	}
        count++;
        
       
        
        this.morphChannel = toFrom[0].getPart().getIDChannel();
        
        // if one of the ones are empty, fade instead of cutting
        if (toFrom[0].size() == 0 || toFrom[1].size() == 0)
            return crossFadeMorph.morphRT(toFrom, mlparts, morphIndex, e, null);
       
        int selInt = RMath.rndSelect(morphIndex);
        
        // initialise morph parameters
        this.morphInit(toFrom[0], toFrom[1], toRetMorph);

        // prepare the seed 
        prepareSeed(hist, this.params[3].getValueInt(), seed);

        // make the prediction
        PhraseLL tempPred = null;
        if (seed.getSize() > 0 && seed.getPhrase(0) != null &&
                seed.getPhrase(0).getNote(0) != null) {
        	double [] simsm = createSimilarityMatrix(seed, toFrom[selInt]);
        	
        	// make it the right length for print out
        	StringBuffer bat = new StringBuffer(Double.toString(e.at()%64.0));
        	while(bat.length() < 6) {
        		bat.append(' ');
        	}
        	if(VB) {
        		double sumsim = RMath.sum(simsm);
        		
        		simMovAvg = (simMovAvg*(count*1.0-1.0) + sumsim)/count;
        		        		
        		String ts = Double.toString(sumsim);
        		if(ts.length() > 4)
        			ts = ts.substring(0, 4);
        		
        		String ms = Double.toString(simMovAvg);
        		if(ms.length() > 4)
        			ms = ms.substring(0, 4);
        		
        		PO.p("beat " +  bat, simsm, decp, 
        				"total = " + ts +
        			    " m_avg = " + ms + "\n");
        		
        	}
        
            tempPred = predictPhrase(toFrom[selInt], simsm);
        } else {
            return this.fallBack(toFrom[selInt], selInt , e, mlparts, toFrom);
        }

       /// PO.p("seed = " + seed.toString());
        
        // get the gap between where we are at and the most recent note
        gap = (e.at() - seed.getPhrase(seed.size() - 1).getStartTime());
        
        Part [] check = this.checkForLoss(tempPred, toFrom[selInt],
                                          selInt,  e, mlparts, toFrom, gap);
        // check holds the fallback. if it is null, there was a prediction
        if(check != null) {
        	if(VB)
        		PO.p("l" + e.at()%16);
            return check;
        } else {
        	
        }
        
        tempst = tempPred.st().getStartTime() - gap;
        
        // if we are at not at right interval to put it in
        if (tempst >= ((LPlayer) e.getSource()).res() || tempst < 0)   {
        	if(VB)
        		PO.p("  ");
            return toRetMorph;          
        } else {
        	if(VB)
        		PO.p("<<<<");
        }
        
        //otherwise, put it in
        return phrLL2PartArr(tempPred, selInt, toRetMorph);
        
    }
    
    protected PhraseLL predictPhrase(final LPart sel, double[] probs) {
        // System.out.println("in predict Phrase");
        double sum = RMath.sumQuick(probs);
        double rand = Math.random() * sum;
        int selectedPhrase = -1;
        int currProb = 0;
        //thwarting double accuracy problems wherever he goes, acc is very small
        double acc = 0.00000001;
        
        for (int i = 0; i < probs.length; i++) {

            currProb += (int)(probs[i]/acc);
            
          //  PO.p("currentProb = " + currProb*acc);
            if (rand < currProb*acc) {
            	
                selectedPhrase = i;
                break;
            }
        }

        if(VB) {
        System.out.print("sel note       ");
        for(int i=0; i< selectedPhrase; i++) {
        	System.out.print("   " + PO.r(" ",decp));
        }
        System.out.print(" " + (selectedPhrase+1));
        }
     //   
   //     PO.p("selected Phrase = " + selectedPhrase);
     //   PO.p("probs = ", probs);
        PhraseLL rpq = new PhraseLL();

        if (selectedPhrase == -1) {
     //   	PO.p("rand = " + rand);
            
        	
            return rpq;
        }

        // make sure it is at the end of the cluster/group with same start
        int selc = 0;
        while (sel.getPart().getPhrase(selectedPhrase)
            .getStartTime() == sel.getPart().getPhrase((selectedPhrase + 1) % sel.size())
            .getStartTime() && selc < sel.size()) {
            // System.out.print(".1w");
            selectedPhrase = (selectedPhrase + 1) % sel.size();
            selc++;
        }
        // System.out.print("\n");
        boolean foundAll = false;
        int nsp = (selectedPhrase + 1) % sel.size();
        selc = 1;
        while (!foundAll) {
            // System.out.print(".2w");
            Phrase toPredict = sel.getPart().getPhrase(nsp)
                .copy();

            shiftPredictedStartTime(sel, toPredict, selectedPhrase);

            rpq.add(toPredict);

            // move to the next one or stop
            if (sel.getPart().getPhrase(nsp)
                .getStartTime() == sel.getPart().getPhrase((nsp + 1) % sel.size())
                .getStartTime() && selc < sel.getPart().getSize()) {
                nsp = (nsp + 1) % sel.size();
                selc++;
            } else {
                foundAll = true;
            }

        }
        return rpq;

    }

    protected void shiftPredictedStartTime(LPart sel, Phrase toPredict,
            int selectedPhrase) {
        
        double selst = sel.getPart().getPhrase(selectedPhrase).getStartTime() %
                       sel.getScope().getValue();
        
        if (toPredict.getStartTime() < selst) {
            
          //  PO.p("score param = " + sel.getScope().getValue());
         //   PO.p("selected phrase start = " +
         //       sel.getPart().getPhrase(selectedPhrase).getStartTime());
          //  PO.p(" to predict start time = " + toPredict.getStartTime());
            
            toPredict.setStartTime(toPredict.getStartTime()
                    + sel.getScope().getValue()
                    - selst);
            // scopeParams[this.selectedFrom]
        } else
            toPredict.setStartTime(toPredict.getStartTime()- selst);

    }

    
    private Part [] checkForLoss(PhraseLL tempPred, LPart selected, int selInt, 
            TickEvent e, LPart [] mlparts, LPart [] fromTo, double gap) {
        //      check for null pred
        if (tempPred == null || tempPred.st() == null) {
            nullPredictionCount++;
            return this.fallBack(selected, selInt , e, mlparts, fromTo);
        }

     //  PO.p(" gap  = " + gap + "  largest interval = " + this.largestInterval);
        // check for stream loss
        if (gap > this.largestInterval*params[5].getValue()) {
            streamLostCount++;
            return this.fallBack(selected, selInt, e, mlparts, fromTo);
        }
        
        return null;
    }
    
    private Part [] fallBack(LPart selected, int selInt, TickEvent e, 
                            LPart [] mlparts, LPart []fromTo) {
        return this.weightedMorph.getSegment(selected,selInt, e, mlparts, fromTo);
    }
    
    private Part [] phrLL2PartArr(PhraseLL tc, int selInt, Part [] toRetMorph) {
        //      put it in
        tc.resetIter();
        boolean done = false;
        Phrase atp = tc.iter();
        double prst = atp.getStartTime();
        while (!done) {
            // record previous start time
            prst = atp.getStartTime();

            // shift relative to 0
            atp.setStartTime(tempst);

            // put it in
            toRetMorph[selInt].add(atp);
            

            // get the next one
            atp = tc.iter();

            // check to see if we are finished
            if (atp == tc.st())
                done = true;

            // check to see if the starttimes are
            // consistent
            if (atp.getStartTime() != prst && done == false) {
                try {
                    Exception ex = new Exception(
                            "dem predicted clusta should all hav' de same starttime mon\n"
                                    + " prev st = " + prst + " this st = "
                                    + atp.getStartTime());
                    ex.fillInStackTrace();
                    throw ex;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
        return toRetMorph;
    }
    

    protected void morphInit(final LPart from, final LPart to, Part [] toRetMorph) {
        
        toRetMorph[0].empty();
        toRetMorph[1].empty();
        
        from.getPart().copyAttributes(toRetMorph[0]);
        to.getPart().copyAttributes(toRetMorph[1]);
        
        
    }
 
    /**
     *  
     * 
     * 
     * @param seed
     * @param sel
     * @return similarity matrix holding a particular value for each note
     * notes that occur on the same start time are normalised to be the average
     * probability
     */
    protected double[] createSimilarityMatrix(Part seed, final LPart sel) {
        // prepare the selected part
        PhraseLL[] selp = new PhraseLL[sel.size()];
        int sle = groupPhrases(selp, sel.getPart());

        // prepare the seed
        PhraseLL[] seep = new PhraseLL[seed.size()];
        int sde = groupPhrases(seep, seed);
           
        // initialise similarity matrix with depth 1
        double[][] sim = new double[sle][];
        int dep = 1;
        double tst = selp[0].st().getStartTime();
        double tinv = (tst+sel.getScope().getValue()) - selp[sle-1].st().getStartTime();
        this.largestInterval = tinv;
        sim[0] = comparePhraseLL(selp[0], seep[sde - dep], 
				 sel.getScope().getValue());
        
        for (int i = 1; i < sle; i++) {
            tinv = selp[i].st().getStartTime()-tst;
            if(tinv > this.largestInterval)
                this.largestInterval = tinv;
            tst = selp[i].st().getStartTime();
            sim[i] = comparePhraseLL(selp[i], seep[sde - dep], 
            						 sel.getScope().getValue());    
        }
        
        
        
        
        // go through and factor it some more
        for (int i = 0; i < sle; i++) {    
            dep = 2;
            while (dep <= sde) {
     //           PO.p(" mm2, csm, dep = " + dep);
                RMath.mult(sim[i], 
                    RMath.mean(
                        comparePhraseLL(selp[(i - dep + 1 + sle) % sle], 
                                        seep[sde - dep], 
                                        sel.getScope().getValue())));
                dep++;
            }
        }
        
        double[] sims = new double[sel.size()];
        
    //   PO.p("b4 b4 sim = ", sim);
        int simsAt = 0;
        for (int i = 0; i < sim.length; i++) {
        	// find the sum
        	double simsum = 0;
        	// and the highest
        	double highsim = (double)Integer.MIN_VALUE;
        	for(int j=0; j< sim[i].length; j++) {
                simsum += sim[i][j];
                if(sim[i][j] > highsim) {
                	highsim = sim[i][j];
                }
            }
        	
        	// make it so that all the probabilities within the notegroup
        	// add up to the value of the highest similarity in the note group
            for(int j=0; j< sim[i].length; j++) {
            	if(simsum == 0) { // to stop it returning NaN
            		sims[simsAt++] = 0.0;
            	} else {
            		sims[simsAt++] = (sim[i][j]/simsum)*highsim;
            	}
                
               
            }
            
            
        }

        
    //    PO.p("seed = " + seed.toString() + " sel = " + sel.toString());
        
        
        
        // distinguish the similar ones by a certain factor
        double maxSims = 0;
        for(int i=0; i< sims.length; i++) {

        	sims[i] = Math.pow(sims[i], params[4].getValue()); 
        	
        	if(sims[i] > maxSims)
        		maxSims = sims[i];
        }
        
        // normalise them by the biggest one
        for(int i=0; i< sims.length; i++) {
        	sims[i] = sims[i]/maxSims;
        	if(sims[i] < cutOff) 
        		sims[i] = 0;
        }
      
        //TODO use a squashing function instead:
        // 1/1+(10^params[4])^(-(sims[i]-0.5))
        // combined with a straight linear function as well, because the
        // squashing function turns into a flatline, not a linear function.
        
        return sims;

    }
    
    
    /**
     * groups phrases that have the same starttime into slots in the pll array.
     * 
     * @param in where the groups are stored
     * @param source the part which has the phrases to be grouped
     * @return the end point of the array.  all items from here and after remain 
     * null
     */
    private int groupPhrases(PhraseLL [] in, final Part source) {
        int inat = 0;
        for (int i = 0; i < source.size(); i++) {
            in[inat] = new PhraseLL();
            in[inat].add(source.getPhrase(i));
            while (i + 1 < source.size() && source.getPhrase(i + 1)
                .getStartTime() == source.getPhrase(i)
                .getStartTime()) {
                in[inat].add(source.getPhrase(++i));
            }
            inat++;
        }
        return inat;
    }
    
    
    /**
     * options:
     *  each sel phrase value is the result of the mean of comparisons
     *  
     *  each one is the highest -- choose this one because it doesn't dilute value
     *  add them 
     *  - kind of like NN - if compared to itself, it should be 100% similar
     * 
     * @param sel
     * @param seed
     * @return
     */
    private double [] comparePhraseLL(PhraseLL sel, PhraseLL seed, double scope) {
        double [] ret = new double [sel.length()];
        sel.resetIter();
        int i=0;
        Phrase at = sel.iter();
        while(i< sel.length()) {
            
            double highc = 0;
            
            // go through and find the highest one
            seed.resetIter();
            Phrase sd = seed.iter();
            int j = 0; 
            while(j<seed.length()) {
                double com = this.comparePhrases(at, sd, scope);
                if(com > highc)
                    highc = com;
                
                sd = seed.iter();
                j++;
            }
            
            // put it in
            ret[i] = highc;
            
            // update
            at = sel.iter();
            i++;
        }
       // PO.p("compare phrase ll = \n", ret);
            
        return ret;
    }

    /**
     * returns the level of similarity between the phrases. (1.0 is similar
     * 0.0 is dissimilar.
     * @param a
     * @param b
     * @param scope
     * @return
     */
    protected double comparePhrases(Phrase a, Phrase b, double scope) {
        double cp, cr, cs, ctr;
        try {
            if (a == null || b == null) {
                return 0.0;
            }

            cp = pcom.compare(a.getNote(0)
                .getPitch(), b.getNote(0)
                .getPitch()) * params[0].getValue();
        //    PO.p("cp = " + cp);
            
            cr = rcom.compare(a.getNote(0)
                .getDuration(), b.getNote(0)
                .getDuration()) * params[1].getValue();
            // System.out.println(" a " + a.toString());
            // System.out.println(" b " + b.toString());

            cs = scom.compare(a.getStartTime() % scope,
                b.getStartTime() % scope)
                    * params[2].getValue();

            // System.out.println("p " + cp + "r" + cr +
            // "s" + cs);
            // System.out.println("pw " +
            // params[0].getValue() + "rw" +
            // params[1].getValue() + "sw" +
            // params[2].getValue());
            ctr = (cp + cr + cs)
                    / (params[0].getValue() + params[1].getValue() + params[2].getValue());

            
       //     PO.p("ctr = " + ctr);
            return ctr;
            // if( cp == 1 || cr == 1 || cs == 1)
            // System.out.println("ctr = " + ctr);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            PO.p(npe.getLocalizedMessage());
            PO.p(npe.getMessage());
        }
        return -1;
    }

    protected void prepareSeed(PhraseQ hist, int depth, Part seed) {
        seed.empty();
        hist.fillWithLast(seed.getPhraseList(), depth, true);
    }

    public void startInit() {
        this.streamLostCount = 0;
        this.nullPredictionCount = 0;
    //    this.playLoggedCount = 0;
        this.count = 0;
    }

    public void finish() {
        PO.p(" for morph channel " + this.morphChannel);
        PO.p("null prediction rate was "
                + (nullPredictionCount * 1.0 / count * 1.0));
        PO.p("stream lost rate was " + (streamLostCount * 1.0 / count * 1.0));
    //    PO.p("play logged rate was " + (playLoggedCount * 1.0 / count * 1.0));
    }
    
    public String getType() {
        return "markovMorph2RT";
    }
    
    public ParameterMap[] getPC() {
        return params;
    }

    public void dload(Element e) {
        for (int i = 0; i < this.params.length; i++) {
            String str = e.getAttribute(params[i].getName()
                .replace(' ', '_'));
            if (str.length() > 0) {
                params[i].setValue(str);
            }
        }
    }

    public void dsave(Element e) {
        e.setAttribute("type", this.getType());
        for (int i = 0; i < this.params.length; i++) {
        	String ts = params[i].getName().replace(' ', '_');
        	ts = ts.replaceAll("<", "&smaller_than");
        	ts = ts.replaceAll(">", "&greater_than");
        	ts = ts.replaceAll("-", "&minus");
        	
            e.setAttribute(ts, params[i].getValueStr());
        }
    }

}