package se.spaedtke.dice.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionListener;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
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

	public static ChartPanel createChart(List<DiceSimulator> simulations) {
		ChartPanel chart = new ChartPanel(barChart(simulations)) {

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(800, 600);
			}
		};
		chart.setMouseWheelEnabled(true);
		return chart;
	}

	public static JFreeChart barChart(List<DiceSimulator> simulations) {
		final CategoryDataset ds = createDataset(simulations.stream().map(sim -> sim.getNormalizedHistogram()).collect(Collectors.toList()));

		JFreeChart barChart = ChartFactory.createBarChart("Probabilities", "Combined dice total", "Percent of rolls", ds, PlotOrientation.VERTICAL, true, true, false);

		NumberAxis mainAxis = (NumberAxis) barChart.getCategoryPlot().getRangeAxis();

		mainAxis.setLowerBound(0);
		mainAxis.setUpperBound(Math.ceil(100 * maximumPercentage(simulations)) / 100);

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
		
		BarRenderer renderer = (BarRenderer) barChart.getCategoryPlot().getRenderer();
		renderer.setBarPainter(new StandardBarPainter());

		LegendItemCollection chartLegend = new LegendItemCollection();
		Shape shape = new Rectangle(10, 10);
		for(int i = 0; i < simulations.size(); i++){
			Color color = new Color((32*i)%256, (32*5*i)%256, (32*10*i)%256);
			renderer.setSeriesPaint(i, color);
			chartLegend.add(new LegendItem(simulations.get(i).specification(), null, "Expected value: " + simulations.get(i).expectedValue(), null, shape, color));
		}		
		barChart.getCategoryPlot().setFixedLegendItems(chartLegend);
		TextTitle legendText = new TextTitle("Some nice text goes here");
		legendText.setPosition(RectangleEdge.TOP);
		barChart.addSubtitle(legendText);
		
		return barChart;
	}

	private static Double maximumPercentage(List<DiceSimulator> simulations) {
		return simulations.stream()
				.flatMap(toPercentages())
				.max((x, y) -> Double.compare(x, y))
				.map(Double::new)
				.get();
	}

	private static Function<DiceSimulator, Stream<Double>> toPercentages() {
		return sim -> sim.getNormalizedHistogram().values().stream();
	}

	private static CategoryDataset createDataset(List<Map<Integer, Double>> histograms) {		
		Integer maxNumBuckets = histograms.stream().map(m -> m.keySet().size()).max((x,y) -> Integer.compare(x,y)).orElse(0);
		final Double[][] data = new Double[histograms.size()][maxNumBuckets];
		for(int i = 0; i < histograms.size(); i++){
			data[i] = paddedDataFrom(histograms.get(i), maxNumBuckets);
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
