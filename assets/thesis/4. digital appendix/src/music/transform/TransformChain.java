/*
 * Created on 2/12/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package music.transform;

import jm.music.data.Part;
import jm.music.data.Score;
import jm.music.tools.Mod;
import jmms.TickEvent;
import lplay.LPlayer;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.io.Domable;
import ren.io.Domc;
import ren.util.PO;


/**
 * @author wooller
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransformChain implements Domable {

	private Transformer first, last;
	
	private int length = 0;
    
	public TransformChain() {}
	
	public void construct() {
        empty();
        add(TFactory.create("scope"));
        add(TFactory.create("quantise"));
        add(TFactory.create("shuffle"));
	}
	
	public void empty() {
	    first = null;
        last = null;
	}
	
	public ParameterMap getScopeParam() {
		if(first instanceof Scope) {
		    return first.getParamMaps()[0];
		} else {
			return null;
		}
	}
    
    public ParameterMap getQuantiseParam() {
               
        if(first.next instanceof Quantise) {
            return first.next.getParamMaps()[0];
        } else if(this.length > 2){
            return getFirstOfClass(Quantise.class).getParamMaps()[0];
           
        }
        return null;
    }
   
    public ParameterMap getShuffleParam() {
        if(first.next.next instanceof Shuffle) {
            return first.next.next.getParamMaps()[0];
       } else  if( this.length > 2) {
           return getFirstOfClass(Shuffle.class).getParamMaps()[0];
       }
           return null;
    }
    
    public Transformer getFirstOfClass(Class c) {
        resetIterate();
        Transformer toGet = this.iterate();  
        while(toGet != null) {
            if(toGet.getClass().getName().equals(c.getName())) {
                return toGet;
            }
            toGet = this.iterate();
        }
        return null;
    }
	
	
	/**
	 * This will be called after the scope has made a copy
	 * of the data already, so there is really no need to copy anything
	 * @param stream
	 * @param context
	 * @param e
	 * @return
	 */
	public Part transform(Part stream, Object context, TickEvent e) {
	    // apply all of the transformations
	    e = e.copy();
	//       System.out.println("  1111 " + stream.toString());
	    // scope adds a rest to the end
	    stream = first.transform(stream, context, e);
	    
        //remove the rest at the end so that it doesn't stuff the order around
       // if(stream.getPhrase(stream.size()-1).getNote(0).isRest()) {
       //     stream.removeLastPhrase();
      //  }
	    //   System.out.println("  22222 " + stream.toString());
	    
	    // if the transformations happened to shorten it too much, loop it
//	    if(e.at() > stream.getEndTime()) {
	       // System.out.println("e at = " + e.at() + " end time = " + stream.getEndTime());
	//        e.setAt(e.at()%stream.getEndTime());
//	    } // this is in rate now
	    
	   // System.out.println("stream.getEntTime() = " + stream.toString());
	    
	    //trim the stream, if it hasn't been trimmed (rt morph streams will be trimmed already)
	  //  if(stream.getChannel() == 9 || stream.getChannel() == 10)
	//        PO.p("stream  before last cropping" + stream.toString());
        
   //    if(stream.getEndTime() > ((LPlayer)e.getSource()).res()){    
	       /////remove the data before and after the timeslice
	   
            //Mod.cropFaster(stream, e.at(), ((LPlayer)e.getSource()).plusRes(e.at()), true);
 //           Mod.cropFaster(stream, 0, ((LPlayer)e.getSource()).res(), true);
//       }   
	   
   //     PO.p("stream after transform chain = " + stream.toString());
        
	    
		return stream;
	}
	
	/**
	 * this isn't really used at the moment.
	 * @param stream
	 * @param context
	 * @param e
	 * @return
	 */
	public Score transform(Score stream, Object context, TickEvent e) {
	    stream = first.transform(stream, context, e);
	    
	    //trim the stream 
	    if(stream.getEndTime() > ((LPlayer)e.getSource()).res()){
	        stream = stream.copy(e.at(), ((LPlayer)e.getSource()).plusRes(e.at()), true, false, false);        
	    }
	    System.out.println("transforming the score");
	    return first.transform(stream, context, e);
	}
	
	
	
	public void resetIterate() {
			iter = first;
	}
	
	private transient Transformer iter = first;
	public Transformer iterate() {
		if(iter == null) {
			iter = first;
			return null;
		}
		t1 = iter;
		iter = iter.next;
		return t1;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("transform chain");
		this.resetIterate();
		Transformer at = iterate();
		int i=0;
		while(at != null) {
			sb.append(" i = " + i + "  " + at.toString() + "  ");
			at = iterate();
			i++;
		}
		return sb.toString();
	}
	
	private transient Transformer t1, t2;
	//inserts the first transformer one step after the at transformer
	public void insert(Transformer in, Transformer at) {
		
		if(first == null)  {
			first = in;
			last = in;
			return;
		}
		
		t1 = null;
		t2 = first;
		while(t1 != at){
			if(t2 == null)
				return; // can't find the at
			t1 = t2;
			t2 = t1.next;
		}
		
		if(t2 == null) { // if it reached the end
			t1.next = in;
			in.next = null;
			last = in;
		} else {
			t1.next = in;
			in.next = t2;
		}
		
		length++;
	}
	
	public void add(Transformer toAdd) {
        if(toAdd == null) {
            try {
                Exception ex = new NullPointerException(
                    " trying to add a transformer, but it is null");
                throw ex;
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
            
        
		if(last == null) {
			if(first != null) {
				System.out.println("serious error in Transform.add()");
				return;
			} else {
				first = toAdd;
				last = toAdd;
				length = 1;
				return;
			}
		}
		last.next = toAdd;
		toAdd.next = null;
		last = last.next;
		length++;
        toAdd.setTC(this);
	}
	
	public void remove(Transformer rm) {
		if(rm == first)
			first = first.next;
		
		t1 = null;
		t2 = first;
		while(t2 != rm) {
			if(t2 == null)
				return; //can't find it
			
			t1 = t2;
			t2 = t2.next;
		}
		
		t1.next = t2.next; //t1.rm(t2).(t2.next) -> t1.(t2.next)
		length--;
		
	}

	public int getLength() {
		return length;
	}
	
	public TransformChain copy() {
	    this.resetIterate();
	    
	    TransformChain toRet = new TransformChain();
	    Transformer tf = this.iterate();
	    while(tf != null) {
	        toRet.add(tf.copy());
	        tf = this.iterate();
	    }
	    return toRet;
	}

    
    
    public void dload(Element e) {
        // set just the first node, and then iterate to get the last node
        Element fe = (Element)(e.getElementsByTagName(FIRST_TRANS).item(0));
        if(fe == null) {
            this.construct();
            return;
        }
        
        this.first = (Transformer)Domc.lo(fe, TFactory.getInstance(), e.getOwnerDocument());
        this.length = 0;
        this.iter = first;
        while(iter.next != null) {
            this.length++;
            iter = iter.next;
        }
        this.last = iter;
        this.resetIterate();
        
        
      //  if(this.length <3)
       //     this.construct();
        
        /*
        NodeList nl = e.getChildNodes();
        
        for(int i=0; i<nl.getLength(); i++) {
            this.add(((Transformer)Domc.lo(nl.item(i), Transformer.class, e.getOwnerDocument())));
        }*/
        
    }

    private static String FIRST_TRANS = "firstTransformer";
    
    public void dsave(Element e) {
        resetIterate();
        Transformer toSave = this.iterate();
        e.appendChild((Element)Domc.sa(toSave, FIRST_TRANS, 
            e.getOwnerDocument()));
       
       
    }
	
}
