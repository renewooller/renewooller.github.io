/*
 * Created on 2/12/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package music.transform;

import jm.music.data.Part;
import jm.music.data.Score;
import jmms.TickEvent;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.io.Domable;
import ren.io.Domc;
import ren.io.Factorable;

/**
 * @author wooller
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Transformer implements Domable, Factorable {
		
    protected TransformChain motherChain;
    
	public Transformer next;
	
    protected ParameterMap [] paramMaps = new ParameterMap [1];
    
    protected Part [] streamParts;
    
	public Transformer() {
			
	}
    
	// need to implement a stream buffer - possibly using a que or linked list
	// returns the stream output from this stage of the process
	public Part transform(Part stream, Object context, TickEvent e) {
	    if(next == null)
	        return stream;
	    else
	        return next.transform(stream, context, e);
	}
	
	
	/**
	 * The context should be passed throught the entire chain so that if anything needs to use it, they can
	 * 
	 * transformers should always return the stream
	 * @param stream
	 * @param root
	 * @param e
	 * @return
	 */
	Part tsp; // temporary stream part
	public Score transform(Score stream, Object context, TickEvent e) {
	    
	    streamParts = stream.getPartArray();
        
        for(int i=0; i<streamParts.length; i++) { 
            tsp = transform(streamParts[i], context, e);       
            // in the rare event of the stream being recreated rather than transformed
            if(tsp != streamParts[i]){
                stream.removePart(streamParts[i]);
                stream.add(tsp);
            }
        }
	    return stream;       
	}
	
	public abstract ParameterMap [] getParamMaps();
	
	public abstract String getType();
	
	public String toString() {
		return this.getType();
	}
	
	public Transformer copy() {
	    Transformer toRet = TFactory.create(this.getType());
	    ParameterMap [] thisParams = this.getParamMaps();
	    ParameterMap [] toParams = toRet.getParamMaps();
	    for(int i=0; i< toParams.length; i++) {
	        toParams[i].setValue(thisParams[i].getValue());
	    } 
	    return toRet;
	}
    
    public void dsave(Element e) {
        e.setAttribute("type", this.getType());
        e.setAttribute("class", this.getClass().getName());
        if(next != null)
            e.appendChild((Element)Domc.sa(next, "next", e.getOwnerDocument()));
        
        if(this.motherChain != null)
            e.appendChild((Element)Domc.sa(motherChain, 
                MO_CH, e.getOwnerDocument()));
        
    }
    
    private static String MO_CH = "motherChain";
    private static String NEXT = "next";
    
    public void dload(Element e) {
        Element mce = (Element)(e.getElementsByTagName(MO_CH).item(0));
        Element ne = (Element)(e.getElementsByTagName(NEXT).item(0));
        if(ne != null) {
            this.next = (Transformer)(Domc.lo(ne,ne.getAttribute("class"), 
                e.getOwnerDocument()));
        }
        if(mce != null) {
            this.motherChain = (TransformChain)(Domc.lo(mce, 
                TransformChain.class, 
                e.getOwnerDocument()));
        }
        
    }

    public void setTC(TransformChain chain) {
        this.motherChain = chain;
    }
    
}
