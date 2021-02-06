
package lplay;

import music.LPart;

import org.w3c.dom.Element;

import ren.io.Domable;
import ren.io.Domc;

public abstract class AutoEvent implements Domable {
    
    protected LPart lp;
   
    protected double at;
    
    protected String name;
    
    public AutoEvent() {}
    
    public AutoEvent construct(LPart lp, double at, String name) {
        this.lp = lp;
        this.at = at;
        this.name = name;
        return this;
    }
    
    public abstract boolean play();
    
    public double getAt() {
        return at;
    }
    
    public LPart getLPart() {
        return this.lp;
    }
    
    public String getName() {
        return name;
    }
    
    public void dload(Element e) {
        lp = (LPart)(Domc.lo(Domc.find(e, "lp"), 
            LPart.class, e));
        
        name = e.getAttribute("name");
        
        at = Double.parseDouble(e.getAttribute("at"));
        
    }
    
    public void dsave(Element e) {
        e.appendChild(Domc.sa(lp, "lp", e));
        
        e.setAttribute("name", name);
        
        e.setAttribute("at", String.valueOf(at));
        
    }
    
}