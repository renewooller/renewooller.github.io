package ren.io;
import org.w3c.dom.Element;

/*
 * Created on 24/07/2005
 *
 * @author Rene Wooller
 */

public interface Domable {

    public void dload(Element e);
    
    public void dsave(Element e);
    
}
