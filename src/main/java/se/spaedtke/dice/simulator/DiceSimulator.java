package se.spaedtke.dice.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

public class DiceSimulator {

	private enum WHICH_TO_KEEP {
		HIGH((x, y) -> Integer.compare(y, x)), LOW((x, y) -> Integer.compare(x, y));

		public Comparator<Integer> sortingOrder;

		WHICH_TO_KEEP(Comparator<Integer> sortingOrder) {
			this.sortingOrder = sortingOrder;
		}

		public static WHICH_TO_KEEP fromString(String string) {
			if (string == null || string.isEmpty()) {
				return HIGH;
			}
			switch (string.toLowerCase()) {
			case "l":
			case "low":
			case "lowest":
				return LOW;
			default:
				return HIGH;
			}
		}
	}

	private final int numRuns;
	private final double precission;
	private final int diceToKeep;
	private final int staticBonus;
	private final WHICH_TO_KEEP whichToKeep;
	private final List<Die> dice;
	private Map<Integer, Integer> histogram = new HashMap<>();

	private DiceSimulator(Builder builder) {
		this.dice = builder.dice;
		this.numRuns = builder.numRuns != null ? builder.numRuns : 1000000;
		this.diceToKeep = builder.diceToKeep != null ? builder.diceToKeep : this.dice.size();
		this.whichToKeep = WHICH_TO_KEEP.fromString(builder.whichToKeep);
		this.precission = builder.numDecimalPlaces != null ? Math.pow(10.0, builder.numDecimalPlaces) : Math.pow(10.0, 2);
		this.staticBonus = builder.staticBonus != null ? builder.staticBonus : 0;
	}

	public void roll() {
		for (int i = 0; i < numRuns; i++) {
			Integer roll = dice.stream()
					.map(d -> d.roll())
					.sorted(whichToKeep.sortingOrder)
					.limit(diceToKeep)
					.collect(Collectors.summingInt(x -> x)) + staticBonus;
			histogram.compute(roll, (k, v) -> v != null ? v + 1 : 1);
		}
	}

	public Map<Integer, Integer> getHistogram(){
		return Collections.unmodifiableMap(histogram);
	}

	public Map<Integer, Double> getNormalizedHistogram(){
		Double total = histogram.values().stream().map(Double::new).collect(Collectors.summingDouble(d -> d));
		Map<Integer, Double> normalized = histogram.entrySet().stream().collect(Collectors.toMap(keepKey(), normalize(total)));
		return Collections.unmodifiableMap(normalized);
	}

	private Function<Entry<Integer, Integer>, Integer> keepKey() {
		return e -> e.getKey();
	}

	private Function<Entry<Integer, Integer>, Double> normalize(Double max) {
		return e -> new Double(e.getValue())/max;
	}

	public double expectedValue() {
		int totalSum = histogram.keySet().stream().map(k -> k * histogram.get(k)).collect(Collectors.summingInt(x -> x));
		return round(((double) totalSum) / ((double) numRuns));
	}

	public static Builder with() {
		return new Builder();
	}

	private Double round(double d) {
		return ((double) ((int) Math.floor(d * precission + 0.5))) / precission;
	}

	public static class Builder {
		private List<Die> dice;
		private Integer numRuns;
		private Integer numDecimalPlaces;
		private Integer diceToKeep;
		private Integer staticBonus;
		private String whichToKeep;

		public Builder dice(Die... dice) {
			this.dice = Arrays.asList(dice);
			return this;
		}

		/**
		 * Supported formats are </br> XdN (eg. 4d6) </br> XdN+M (eg. 3d4+3)
		 * </br> XdN,YdM (eg. 2d20,1d6 which would mean roll 2d20 and 1d6 and
		 * add the results) </br> XdN,YdM+S (eg. 2d20,1d6+3) </br>
		 * 
		 */
		public Builder diceSpecification(String diceSpecification) {
			parse(diceSpecification);
			return this;
		}

		private void parse(String diceSpecification) {
			staticBonus = staticBonusFrom(diceSpecification);
			dice = diceFrom(diceSpecification);
			diceToKeep = diceToKeepFrom(diceSpecification);
			whichToKeep = whichToKeepFrom(diceSpecification);
		}

		private Integer staticBonusFrom(String diceSpecification) {
			Pattern p = Pattern.compile(".*([+-]\\d*).*");
			Matcher m = p.matcher(diceSpecification);
			if (m.matches()) {
				return Integer.parseInt(m.group(1));
			}
			return 0;
		}

		private List<Die> diceFrom(String diceSpecification) {
			List<Die> res = new ArrayList<>();
			Pattern p = Pattern.compile(",?(\\d*)[dD](\\d*)(,.*)?(?:[+-]?.*)?");
			Matcher m = p.matcher(diceSpecification);
			if (m.matches()) {
				for (int i = 0; i < Integer.parseInt(m.group(1)); i++) {
					res.add(new Die(Integer.parseInt(m.group(2))));
				}
				if (m.group(3) != null) {
					res.addAll(diceFrom(m.group(3)));
				}
			}
			return res;
		}

		private String whichToKeepFrom(String diceSpecification) {
			Pattern p = Pattern.compile(".* k.* \\d* ?(.*).*");
			Matcher m = p.matcher(diceSpecification);
			if (m.matches()) {
				return m.group(1);
			}
			return null;
		}

		private Integer diceToKeepFrom(String diceSpecification) {
			Pattern p = Pattern.compile(".* k.* (\\d*) .*");
			Matcher m = p.matcher(diceSpecification);
			if (m.matches()) {
				return Integer.parseInt(m.group(1));
			}
			return null;
		}

		public Builder numRuns(int numRuns) {
			this.numRuns = numRuns;
			return this;
		}

		public Builder numDecimalPlaces(int numDecimalPlaces) {
			this.numDecimalPlaces = numDecimalPlaces;
			return this;
		}

		public Builder diceToKeep(int diceToKeep) {
			this.diceToKeep = diceToKeep;
			return this;
		}

		public Builder staticBonus(int staticBonus) {
			this.staticBonus = staticBonus;
			return this;
		}

		public Builder whichToKeep(String whichToKeep) {
			this.whichToKeep = whichToKeep;
			return this;
		}

		public DiceSimulator build() {
			Validate.notNull(dice, "Dice can't be null");
			return new DiceSimulator(this);
		}
	}
}
