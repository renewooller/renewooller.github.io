/*
 * Created on 3/12/2004
 *
 * rene wooller
 */
package music;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.music.tools.Mod;
import jmms.NoteEvent;
import jmms.TickEvent;
import music.transform.TransformChain;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ren.gui.ParameterMap;
import ren.io.Domable;
import ren.io.Domc;
import ren.util.PO;
import ren.util.Save;

/**
 * @author wooller
 * 
 * this contains a bunch of LParts and is used to hold
 * meta tnsformations and VGs and get scores from the
 * lpartArr
 */
public class LScore implements TransformChainHolder,
        Serializable, Domable{

    String name = "unnamed"; // used for saving parts in

    /* holds all the lpartArr */
    LPart[] lpartArr;

    int lpartNum = 0;

    // may put default LPart arrays in here, eg
    // DEFAULT_BIGBAND etc....

    /* tempo of the whole score can be updated remotely */
    ParameterMap tempoParam = (new ParameterMap()).construct(0,
            210, 40.0, 250.0, 125.0, "tempo");

    /* the transformation chain for the whole score */
    TransformChain tc;

    public LScore() {
        init();
    }

    private void init() {
        lpartArr = new LPart[60];
        lpartNum = 0;
        tempoParam = (new ParameterMap()).construct(0,
            210, 40.0, 250.0, 125.0, "tempo");
        (tc = new TransformChain()).construct();
        s = new Score(name);
    }

    //need to change this so that it uses the prg
    // changes - maybe it clears data at a later morph
    // stage?
    //temporary variables used in the algorithm
    transient LPart[] pa;

    transient LPart[] p;

    /**
     * adds all the parts from the two lscores into this
     * one, used for morphing, where you need two
     * different parts for each of the parts being
     * morphed to and from
     * 
     * if if one is longer than the other, it puts two
     * lpartArr for each lpart from the one that is
     * longer
     * 
     * When it merges, it should actually retain the idchannels
     * because there is not "adding", and it copies the idchannels
     * 
     * as well as this, is performs a quicksort on each original Part
     * 
     * @param ls1
     * @param ls2
     */
    public void mergeLScores(LScore ls1, LScore ls2) {
        // fill the arrays
  //      PO.p("merging lscores");
        //determine the maximum length that this needs
        // to be
        int len = Math.max(ls1.size(), ls2.size()) * 2;

        // check to see if it is too big
        if (len > lpartArr.length)
            lpartArr = new LPart[len * 2];

        // go through and initialise all the lpartArr
        int i = 0;
        for (i = 0; i < len; i++) {
            if (i % 2 == 0) { // if it relates t the
                // first one
                // sort from
                
                
                
                if ((int) (i * 0.5) < ls1.size()) {
                    // make sure the part is in order
                    Mod.quickSort(ls1.getLPart((int) (i * 0.5)).getPart());
                    //if a part hasn't bee allocated, make one
                    if (lpartArr[i] == null)
                        lpartArr[i] = new LPart();
                    // empty the part and copy the part
                    // attributes and the transformation
                    // chain
                    lpartArr[i].emptyCopyFrom(ls1.getLPart((int) (i * 0.5)));
                } else { // if there is nothing to morph to 
                    // make one
                    if (lpartArr[i] == null)
                        lpartArr[i] = new LPart();
                    // empty the part and copy the part
                    // attributes and the transformation
                    // chain
                    lpartArr[i].emptyCopyFrom(ls2.getLPart((int) (i * 0.5)));
                }
            } else {
                
                if ((int) (i * 0.5) < ls2.size()) {
                    // sort the to part
                    Mod.quickSort(ls2.getLPart((int) (i * 0.5)).getPart());
                                        
                    //if the part hasn't been allocated yet, make it
                    if (lpartArr[i] == null)
                        lpartArr[i] = new LPart();
                    // empty the part and copy the part
                    // attributes and the transformation
                    // chain
                    lpartArr[i].emptyCopyFrom(ls2.getLPart((int) (i * 0.5)));
                } else { // if it has run out of parts
                    // that relate to the first one
                    // (nothing to morph from)
                    // if it hasn't got a part in it,
                    // make one
                    if (lpartArr[i] == null)
                        lpartArr[i] = new LPart();
                    // empty the part and copy the part
                    // attributes and the transformation
                    // chain
                    lpartArr[i].emptyCopyFrom(ls1.getLPart((int) (i * 0.5)));
                }
            }
        }
        this.lpartNum = len;
    }

    /*
     * public void empty() {
     * this.lpartArr.removeAllElements(); }
     */

    public void record(NoteEvent e) {
        PO.p("going through the parts");
        for(int i=0; i<this.lpartNum; i++) {
            PO.p("i = " + i + " chan = " + e.getChannel());
            if(lpartArr[i].getPart().getChannel() == e.getChannel()) {
                PO.p("in channel");
                e.getNotePhr().setStartTime(e.getNotePhr().
                    		   getStartTime()%this.tc.getScopeParam().getValue());
                PO.p(e.getNotePhr().toString());
                lpartArr[i].getPart().add(e.getNotePhr());
            }
        }
    }
    

    
    public void add(Score s) {
        for(int i=0; i< s.size(); i++) {
            add(s.getPart(i));
        }
    }
    
    /**
     * 
     * 
     * public LScore(int size) { for (int i = 0; i <
     * size; i++) { this.add(new LPart()); } }
     */

    public void add(LPart[] toAdd) {
        for (int i = 0; i < toAdd.length; i++) {
            this.add(toAdd[i]);
        }
    }

    public void add(LPart lpart) {
        lpart.getPart().setIdChannel(lpartNum);
        lpartArr[lpartNum++] = lpart;
        //lpartArr.add(lpart);
    }

    public void add(Part p) {
        add((new LPart()).construct(p));
    }

    transient LPart tis1, tis2;

    public void add(LPart lpart, int pos) {
        tis1 = lpart;
        lpartNum++;
        // do insertion
        for (int i = pos; i < lpartNum; i++) {
            // store the lpart that is to be replaced
            tis2 = this.lpartArr[i];
            // replace it with lpart that has been
            // stored previously
            this.lpartArr[i] = tis1;
            // make the stored one into the one that
            // will be inserted next
            tis1 = tis2;
        }
    }

    public void remove(int pos) {
        tis1 = null;

        // do insertion
        for (int i = pos; i < lpartNum; i++) {
            // store the lpart that is to be replaced
            this.lpartArr[i] = this.lpartArr[i + 1];
         //  this.lpartArr[i].set
        }

        lpartNum--;
    }

    public void empty() {
        lpartNum = 0;
    }
    
  //  transient Enumeration enumr;

    transient Score s;

    public Score getTickScore(TickEvent e) {
        
        s.empty();
        boolean soloDown = false;
        for (int i = 0; i < lpartNum; i++) {
            if(lpartArr[i].isSolo()) {
                soloDown = true;
                break;
            }
        }
        
        for (int i = 0; i < lpartNum; i++) {
            if(soloDown) {
                if(lpartArr[i].isSolo()) {
                    if(!lpartArr[i].isMute()) {
                        s.addPart(lpartArr[i].getTickPart(e));
                    }
                }
            } else {
                
                
                if(!lpartArr[i].isMute()) {
                    
                    s.addPart(lpartArr[i].getTickPart(e));
                } else {
                   
                }
            }

        }
        return s;
    }

    public TransformChain getTransformChain() {
        return this.tc;
    }

    public Score getJMScore() {
    	return getJMScore(true);
    }
    
    /**
     * creates a new score, but uses the same parts
     * 
     * @return
     */
    public Score getJMScore(boolean ignoreScope) {

        Score score = new Score();
        for (int i = 0; i < lpartNum; i++) {
    //        System.out.println(" channel of part " + i + " in  lscore is " + lpartArr[i].getPart().getChannel());
            //lpartArr[i].getTickPart(e); 
        	if(!ignoreScope) {
        		Part p = lpartArr[i].getPart().copy();
        		Mod.cropFaster(p, 0., lpartArr[i].getScope().getValue());
        		score.addPart(p);
        	} else {
        		score.addPart(lpartArr[i].getPart());
        	}
        }
        score.setTempo(this.tempoParam.getValue());
        return score;
    }

    public void setJMScore(Score score) {
        //reset lpartNum
        lpartNum = score.getSize();

        //go through and set the lpartArr
        for (int i = 0; i < lpartNum; i++) {
            //if it's not there than make one
            if (lpartArr[i] == null)
                lpartArr[i] = (new LPart()).construct((Part) score.getPart(i));
            else { // if its there, reset it
                lpartArr[i].reset();
                lpartArr[i].getPart()
                    .copyFrom((Part) score.getPart(i));
            }
        }
        this.tempoParam.setValue(score.getTempo());
    }

    /**
     * get the lpart at the specified position
     * 
     * @param pos
     * @return
     */
    public LPart getLPart(int pos) {
       //its not here
        if (pos > lpartNum) {
            try {
                (new Error(
                        "trying to get a part that doesn't exist in the lscore")).fillInStackTrace();
            } catch (Error e) {
                e.fillInStackTrace();
            }
        }
        return lpartArr[pos];
    }

    /**
     * 
     * @return
     * 
     * public LPart [] getLPartArray() { LPart [] toRet =
     * new LPart [this.lpartArr.size()];
     * lpartArr.toArray(toRet); return toRet; }
     */

    public Part getPart(int pos) {
   //    System.out.println("getting part " + pos + " from lscore");
        return this.getLPart(pos)
            .getPart();
    }

    public ParameterMap getTempoParam() {
        return this.tempoParam;
    }

    public int size() {
        return this.lpartNum;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getName() {
    	return this.name;
    }
    
    // for the benefit of any jcomponent holders
    public String toString() {
    	StringBuffer sb = new StringBuffer("lscore ");
    	sb.append("\nname = " + getName());
    	sb.append("\nsize = " + this.size());
    	sb.append("\nparts : \n");
    	for(int i=0; i< size(); i++) {
    		sb.append("    part " + i);
    		sb.append(lpartArr[i].getPart().toString());
    	}
        return sb.toString();
    }

    // prin the current state of the lscore for
    // debugging etc
    public String print() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.toString());
        sb.append(this.tempoParam.getValue());

        for (int i = 0; i < lpartNum; i++) {
            sb.append(" /n " + lpartArr[i].print());
        }
        return sb.toString();
    }

    private void writeObject(ObjectOutputStream oos)
            throws Exception {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois)
            throws Exception {
        ois.defaultReadObject();
        s = new Score(name);
    }

    private String lb = ("<" + this.getClass()
        .getName() + ">");

    private String rb = ("</" + this.getClass()
        .getName() + ">");

    public String getXML() {
        StringBuffer sb = new StringBuffer();
        sb.append(lb);
        //name
        sb.append("name = " + "\"" + this.name + "\"");

        //tempo
        sb.append("tempo = " + "\""
                + this.tempoParam.getValue() + "\"");
        /*
         * //lpartArr LPart [] arr =
         * this.getLPartArray(); for(int i=0; i <
         * arr.length; i++) {
         * sb.append(arr[i].getXML()); }
         */

        sb.append(rb);

        return sb.toString();
    }

    /**
     * loads data for this object from the string given.
     * This needs to the tag of only this class (or it
     * may find variables in other tags)
     * 
     * @param xml
     */
    public void loadXML(String xml) {
        int pos = xml.indexOf(lb);

        //int endPos = xml.indexOf(rb);
        //catch the error if this class does not exist
        // in the LScore
        if (pos == -1) {
            try {
                Exception e = new Exception();
                e.fillInStackTrace();
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //name
        this.setName(Save.getStringVar(xml, "name"));

        //tempo
        this.tempoParam.setValue(Save.getDoubleVar(xml,
            "tempo"));

    }

    public void dload(Element e) {
        this.setName(e.getAttribute("name"));
        this.getTempoParam().setValue(Double.parseDouble(e.getAttribute("tempo")));
        this.tc = (TransformChain)Domc.lo((Element)(e.getElementsByTagName("tc").item(0)),
                                          TransformChain.class, e.getOwnerDocument());
        
        NodeList lpn = e.getElementsByTagName("lpart");
        for(int i=0; i<lpn.getLength(); i++) {
            this.add((LPart)Domc.lo(((Element)(lpn.item(i))), LPart.class, e.getOwnerDocument()));
            
        }
        
    }

    public void dsave(Element e) {
        e.setAttribute("name", this.name);
        e.setAttribute("tempo", String.valueOf(this.tempoParam.getValue()));
        e.appendChild(Domc.sa(tc, "tc", e.getOwnerDocument()));
        for(int i=0; i< this.lpartNum; i++) {
            e.appendChild(Domc.sa(this.lpartArr[i], "lpart", e.getOwnerDocument()));          
        }
    }

    public boolean shiftDown(int i) {
        if(i >= this.size() || i <= 0)
            return false;
        swap(i, i-1);
        return true;
    }
    
    public boolean shiftUp(int i) {
        if(i >= this.size()-1 || i < 0)
            return false;
        swap(i, i+1);
        return true;
    }
    
    public void swap(int i, int j) {
        LPart t = lpartArr[i];
        lpartArr[i] = lpartArr[j];
        lpartArr[j] = t;
        lpartArr[i].getPart().setIdChannel(i);
        lpartArr[j].getPart().setIdChannel(j);
    }

    
    public void mergeSameChan() {
        /// set all the scopes
        int [] sps = new int [this.lpartNum];
        for(int i=0; i< this.lpartNum; i++) {
            sps[i] = lpartArr[i].getScope().getValueInt();
        }
        
      //  PO.p("sps bfore = ", sps);
        // collate the largest scopes of each lpart
        for(int i=0; i< this.lpartNum; i++) {
            for(int j=i+1; j<this.lpartNum; j++) {
                if(lpartArr[i].getPart().getChannel() ==
                    lpartArr[j].getPart().getChannel()) {
                    sps[j] = Math.max(sps[j], sps[i]);
                    sps[i] = sps[j];
                }
            }
        }
     //   PO.p("sps after = ", sps);
        int cnt = 0;
        for(int i=0; i<this.lpartNum; i++) {
            // repeat the part we are at
            if(lpartArr[i].getScope().getValue() < sps[i]) {
                Mod.repeatRT(lpartArr[i].getPart(),
                             lpartArr[i].getScope().getValue(),
                             sps[i]);
            }
            
            for(int j=i+1; j<this.lpartNum; j++) {
                if(lpartArr[i].getPart().getChannel() == 
                    lpartArr[j].getPart().getChannel()) {
                    /*
                    if(lpartArr[i].getPart().getChannel() == 9) {
                        
                        PO.p(lpartArr[j].getPart().getTitle());
                        PO.p("scope = " + lpartArr[j].getScope().getValue());
                        PO.p("repeat tp = " + sps[i+cnt]);
                  } //*/
                        
                    
                    // repeat the part as necessary
                    if(lpartArr[j].getScope().getValue() < sps[j]) {
                        Mod.repeatRT(lpartArr[j].getPart(),
                                     lpartArr[j].getScope().getValue(), 
                                     sps[i+cnt]);
                    }
                                        
                    // add it
                    lpartArr[i].getPart().
                        addPhraseList(lpartArr[j].getPart().getPhraseArray(), 
                                      false);
                    
                    Mod.quickSort(lpartArr[i].getPart());
                    this.remove(j);
                    cnt++;
                    j--;
                }
            } 
        }
        updateIDChannels();
    }
    
    public void updateIDChannels() {
        for(int i=0; i< this.lpartNum; i++) {
            lpartArr[i].getPart().setIdChannel(i);
        }
    }

    /**
     * splits into separate pitched parts
     * 
     * @param sel
     */
    public void splitLPart(int sel) {
        Part p = this.lpartArr[sel].getPart();
        Part [] np = new Part [p.length()];
        int ni = 0;
        
        np[ni] = p.copyEmpty();
        np[ni].add(p.getPhrase(0));
        
       // np[++ni] = p.copyEmpty();
        for(int i=1; i<p.length(); i++) {
            int compi = ni;
            int opitch = p.getPhrase(i).getNote(0).getPitch();
            int npitch = np[compi].getPhrase(0).getNote(0).getPitch();
            while(opitch != npitch) {
                compi--;
                if(compi == -1)
                    break;
                npitch = np[compi].getPhrase(0).getNote(0).getPitch();
            }
            if(compi == -1) {
                np[++ni] = p.copyEmpty();
                np[ni].add(p.getPhrase(i));
            } else {
                np[compi].add(p.getPhrase(i));
            }
        }
        this.lpartArr[sel].setPart(np[0]);
        for(int i=1; i<ni; i++) {
            this.add(np[i]);
        }
    }

	public void setLPart(LPart part, int i) {
		this.lpartArr[i] = part;
		this.lpartNum = Math.max((i+1), lpartNum);
	}

	public void setKey(int k) {
		for(int i=0; i< this.lpartNum; i++) {
			this.lpartArr[i].getTonalManager().setRoot(k);
		}
	}

	public void setScale(int s) {
		for(int i=0; i< this.lpartNum; i++) {
			this.lpartArr[i].getTonalManager().setScale(s);
		}
	}
}