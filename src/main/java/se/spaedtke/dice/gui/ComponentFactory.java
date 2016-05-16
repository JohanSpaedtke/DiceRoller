package se.spaedtke.dice.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleEdge;

import se.spaedtke.dice.simulator.DiceSimulator;

@SuppressWarnings("serial")
public class ComponentFactory {

	public static ButtonsPanel createRadioButtons(List<String> diceSpecifications, ActionListener listener) {
		ButtonsPanel buttons = new ButtonsPanel(diceSpecifications, listener);
		return buttons;
	}

	public static JPanel createRerunButton(ActionListener listener) {
		JPanel buttonPanel = new JPanel();
		JButton rerunButton = new JButton("Rerun custom simulation");
		rerunButton.addActionListener(listener);
		buttonPanel.add(rerunButton, BorderLayout.CENTER);
		return buttonPanel;
	}

	public static JTextField createTextInput(Optional<String> initialText) {
		JTextField textInput = new JTextField(30);
		textInput.setText(initialText.orElse(""));
		textInput.setMaximumSize(textInput.getPreferredSize());
		return textInput;
	}

	public static ChartPanel createChart(DiceSimulator... simulations) {
		ChartPanel chart = new ChartPanel(barChart(simulations)) {

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(800, 600);
			}
		};
		chart.setMouseWheelEnabled(true);
		return chart;
	}

	public static JFreeChart barChart(DiceSimulator... simulation) {
        final CategoryDataset ds = createDataset(simulation.getNormalizedHistogram());

		JFreeChart barChart = ChartFactory.createBarChart("Probabilities", "Combined dice total", "Percent of rolls", ds, PlotOrientation.VERTICAL, false, false, false);

		BarRenderer renderer = (BarRenderer) barChart.getCategoryPlot().getRenderer();
		simulation.getNormalizedHistogram().entrySet().forEach(e -> renderer.setSeriesPaint(e.getKey(), Color.red));

		NumberAxis mainAxis = (NumberAxis) barChart.getCategoryPlot().getRangeAxis();

		mainAxis.setLowerBound(0);
		mainAxis.setUpperBound(Math.ceil(100 * simulation.getNormalizedHistogram().values().stream().max((x, y) -> Double.compare(x, y)).map(Double::new).get()) / 100);

		mainAxis.setNumberFormatOverride(new NumberFormat() {

			@Override
			public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
				return new StringBuffer(asPercent(number));
			}

			@Override
			public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
				return new StringBuffer(String.format("%s", number));
			}

			@Override
			public Number parse(String source, ParsePosition parsePosition) {
				return null;
			}
		});
		TextTitle legendText = new TextTitle("Expected value: " + simulation.expectedValue());
		legendText.setPosition(RectangleEdge.TOP);
		barChart.addSubtitle(legendText);
		return barChart;
	}
	
	@SafeVarargs
	private static CategoryDataset createDataset(Map<Integer, Double>... histograms) {		
		Integer maxNumBuckets = Arrays.asList(histograms).stream().map(m -> m.keySet().size()).max((x,y) -> Integer.compare(x,y)).orElse(0);
        final Double[][] data = new Double[histograms.length][maxNumBuckets];
        for(int i = 0; i < histograms.length; i++){
        	data[i] = paddedDataFrom(histograms[i], maxNumBuckets);
        }
        return DatasetUtilities.createCategoryDataset("Series ", "Factor ", data);
    }

	private static Double[] paddedDataFrom(Map<Integer, Double> histogram, Integer maxNumBuckets) {
		Double[] data = new Double[maxNumBuckets];
		for(int i = 0; i < data.length; i++){
			data[i] = 0.0;
		}
		List<Double> values = new ArrayList<>(histogram.values());
		for(int i = 0; i < values.size(); i++){
			data[i] = values.get(i);
		}
		return data;
	}

	private static String asPercent(Double fraction) {
		Double percent = 100 * fraction;
		return String.format("%.2f%%", percent);
	}

	public static JPanel createControllPanel() {
		JPanel controllPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(controllPanel, BoxLayout.Y_AXIS);
		controllPanel.setLayout(boxLayout);
		return controllPanel;
	}
}
