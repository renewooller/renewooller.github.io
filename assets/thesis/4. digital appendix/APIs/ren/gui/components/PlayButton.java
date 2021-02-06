package ren.gui.components;

import javax.swing.JCheckBox;
import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import ren.util.Playable;

public class PlayButton extends JCheckBox implements ActionListener {

	private transient Playable player;

	public PlayButton() {
		super();
	}

	public PlayButton construct(Playable player) {

		this.player = player;
		this.setIcon(new PlayIcon());
		this.setSelectedIcon(new StopIcon());
		this.addActionListener(this);
		return this;
	}
	
	public void setPlayer(Playable player) {
		this.player = player;
	}

	public void actionPerformed(ActionEvent e) {
		//	System.out.println("action performed = " +
		// ((JCheckBox)e.getSource()).isSelected());

		if (((JCheckBox) e.getSource()).isSelected()) {
			player.go();
		} else {
			player.stop();
		}
	}
}

class PlayIcon implements Icon {
	private int height, width;

	public PlayIcon() {
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		int height = c.getBounds().height;
		int width = c.getBounds().width;

		g.setColor(Color.GREEN);
		g.fillPolygon(new int[] { (int) (0), width, (int) (0) }, new int[] { 0,
				(int) (height * 0.5), height }, 3);
		g.dispose();

	}

	public int getIconHeight() {
		return height;
	}

	public int getIconWidth() {
		return width;
	}

}

class StopIcon implements Icon {
	private int height, width;

	public StopIcon() {
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		int height = c.getBounds().height;
		int width = c.getBounds().width;

		g.setColor(Color.RED);
		g.fillRect(0, 0, width, height);
		//	g.fillRect((int)(width*0.15),(int)(height*0.15),
		//	   (int)(width*1.0-width*3.0), (int)(height*1.0-height*3.0));
		g.dispose();

	}

	public int getIconHeight() {
		return height;
	}

	public int getIconWidth() {
		return width;
	}

}