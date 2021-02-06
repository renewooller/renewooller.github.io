/*
 * Created on 9/07/2005
 *
 * @author Rene Wooller
 */
package ren.util;

/**
 * @author wooller
 *
 *9/07/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class Err {

    public static void e(String err) {
        try {
            Error e = new Error(err);
            e.fillInStackTrace();
            throw e;
        } catch(Error e) {
            e.printStackTrace();
        }
    }
}
