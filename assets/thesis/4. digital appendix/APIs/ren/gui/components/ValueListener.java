/**
 * ValueListener
 * this class is simply a bridge from the value generators to the parameters
 * which means that the parameters don't need to implement adjustmentlistener
 * changelistener, actionlistener, statechangedlistener or any of the other 
 * listeners to get the value.  All they need to do is implement ValueListener
 * and the conversion from whatever local listener type to ValueListener is done
 * in the ValueGenerator component itself.
 * 
 * In situations where the ParamListener is getting data from the ValueListener
 * which is getting data from whatever local listener it may seem a bit silly,
 * but you should bear in mind that the ParamListener takes the data through
 * the remapping that is associated with that parameter.
 * 
 * The valueListener on the other hand is taking the data in it's raw integer
 * form
 */
package ren.gui.components;

public interface ValueListener {
    public void valueGeneratorUpdate(int rv);
}
