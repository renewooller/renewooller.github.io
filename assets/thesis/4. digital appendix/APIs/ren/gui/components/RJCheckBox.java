/**
 *  this is an implementation of a checkbox that actually looks good.
 */
package ren.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JCheckBox;

import ren.gui.lookFeel.CustomColors;

public class RJCheckBox extends JCheckBox {

    public static final int PLAIN = 0;

    public static final int GREEN = 1;

    public static final int RED = 2;

    public RJCheckBox() {
        super();
        init(0);
    }

    public RJCheckBox(String text) {
        this(text, false);
    }

    public RJCheckBox(String text, boolean sel) {
        this(text, sel, 0);
    }

    public RJCheckBox(String text, boolean sel, int type) {
        super(text, sel);
        init(type);
    }

    private void init(int type) {
        if (type == PLAIN) {
            this.setDisabledIcon(new DisabledPlainBoxIcon());
            this.setIcon(new UnselectedPlainBoxIcon());
            this.setSelectedIcon(new SelectedPlainBoxIcon());
            this.setIconTextGap(0);
        } else if (type == GREEN) {
            this.setDisabledIcon(new ColoredIcon(CustomColors.dark));
            this.setIcon(new ColoredIcon(CustomColors.unselectedGreen));
            this.setSelectedIcon(new ColoredIcon(CustomColors.selectedGreen));
            this.setIconTextGap(0);
        } else if (type == RED) {
            this.setIcon(new ColoredIcon(CustomColors.unselectedRed));
            this.setSelectedIcon(new ColoredIcon(CustomColors.selectedRed));
            this.setIconTextGap(0);
        }
    }
}

class DisabledPlainBoxIcon implements Icon {
    private int height;

    private int width;

    public int getIconHeight() {
        return height;
    }

    public int getIconWidth() {
        return width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        int height = c.getBounds().height;// +10;
        int width = c.getBounds().width;// + 10;

        g.setColor(CustomColors.selectedPlain.darker());
        g.fillRect(3, 3, width - 6, height - 6);

        g.setColor(new Color(0, 0, 30).darker());
        g.drawRect(0, 0, width - 1, height - 1);
        g.setColor(new Color(90, 90, 100).darker());
        g.drawRect(1, 1, width - 3, height - 3);
        g.setColor(new Color(180, 150, 210).darker());
        g.drawRect(2, 2, width - 5, height - 5);
    }
}

class SelectedPlainBoxIcon implements Icon {
    private int height;

    private int width;

    public int getIconHeight() {
        return height;
    }

    public int getIconWidth() {
        return width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        int height = c.getBounds().height;// +10;
        int width = c.getBounds().width;// + 10;

        g.setColor(CustomColors.selectedPlain);
        g.fillRect(3, 3, width - 6, height - 6);

        g.setColor(new Color(0, 0, 30));
        g.drawRect(0, 0, width - 1, height - 1);
        g.setColor(new Color(90, 90, 100));
        g.drawRect(1, 1, width - 3, height - 3);
        g.setColor(new Color(180, 150, 210));
        g.drawRect(2, 2, width - 5, height - 5);
    }
}

class UnselectedPlainBoxIcon implements Icon {
    private int height;

    private int width;

    public int getIconHeight() {
        return height;
    }

    public int getIconWidth() {
        return width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        int height = c.getBounds().height;// +10;
        int width = c.getBounds().width;// + 10;

        g.setColor(CustomColors.unselectedPlain);
        g.fillRect(3, 3, width - 6, height - 6);

        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, width - 1, height - 1);
        g.setColor(new Color(40, 40, 60));
        g.drawRect(1, 1, width - 3, height - 3);
        g.setColor(new Color(70, 70, 90));
        g.drawRect(2, 2, width - 5, height - 5);
    }
}
