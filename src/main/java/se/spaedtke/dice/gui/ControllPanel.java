package se.spaedtke.dice.gui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartPanel;

import se.spaedtke.dice.Constants;
import se.spaedtke.dice.simulator.DiceSimulator;

@SuppressWarnings("serial")
public class ControllPanel extends JPanel {

	private static final int MIN_NUM_ROLLS = 0;
	private static final int MAX_NUM_ROLLS = 7;
	private static final int DEFAULT_NUM_ROLLS = 5;
	
	private final JSlider slider;

	public ControllPanel(ChartPanel chart) {
		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		slider = createSlider();
		SimulationSpecification textField = createTextInput(Constants.INITIAL_DICE_SPEC);
		JPanel rerunButton = createRerunButton(e -> {
			update(chart, newSimulations(textField.getDiceSpecification().orElse("1d6")));
			chart.repaint();
		});
		setLayout(boxLayout);
		add(slider);
		add(textField);
		add(rerunButton);
	}

	private JSlider createSlider() {
		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, MIN_NUM_ROLLS, MAX_NUM_ROLLS, DEFAULT_NUM_ROLLS);
		slider.setPaintLabels(true);
		slider.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
		for (int i = 0; i <= MAX_NUM_ROLLS; i++) {
			table.put(i, new JLabel("10^" + i));
		}
		slider.setLabelTable(table);
		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);

		return slider;
	}

	public static JPanel createRerunButton(ActionListener listener) {
		JPanel buttonPanel = new JPanel();
		JButton rerunButton = new JButton("Run custom simulation");
		rerunButton.addActionListener(listener);
		buttonPanel.add(rerunButton, BorderLayout.CENTER);
		return buttonPanel;
	}

	public static SimulationSpecification createTextInput(String initialText) {
		return new SimulationSpecification(initialText);
	}

	private List<DiceSimulator> newSimulations(String diceSpecifications) {
		String[] specifications = diceSpecifications.split(" *; *");
		return Arrays.asList(specifications).stream().map(toDiceSimulator()).collect(Collectors.toList());
	}

	private Function<String, DiceSimulator> toDiceSimulator() {
		return string -> DiceSimulator.with().diceSpecification(string).numRuns(new Double(Math.pow(10, slider.getValue())).intValue()).build();
	}

	private static void update(ChartPanel chart, List<DiceSimulator> simulations) {
		chart.setChart(ComponentFactory.barChart(simulations));
	}

}
