package se.spaedtke.dice.gui;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Function;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class ButtonsPanel extends JPanel{

	private final ButtonGroup group;
	
	public ButtonsPanel(List<String> diceSpecifications, ActionListener listener) {
		super(new GridLayout(0,1, 8, 8));
		group = new ButtonGroup();
		Box box = Box.createVerticalBox();
		diceSpecifications.stream().map(toRadioButton()).forEach(button -> {
			button.addActionListener(listener);
			group.add(button);
			box.add(button);
		});
		group.clearSelection();
		add(box);
	}

	public void clearSelection(){
		group.clearSelection();
	}
	
	private static Function<String, JRadioButton> toRadioButton() {
		return string -> {
			JRadioButton button = new JRadioButton(string);
			button.setActionCommand(string);
			button.setSelected(true);
			return button;
		};
	}
}
