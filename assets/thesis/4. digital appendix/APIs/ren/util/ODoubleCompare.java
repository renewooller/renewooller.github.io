/*
 * Created on 27/10/2005
 *
 * @author Rene Wooller
 */
package ren.util;

import java.util.Comparator;

public class ODoubleCompare implements Comparator {

    public ODoubleCompare() {
        super();
        // TODO Auto-generated constructor stub
    }

    public int compare(Object a, Object b) {
        if (a instanceof ODouble && b instanceof ODouble) {

            
            ODouble ao = (ODouble)a;
            ODouble bo = (ODouble)b;
            if (ao.v == bo.v)
                return 0;
            else if (ao.v < bo.v) {
                return -1;
            } else {
                return 1;
            }
        } else {
            ClassCastException ce = new ClassCastException();
            ce.fillInStackTrace();
            throw ce;
        }
       
    }

}
