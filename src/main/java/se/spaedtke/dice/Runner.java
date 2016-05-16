package se.spaedtke.dice;

import java.util.ArrayList;
import java.util.List;

import se.spaedtke.dice.gui.GUI;

public class Runner {

	private final static String INITIAL_DICE_SPEC = "4d6,1d20 keep 3 highest";
	private final static List<String> RADIO_BUTTON_ALTERNATIVES;
	static{
		RADIO_BUTTON_ALTERNATIVES = new ArrayList<String>();
		RADIO_BUTTON_ALTERNATIVES.add("1d6");
		RADIO_BUTTON_ALTERNATIVES.add("2d6");
		RADIO_BUTTON_ALTERNATIVES.add("3d6");
		RADIO_BUTTON_ALTERNATIVES.add("4d6 keep 3 highest");
	}
	
	public static void main(String args[]) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GUI(INITIAL_DICE_SPEC, RADIO_BUTTON_ALTERNATIVES).setVisible(true);
			}
		});
	}
}
