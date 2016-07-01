package se.spaedtke.dice.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jfree.chart.ChartFactory;
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
	
	public static JFreeChart barChart(List<DiceSimulator> simulations) {
		final CategoryDataset ds = createDataset(simulations);

		JFreeChart barChart = ChartFactory.createBarChart("Probabilities", "Combined dice total", "Percent of rolls", ds, PlotOrientation.VERTICAL, true, true, false);

		NumberAxis mainAxis = (NumberAxis) barChart.getCategoryPlot().getRangeAxis();

		mainAxis.setLowerBound(0);
		Double maxPercentageInData = maximumPercentage(simulations);
		mainAxis.setUpperBound(maxPercentageInData + maxPercentageInData/10);
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
		for (int i = 0; i < simulations.size(); i++) {
			Color color = new Color((32 * i) % 256, (32 * 5 * i) % 256, (32 * 10 * i) % 256);
			renderer.setSeriesPaint(i, color);
			chartLegend.add(new LegendItem(simulations.get(i).getSpecification(), null, "Expected value: " + simulations.get(i).expectedValue(), null, shape, color));
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

	private static CategoryDataset createDataset(List<DiceSimulator> simulations) {
		Integer maxNumBuckets = simulations.stream().map(sim -> sim.getNormalizedHistogram()).flatMap(m -> m.keySet().stream()).max((x, y) -> Integer.compare(x, y)).orElse(0);
		final double[][] data = new double[simulations.size()][maxNumBuckets];
		for (int i = 0; i < simulations.size(); i++) {
			data[i] = paddedDataFrom(simulations.get(i).getNormalizedHistogram(), maxNumBuckets);
		}
		return DatasetUtilities.createCategoryDataset(rowKeys(simulations), columnKeys(maxNumBuckets), data);
	}

	private static String[] rowKeys(List<DiceSimulator> simulations){
		final String[] rowKeys = new String[simulations.size()];		
		for (int i = 0; i < simulations.size(); i++) {
			rowKeys[i] = simulations.get(i).getSpecification();			
		}
		return rowKeys;
	}
	
	private static Integer[] columnKeys(Integer maxNumBuckets) {
		final Integer[] columnKeys = new Integer[maxNumBuckets];
		for(int i = 0; i < maxNumBuckets; i++){
			columnKeys[i] = i+1;
		}
		return columnKeys;
	}

	private static double[] paddedDataFrom(Map<Integer, Double> histogram, Integer maxNumBuckets) {
		double[] data = new double[maxNumBuckets];
		for (int i = 0; i < data.length; i++) {
			data[i] = 0.0;
		}
		List<Integer> index = new ArrayList<>(histogram.keySet());
		List<Double> values = new ArrayList<>(histogram.values());
		for (int i = 0; i < values.size(); i++) {
			data[index.get(i)-1] = values.get(i);
		}
		return data;
	}

	private static String asPercent(Double fraction) {
		Double percent = 100 * fraction;
		return String.format("%.2f%%", percent);
	}

}
