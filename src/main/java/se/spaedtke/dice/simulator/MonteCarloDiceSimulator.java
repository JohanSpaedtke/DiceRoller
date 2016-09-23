package se.spaedtke.dice.simulator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

public class MonteCarloDiceSimulator implements DiceSimulator
{

	private final Integer numRuns;
	private final Double precission;
	private final Integer diceToKeep;
	private final Integer staticBonus;
	private final WhichToKeep whichToKeep;
	private final List<Die> dice;
	private Map<Integer, Integer> histogram = new HashMap<>();

	private MonteCarloDiceSimulator(Builder builder) {
		this.dice = builder.dice;
		this.numRuns = builder.numRuns != null ? builder.numRuns : 1000000;
		this.diceToKeep = builder.diceToKeep != null ? builder.diceToKeep : this.dice.size();
		this.whichToKeep = builder.whichToKeep;
		this.precission = builder.numDecimalPlaces != null ? Math.pow(10.0, builder.numDecimalPlaces) : Math.pow(10.0, 2);
		this.staticBonus = builder.staticBonus != null ? builder.staticBonus : 0;
		roll();
	}

	private void roll() {
		for (int i = 0; i < numRuns; i++) {
			Integer roll = dice.stream()
					.map(d -> d.roll())
					.sorted(whichToKeep.sortingOrder)
					.limit(diceToKeep)
					.collect(Collectors.summingInt(x -> x)) + staticBonus;
			histogram.compute(roll, (k, v) -> v != null ? v + 1 : 1);
		}
	}

	@Override
	public String getSpecification(){
		StringBuilder sb = new StringBuilder();
		sb.append(String.join(",", dice.stream().collect(Collectors.groupingBy(Die::getNumFaces)).entrySet().stream().map(e -> e.getValue().size() + "d" + e.getKey()).collect(Collectors.toList())));
		if(staticBonus != 0){
			sb.append(Math.signum(staticBonus) + staticBonus);
		}
		if(diceToKeep != dice.size()){
			sb.append(" keep " + diceToKeep + " " + whichToKeep.toString());
		}
		return sb.toString();
	}
	
	public Map<Integer, Integer> getHistogram(){
		return Collections.unmodifiableMap(histogram);
	}

	@Override
	public Map<Integer, Double> getNormalizedHistogram(){
		Double total = histogram.values().stream().map(Double::new).collect(Collectors.summingDouble(d -> d));
		Map<Integer, Double> normalized = histogram.entrySet().stream().collect(Collectors.toMap(keepKey(), normalize(total)));
		return Collections.unmodifiableMap(normalized);
	}

	@Override
	public String toString(){
		return getSpecification();
	}
	private Function<Entry<Integer, Integer>, Integer> keepKey() {
		return e -> e.getKey();
	}

	private Function<Entry<Integer, Integer>, Double> normalize(Double max) {
		return e -> new Double(e.getValue())/max;
	}

	@Override
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

	public static MonteCarloDiceSimulator.Builder from(SimulationSpecification spec){
        return new Builder()
                .dice(spec.getDice())
                .diceToKeep(spec.getDiceToKeep())
                .staticBonus(spec.getStaticBonus())
                .whichToKeep(spec.getWhichToKeep());
    }

	public static class Builder {
		private List<Die> dice;
		private Integer numRuns;
		private Integer numDecimalPlaces;
		private Integer diceToKeep;
		private Integer staticBonus;
		private WhichToKeep whichToKeep;

		public Builder dice(Die... dice) {
			this.dice = Arrays.asList(dice);
			return this;
		}

        public Builder dice(List<Die> dice) {
            this.dice = dice;
            return this;
        }

        public Builder numRuns(Integer numRuns) {
			this.numRuns = numRuns;
			return this;
		}

		public Builder numDecimalPlaces(int numDecimalPlaces) {
			this.numDecimalPlaces = numDecimalPlaces;
			return this;
		}

		public Builder diceToKeep(Integer diceToKeep) {
			this.diceToKeep = diceToKeep;
			return this;
		}

		public Builder staticBonus(Integer staticBonus) {
			this.staticBonus = staticBonus;
			return this;
		}

		public Builder whichToKeep(WhichToKeep whichToKeep) {
			this.whichToKeep = whichToKeep;
			return this;
		}

		public MonteCarloDiceSimulator build() {
			Validate.notNull(dice, "Dice can't be null");
			return new MonteCarloDiceSimulator(this);
		}
	}
}
