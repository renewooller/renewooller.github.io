/*
 * Save.java
 *
 * Created on 27 August 2003, 20:17
 */

package ren.util;

import jm.music.data.*;
import java.util.Enumeration;
/**
 *  Utilities for retrieving and saving using strings
 *
 * @author  Rene Wooller
 */
public class Save {
    
    public static final String ld = "\"";
    public static final String rd = "\"";
    

    public static String st(String s) {
        return "<" + s + ">";
    }
    public static String et(String s) {
        return "</" + s + ">";
    }
	
    
	/**
	 * Gets the start and end location of the value for the variable var
	 * 
	 * eg if the variable structure is
	 *  var"1000"
	 * it return the  start and end location of 1000
	 * 
	 * if [0] == -1 then the variable var is not in the string from
	 * if [1]-[0] <= 1 then there is no value for the variable
	 * 
	 * @param from
	 * @param var
	 * @param leftDelimit
	 * @param rightDelimit
	 * @return pos [0] is the index of the start of the substring of the value of the variable, 
	 * pos[1] is the end
	 * 
	 */
	public static int [] getStartEndPos(String from, String var, String leftDelimit, String rightDelimit) 
    	 {
        int pos = 0;
        int endPos = 0;
        if(var != null)
            pos = from.indexOf(var);
        
        pos = from.indexOf(leftDelimit, pos);
        // if the left delimit actually exists, start at the position just after it
        if(pos != -1)
            pos = pos + leftDelimit.length();
        
        endPos = from.indexOf(rightDelimit, pos); 
        
        return new int [] {pos, endPos};
    }
	
	public static int [] getStartEndPos(String from, String var) {
		return getStartEndPos(from, var, ld, rd);
	}
	
	/**
	 * gets the start and end location for the substring in between
	 * <var> .... </var>
	 * for the string from
	 */
	public static int [] getSubPosXML(String from, String var) {
	    return getStartEndPos(from, null, "<"+var+">", "</" + var + ">");
	}
	
    
    
    /**
     *  
     *
    */
    public static double getDoubleVar(String from, String var) {   
        double toRet = -1.0;     
        int [] loc = getStartEndPos(from, var);
        if(loc == null)
            return toRet;
        try{
            String sub = from.substring(loc[0], loc[1]);
           // System.out.println(var + "--- sub " + sub);
            toRet = Double.parseDouble(sub);
        } catch(java.lang.Exception ex) {
            System.out.println(" problem with variable " + var + "\n" +
                                "from " + from);
            ex.printStackTrace();
        }
     //   System.out.println(toRet);
        return toRet;
    }
    
    public static double getDoubleXML(String from, String var) {
        double toRet = -Double.MAX_VALUE;     
        int [] loc = getSubPosXML(from, var);
        if(loc == null || loc[0] == -1)
            return toRet;
        
        try{
            toRet = Double.parseDouble(from.substring(loc[0], loc[1]));
        } catch(java.lang.Exception ex) {
            ex.printStackTrace();
        }
        return toRet;
    }

    public static int getIntVar(String from, String var) {
        int toRet = -1;     
        int [] loc = getStartEndPos(from, var);
        if(loc == null)
            return toRet;
        
        try{
            toRet = Integer.parseInt(from.substring(loc[0], loc[1]));
        } catch(java.lang.Exception ex) {
            ex.printStackTrace();
        }
        return toRet;

    }
    
    public static int getIntXML(String from, String var) {
        int toRet = Integer.MIN_VALUE;     
        int [] loc = getSubPosXML(from, var);
        if(loc == null || loc[0] == -1)
            return toRet;
        
        try{
            toRet = Integer.parseInt(from.substring(loc[0], loc[1]));
        } catch(java.lang.Exception ex) {
            System.out.println("from = " + from);
            System.out.println("var = " + var);
            ex.printStackTrace();
        }
        return toRet;
    }
    
    /**
     *  gets the string inbetween the tags of the var given
     * @param from
     * @param var
     * @return
     */
    public static String getStringXML(String from, String var) {
        int [] loc = getSubPosXML(from, var);
        return from.substring(loc[0], loc[1]);     
        
    }
    public static String getStringVar(String from, String var) {
        int [] loc = getStartEndPos(from, var);
        return from.substring(loc[0], loc[1]);     
        
    }
    
     public static void intoString(String n, int v, StringBuffer into) {
         into.append("," + n);
         into.append(ld+Integer.toString(v) + rd);
     }
     
     public static void intoString(String n, double v, StringBuffer into) {
         into.append(n);
         into.append(ld+Double.toString(v) + rd);
     }
}
