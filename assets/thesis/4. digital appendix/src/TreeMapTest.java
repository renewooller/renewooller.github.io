import java.util.Iterator;
import java.util.TreeMap;

import ren.util.ODouble;
import ren.util.ODoubleCompare;
import ren.util.PO;

/*
 * Created on 27/10/2005
 *
 * @author Rene Wooller
 */

public class TreeMapTest {

    public static void main(String [] args) {
        TreeMap  tm = new TreeMap(new ODoubleCompare());
        String [] str = new String [10];
        ODouble [] od = new ODouble [10];
        for(int i=0; i< str.length; i++) {
            str[i] = String.valueOf(i);
            od[i] = new ODouble();
            od[i].v = i*1.0;
            tm.put(od[i], str[i]);
        }
        
        Iterator iter = tm.values().iterator();
        while(iter.hasNext()) {
            String nstr = iter.next().toString();
            if(nstr.equals("5")) {
                tm.put((new ODouble()).construct(5.5), "5.5");
            }
                
            PO.p(nstr);
        }
        
    }
    
    public TreeMapTest() {
        super();
        // TODO Auto-generated constructor stub
    }

}
