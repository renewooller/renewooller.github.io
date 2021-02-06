/*
 * Created on 28/10/2005
 *
 * @author Rene Wooller
 */

public class BoolTest {

    public static void main(String [] args) {
        String bool = "true";
        boolean boolShit = Boolean.getBoolean(bool);
        System.out.println("bool = " + bool + " boolShit = " + boolShit);
        // use parseBoolean instead!!!
    }

}
