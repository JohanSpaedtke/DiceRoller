package se.spaedtke.dice;

import se.spaedtke.dice.gui.GUI;

public class Runner {
	
	public static void main(String args[]) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GUI().setVisible(true);
			}
		});
	}
}
