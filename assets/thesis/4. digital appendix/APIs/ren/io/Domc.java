package ren.io;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.IdentityHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ren.util.PO;

/*
 * Load cache - makes sure that objects referenced by different
 * classes are not recreated separately on loading
 * Created on 24/07/2005
 *
 * @author Rene Wooller
 */

public class Domc {

    public static final String VERSION = "1";
    
    private static final String prox = "proxyID";
    public static final String domID = "ID";
    private static final String type = "objectType";
    private static final String className = "class";
    
    private static IdentityHashMap lm = new IdentityHashMap();

    private static IdentityHashMap sm = new IdentityHashMap();

    private static int sidc = 0; // save id counter

    public static void init() {
    	System.setProperty("entityExpansionLimit", "256000");
    	
        sidc = 0;
        lm = new IdentityHashMap();
        sm = new IdentityHashMap();
    }

    /**
     * 
     * @param toSave
     * @param name
     * @param el the element which contains the document owner that will be used
     * @return
     */
    public static Element sa(Domable toSave, String name, Element el) {
        return sa(toSave, name, el.getOwnerDocument());
    }
    
    /**
     * call this method to obtain an element from the
     * Domable instance that is being saved to ensure
     * circular references can be saved
     * 
     * @param toSave
     *            instance to save
     * @param name
     *            instance variable name
     * @param d
     *            the document owner
     * @return the element that is created to be put in
     *         the DOM Note, if it is a refernce to an
     *         object that has already been saved it
     *         will return a proxy element which
     *         contains one attribute "proxyID" which is
     *         the ID of the attribute being referenced.
     */
    public static Element sa(Domable toSave,
            String name, Document d) {
        
        //check to see if an element has already been assigned to this Domable
        Element sae = (Element) sm.get(toSave);
        //if it hasn't been assigned
        if (sae == null) {
           // PO.p("making new element " + name);
            // create an element
            sae = d.createElement(name);
            
            // identify it as an original original w/ number
            sae.setAttribute(domID,
                String.valueOf(sidc++));
        //    sae.setIdAttribute(domID, true);
           //sae.se
            sae.setAttribute(className, toSave.getClass().getName());
            
            
            if(toSave instanceof Factorable) {
                sae.setAttribute(Domc.type, ((Factorable)toSave).getType());
            }
            
            // register it with the list
            sm.put(toSave, sae);
            
            // add all the sub-nodes and attributes, which
            // are allowed to be the same refrenes as this (proxy)
            toSave.dsave(sae);
            return sae;
        } else {
            // if this element has already been assigned,
            // create a proxy element
            Element toRet = d.createElement(name);
            // with a refernce to the id of the original element
            // that has already been asigned to this object
            toRet.setAttribute(Domc.prox,
                sae.getAttribute(domID));
            return toRet;
        }
    }

    public static Object lo(Element e, Class c, Element ode) {
        return lo(e, c, ode.getOwnerDocument());
    }
    
    public static Object lo(Element e, Class c,
            Document doc) {

        // check to see if the object has been created
        // previously
        Domable d = (Domable) lm.get(e);
        if (d != null) {
            return d; // return it if it has
        } else { // if the object is yet to be created
            // check if it is a proxy node
            if (e.hasAttribute(Domc.prox)) {
                // find orginal element, and load the
                // object relating to it
               // System.out.println(e.get)
                return lo(getElementById(e.getAttribute(Domc.prox), doc),c, doc);
            } else {
                //if it isn't a proxy node, create it
                try {
                    if(c.isInterface())
                        System.out.println("the class is an interface!");
                    
                    d = (Domable) c.newInstance();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                // put it in the list before loading, so 
                // that it can reference itself
                lm.put(e, d);
                
                d.dload(e);
                
                return d;
            }
        }
    }
    
    
    /**
     * because doc.getElementById doesn't work because it's not supported in 1.4
     */
    private static Element getElementById(String id, Document d) {
		
    	Element ret = null;
    	
    	NodeList nl = d.getChildNodes();
    	for(int i=0; i< nl.getLength(); i++) {
    	//	PO.p("BBBBBBBBBBBase " + i);
    		ret = getElementById(id, (Element)nl.item(i));
    		if(ret != null)
    			break;
    	}
		
		if(ret == null) {
			PO.p("id " + id + " doesn't exist in document " + d.toString());
		}
		return ret;
	}
        
    private static Element getElementById(String id, Element el) {
   // 	PO.p("el " + el.getTagName() + " id = " + el.getAttribute(domID));
    	
    	if(el.getAttribute(domID).equalsIgnoreCase(id)) {
    		return el;
    	}
    	
    	NodeList nl = el.getChildNodes();
    //	PO.p("id to get = " + id);
    	Element ret = null;
    	
    	for(int i=0; i< nl.getLength(); i++) {
    		ret =  getElementById(id, (Element)nl.item(i));
    		if(ret != null)
    			return ret;
    	}
    	
    	// doesn't exist in this branch
    	return null;
    }

	public static Object lo(Element e, Document doc) {
        // if it is a proxy, it will not record the class
        if(e.hasAttribute(Domc.prox))
            return lo(getElementById(e.getAttribute(Domc.prox), doc), doc);
        
        if(!e.hasAttribute(className)) {
            try{throw new Error("the element " + e.getTagName() + " isn't a proxy and doesn't have a class");
            } catch(Error err) {err.fillInStackTrace(); err.printStackTrace();} 
        }
        
        Class c = null;
        try {
            c = Class.forName(e.getAttribute(className));
        } catch(Exception ex) {ex.printStackTrace();}
        
        return lo(e, c, doc);
    }
    
    
    public static Object lo(Element e, Factory factory, Document doc) {
//      check to see if the object has been created
        // previously
        Domable d = (Domable) lm.get(e);
        if (d != null) {
            return d; // return it if it has
        } else { // if the object is yet to be created
            // check if it is a proxy node
            if (e.hasAttribute(Domc.prox)) {
                // find orginal element, and load the
                // object relating to it
                // System.out.println(e.get)
                return lo(getElementById(e.getAttribute(Domc.prox), doc), factory, doc);
            } else {
                //if it isn't a proxy node, create it
                try {
                    d = (Domable) factory.createObj(e.getAttribute(Domc.type));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                // put it in the list before loading, so 
                // that it can reference itself
                lm.put(e, d);
                
                d.dload(e);
                
                return d;
            }

        }
    }
    

    public static Document makeDoc() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {  
            DocumentBuilder db = dbf.newDocumentBuilder();    
            return db.newDocument();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void outDoc(String path, Document d) {
        try {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();    
        BufferedOutputStream out = 
            new BufferedOutputStream(new FileOutputStream(path)); 
        
        DOMSource source = new DOMSource(d);
        StreamResult stream = new StreamResult(out);
        trans.transform(source, stream);
        out.flush();
        } catch (Exception e) {e.printStackTrace();}
    }
    
    /**
     * don't use IDattribute (compatable with 1.4
     * 
     * sets the id attribute of the node to be
     * equal to a named attribute, if it exists, and 
     * recursively performs the same method on each sub-node
     * @param n the node
     * @param id the string specifying the name of attribute 
     * to associate with id
     * 
    public static void  setID(Node n, String id) {
        //if it is an element
        if(n instanceof Element) {
            Element eln = (Element)n;
            // if it has an id
            if(eln.hasAttribute(id))
                eln.setIdAttribute(id, true);    
        }
        
        NodeList nl = n.getChildNodes();
        for(int i=0; i<nl.getLength(); i++) {
            setID(nl.item(i), id);
        }
    }
    
    public static void setID(Node n) {
        setID(n, domID);   
    }*/

    public static Object lo(Element e, String type, Document o) {
        try {
            return lo(e, Class.forName(type), o);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static Element find(Element e, String n) {
        NodeList nl = e.getElementsByTagName(n);
        if(nl.getLength() == 0)
            return null;
        else
            return (Element)nl.item(0);
            
    }
   
    /*
    public static NodeList getNodesStartWith(Element e, String str) {
        NodeList nl = e.getChildNodes();
        IIOMetadataNode imd = new IIOMetadataNode();
        for(int i=0; i< nl.getLength(); i++) {
            if(nl.item(i).getNodeName().startsWith(str)) {
                imd.appendChild(nl.item(i));
            }
        }
        return imd;
    }*/
    
}
