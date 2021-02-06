/*
 * Created on 3/10/2005
 *
 * @author Rene Wooller
 */
package ren.util;

public class KeyItem {

    String key;
    
    Object item;
    
    public KeyItem(String key, Object item) {
        this.key = key;
        this.item = item;
    }
    
    public String getKey() {
        return key;
    }
    
    public Object getItem() {
        return item;
    }

    public int [] getIntArr() {
        if(item instanceof int[]) {
            return ((int[])item);
        } else {
            try {
                Exception ex = new Exception("getIntArr called and the key isn't an int []");
                ex.fillInStackTrace();
                throw ex;
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }
            
    }
    
    public boolean isKey(String q) {
        if(key.equalsIgnoreCase(q))
            return true;
        else
            return false;
                
    }
    
}
