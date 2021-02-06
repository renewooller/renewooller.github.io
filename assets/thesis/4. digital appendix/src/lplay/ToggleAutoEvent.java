package lplay;

import music.LPart;

import org.w3c.dom.Element;

public class ToggleAutoEvent extends AutoEvent {
    
    private boolean val;
    
    public ToggleAutoEvent() {}
    
    public ToggleAutoEvent construct(LPart lp, double at, String name, boolean nval) {
        super.construct(lp, at, name);
        val = nval;
        return this;
    }

    public boolean equals(ToggleAutoEvent e) {
        if(e.getAt() == this.at &&
                e.getLPart() == this.lp &&
                e.getName() == this.name)
            return true;
        else
            return false;
    }
    
    public boolean play() {
        return lp.automate(name, val);
    }

    public void dload(Element e) {
        super.dload(e);
        val = Boolean.getBoolean(e.getAttribute("val"));
    }

    public void dsave(Element e) {
        super.dsave(e);
        e.setAttribute("val", String.valueOf(val));
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("TAE:");
        sb.append("\nat = " + at);
        sb.append("\nname = " + name);
        sb.append("\nlp = " + lp.toString());
        return sb.toString();
    }
    
}
