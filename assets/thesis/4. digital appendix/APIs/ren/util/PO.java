/*
 * Created on 30/05/2005
 *
 * @author Rene Wooller
 */
package ren.util;

import jm.music.data.Part;
import music.LPart;

/**
 * @author wooller
 *
 *30/05/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class PO {

    public static void p(double [] d) {
        System.out.print(" = { ");
        for(int i=0; i< d.length; i++) {
            System.out.print(d[i] + ", ");
        }
        System.out.println("}");
    }
    
    public static void p(double [] d, int dec) {
        p(d, dec, "\n");
    }
    
    public static void p(double [] d, int dec, String ps) { 
    	System.out.print(" = { ");
        for(int i=0; i< d.length; i++) {
        	StringBuffer dp = new StringBuffer(Double.toString(d[i]));
        	String out = new String();
        	
        	if(dp.charAt(dp.length()-3) == 'E') {
        		//PO.p("eee ");
        		StringBuffer ndp = new StringBuffer(dp.substring(0, dec-3));
        		ndp.append(dp.substring(17, 21));
        		out = ndp.toString();
        	} else if(dp.length() > dec){	
        		//PO.p("eee ");
            	out = dp.substring(0, dec+1);
        	} else {	
        		//PO.p("eee ");
        		while (dp.length() <= dec) {
        			dp.append('0');
        		}
        		out = dp.toString();
        	}
        	
            System.out.print(out + ", ");
            
        }
        System.out.print("} " + ps);
        
    }
    
    /**
     * 
     * @param s
     * @param p
     * @param type 0 - just pitch, 1 pitch and start times
     */
    public static void p(String s, Part p, int type) {
    	System.out.println("\n" + s);
    	if(type == 0) {
    		for(int i=0; i<p.size(); i++) {
    			System.out.print(
    					p.getPhrase(i).getNote(0).getPitch() + ", ");
    		}
    		
    	} else if (type == 1) {
    		for(int i=0; i<p.size(); i++) {
    			System.out.print(" " + ((int)(p.getPhrase(i).getStartTime()/0.0001))*0.0001 + ":" +
    					p.getPhrase(i).getNote(0).getPitch() + ", ");
    		}
    	} else if (type == 2) {
    		for(int i=0; i<p.size(); i++) {
    			System.out.print("s " + p.getPhrase(i).getStartTime() + " p:" +
    					p.getPhrase(i).getNote(0).getPitch() + " D: " + p.getPhrase(i).getNote(0).getDuration()+ ",  ");
    		}
    	}
    		
    	
    	System.out.print("\n");
    }
    
    public static void p(String s, Object [] o) {
    	System.out.println(s);
    	for(int i=0; i< o.length; i++) {
    		p(o[i].toString());
    	}
    }

    public static void p(String s, LPart [] lp) {
    	System.out.println(s);
    	for(int i=0; i< lp.length; i++) {
    		if(lp[i] == null)
    			break;
    		p(" i = " + i, lp[i].getPart(), 1);
    	}
    }
    
    public static void p(Object o) {
        System.out.println(o.toString());
    }
    
    public static void p(int [] d) {
        System.out.print(" = { ");
        for(int i=0; i< d.length; i++) {
            System.out.print(d[i] + ", ");
        }
        System.out.println("}");
    }
    
    public static void p(int  [][]d) {
    	//System.out.println("l = " + d.length);
    	for(int i=0; i<d.length; i++) {
    		//System.out.println(" sub l = " + d[i].length);
    		if(d[i] == null)
    			System.out.print(" " + i + " null \n");
    		else {
    		
        	   System.out.print(" " + i +"[");
            
        	   for(int j = 0; j< d[i].length; j++) {
        		   System.out.print("," + d[i][j] + " ");
                
        	   }
            
        	   System.out.print("],\n");
    		}
        }
        System.out.print("}");
    }
    
    public static void p(String out, double [] d) {
        System.out.print(out);
        p(d);
    }
    
    public static void p(String out, double [] d, int dec) {
        System.out.print(out);
        p(d, dec);
    }
    
    public static void p(String out, double [] d, int dec, String ps) {
        System.out.print(out);
        p(d, dec, ps);
        
    }

    public static void p(String out, int [] d) {
        System.out.print(out);
        p(d);
    }
    
    public static void p(String out, int [][] d) {
        System.out.print(out);
        p(d);
    }
    
    public static void p(String out, double [][]d) {
        System.out.print(out + "\n {");
        for(int i=0; i<d.length; i++) {
            System.out.print(" " + i +"[");
            for(int j = 0; j< d[i].length; j++) {
                System.out.print("," + d[i][j] + " ");
            }
            System.out.print("],\n");
        }
        System.out.print("}");
    }
    
    public static void p(String out) {
        System.out.println(out);
    }
    
    public static void isn(Object o) {
        if(o == null)
            p("null");
        else
            p("not null");
    }
    
    public static void p(String [] s) {
        for(int i=0; i<s.length; i++) {
            p(" = {" + s[i] + ", ");
        }
    }

    /**
     * repeats the given string the given number of times
     * @param string to be repeated
     * @param i number of times
     * @return
     */
	public static String r(String string, int i) {
		StringBuffer sb = new StringBuffer();
		for(int j=0; j<i; j++) {
			sb.append(string);
		}
		return sb.toString();
	}
}
