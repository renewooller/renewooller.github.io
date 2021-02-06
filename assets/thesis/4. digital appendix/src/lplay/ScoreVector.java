/*
 * Created on 19/06/2005
 *
 * @author Rene Wooller
 */
package lplay;

import java.util.Vector;

import jm.music.data.Part;
import jm.music.data.Score;
import jm.music.tools.Mod;

/**
 * meta-score, containing lots of score
 * 
 * @author wooller
 *
 *19/06/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class ScoreVector {

    Vector scores;
    
    /**
     * 
     */
    public ScoreVector() {
        super();
        scores = new Vector(10000);
    }
    
    public void addScore(Score nscore) {
        scores.add(nscore);
    }
    
    public Score getScore(int i) {
        return (Score)scores.get(i);
    }
    
    transient Score cs;
    transient Part [] cp;
    transient int [] cpi;
    /**
     * obtains the last n notePhrases that belong to the
     * specified part.
     * 
     * if there are not enough notePhrases, there will
     * be less than n notePhrases in the returned Part.
     * 
     * @param String the name of the part
     * 
     * @return Part a part with default attributes that 
     * stores all of the notePhrases in order
     * 
     * this method get the seed history that is 
     * relevant to a certain predictor.
     * 
     */
    public Part findLastFew(String [] partName, int n) {
        Part toRet = new Part();
        int i = scores.size()-1; // the index of scores vector
        
        // temporaries for holding and counting parts
        cp = new Part [partName.length]; 
        cpi = new int [partName.length];
        
        // go through, starting from the most recent
        while(toRet.size() < n) {
            if(i < 0)
                break;
            
            
            cs = getScore(i);
            for(int j = 0; j<partName.length; j++) {
                // add all of the phrases
                toRet.addPhraseList(
                    cs.getPart(partName[j]).getPhraseArray());               
            }
            
            i--;       
        }
        
        Mod.quickSort(toRet);
        
        //remove excess
        while(toRet.size() > n) {
            toRet.getPhraseList().remove(1);
        }
        
        return toRet;
    }
    
    
    /**
     * same as the the above one, but does it with two 
     * different Part-groups, and returns them in their
     * separate parts
     * 
     * @param partName names in the first part group
     * @param otherPartName name in the second part group
     * @param n
     * @return the parts in their different groups
     * 0 is the first partName
     * 1 is the second partName
     *
    public Part [] findLastFew(String [] partName, 
            		String [] otherPartName, int n) {
        
        
    }*/
    
    transient String [] starr = new String [1];
    /**
     * convenience method
     */
    public Part findLastFew(String partName, int n) {
        starr[0] = partName;
        return findLastFew(starr, n);
    }

}
