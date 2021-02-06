/*
 * Created on 3/10/2005
 *
 * @author Rene Wooller
 */
package ren.util;

import java.util.Vector;

public class VectorMap {

    Vector vec = new Vector(20);
    
    public VectorMap() {
        super();
    }
    
    public void add(String key, Object item) {
        KeyItem ki = new KeyItem(key, item);
        vec.add(ki);
    }
    
    public Object get(String key) {
        int index = find(key);
        if(index == -1)
            return null;
        else
            return ((KeyItem)vec.get(index)).getItem();
        
    }
    
    public int find(String key) {
        Object [] kis = vec.toArray();
        for(int i=0; i< kis.length; i++) {
            if(((KeyItem)kis[i]).isKey(key)) {
                return i;
            }
        }
        return -1;
    }
    
    public String [] getKeys() {
        String [] keys = new String [vec.size()];
        for(int i=0; i< vec.size(); i++) {
            keys[i] = ((KeyItem)vec.get(i)).getKey();
        }
        return keys;
    }

    public int[] get(int scaleType) {
        return ((KeyItem)vec.get(scaleType)).getIntArr();
    }
    
    public int size() {
    	return vec.size();
    }
    
    
}
