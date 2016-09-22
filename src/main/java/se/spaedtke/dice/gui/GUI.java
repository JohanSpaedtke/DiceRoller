package se.spaedtke.dice.gui;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;

import se.spaedtke.dice.Constants;
import se.spaedtke.dice.simulator.CountingDiceSimulator;
import se.spaedtke.dice.simulator.DiceNotationParser;
import se.spaedtke.dice.simulator.DiceSimulator;
import se.spaedtke.dice.simulator.MonteCarloDiceSimulator;

@SuppressWarnings("serial")
public class GUI extends JFrame {

	private ChartPanel chart;
	private JPanel controllPanel;
	private List<DiceSimulator> simulations;


	public GUI() {
		super("DiceRoller");
		runInitialSimultaion();
		createComponents();
		createLayout();
        setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
	}

	private void runInitialSimultaion() {
		simulations = Arrays.asList(MonteCarloDiceSimulator.from(DiceNotationParser.diceSpecification(Constants.INITIAL_DICE_SPEC)).build());
	}

	private void createComponents() {
		chart = new Chart(simulations);
		controllPanel = new ControllPanel(chart);
	}

	private void createLayout() {
		add(chart, BorderLayout.WEST);
		add(controllPanel, BorderLayout.EAST);
	}
}