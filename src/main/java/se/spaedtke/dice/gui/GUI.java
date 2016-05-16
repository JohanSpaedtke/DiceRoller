package se.spaedtke.dice.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jfree.chart.ChartPanel;

import se.spaedtke.dice.simulator.DiceSimulator;

@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener {

	private JPanel rerunButton;
	private JTextField textInput;
	private ButtonsPanel radioButtons;

	private ChartPanel chart;
	private JPanel controllPanel;
	private List<DiceSimulator> simulators;

	private final String initialDiceSpec;
	private final List<String> diceSpecifications;

	public GUI(String initialDiceSpec, List<String> diceSpecifications) {
		super("DiceRoller");
		this.initialDiceSpec = initialDiceSpec;
		this.diceSpecifications = diceSpecifications;
		runInitialSimultaion();
		createComponents();
		createLayout();
        setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
	}

	private void runInitialSimultaion() {
		simulators = Arrays.asList(DiceSimulator.with().diceSpecification(initialDiceSpec).build());
	}

	private void createComponents() {
		chart = ComponentFactory.createChart(simulators);
		textInput = ComponentFactory.createTextInput(Optional.of(initialDiceSpec));
		rerunButton = ComponentFactory.createRerunButton(this);
		radioButtons = ComponentFactory.createRadioButtons(diceSpecifications, this);
		controllPanel = ComponentFactory.createControllPanel();
	}

	private void createLayout() {
		add(chart, BorderLayout.WEST);
		controllPanel.add(Box.createVerticalStrut(60));
		controllPanel.add(radioButtons);
		controllPanel.add(Box.createVerticalStrut(20));
		controllPanel.add(textInput);
		controllPanel.add(rerunButton);
		controllPanel.add(Box.createVerticalStrut(400));
		add(controllPanel, BorderLayout.EAST);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == rerunButton.getComponent(0)) {
			clearRadioButtons();
			rerunSimulation(textInput.getText());
		}
		if (e.getSource() instanceof JRadioButton) {
			rerunSimulation(((JRadioButton)e.getSource()).getText());
		}
		updateChart();
		repaint();
	}

	private void clearRadioButtons() {
		radioButtons.clearSelection();
	}

	private void rerunSimulation(String diceSpecifications) {
		String[] specifications = diceSpecifications.split(" *; *");
		simulators = Arrays.asList(specifications).stream().map(toDiceSimulator()).collect(Collectors.toList());		
	}

	private Function<String, DiceSimulator> toDiceSimulator() {
		return string -> DiceSimulator.with().diceSpecification(string).build();
	}

	private void updateChart() {
		chart.setChart(ComponentFactory.barChart(simulators));
	}
}