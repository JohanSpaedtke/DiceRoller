package se.spaedtke.dice;

import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import org.junit.Test;

import se.spaedtke.dice.simulator.DiceSimulator;

public class DiceSimulatorTest {

	@Test
	public void dummy(){
		for(int i = 0; i < 1; i++){
			DiceSimulator d = DiceSimulator.with().diceSpecification("4d6,1d20 keep 3 H").build();
			System.out.println(d.expectedValue());
			Map<Integer, Double> hist = d.getNormalizedHistogram();
			System.out.println(hist);
			System.out.println(hist.values().stream().collect(Collectors.summingDouble(idntity())));
		}
	}

	private ToDoubleFunction<Object> idntity() {
		return o -> (Double) o;
	}
}
