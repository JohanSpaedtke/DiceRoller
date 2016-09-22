package se.spaedtke.dice;

import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import org.junit.Test;

import se.spaedtke.dice.simulator.DiceNotationParser;
import se.spaedtke.dice.simulator.MonteCarloDiceSimulator;

public class MonteCarloDiceSimulatorTest
{

	@Test
	public void dummy(){
		for(int i = 0; i < 1; i++){
			MonteCarloDiceSimulator d = MonteCarloDiceSimulator.from(DiceNotationParser.diceSpecification("4d6,1d20 keep 3 H")).build();
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
