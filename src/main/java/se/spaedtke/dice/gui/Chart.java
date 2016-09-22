package se.spaedtke.dice.gui;

import java.awt.Dimension;
import java.util.List;

import org.jfree.chart.ChartPanel;

import se.spaedtke.dice.simulator.DiceSimulator;
import se.spaedtke.dice.simulator.MonteCarloDiceSimulator;

@SuppressWarnings("serial")
public class Chart extends ChartPanel{
	
	public Chart(List<DiceSimulator> simulations) {
		super(ComponentFactory.barChart(simulations));
		setMouseWheelEnabled(true);
		setDomainZoomable(true);
		setPreferredSize(new Dimension(800, 600));
	}
}
