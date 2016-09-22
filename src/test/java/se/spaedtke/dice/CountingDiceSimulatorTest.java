package se.spaedtke.dice;

import org.junit.Test;
import se.spaedtke.dice.simulator.CountingDiceSimulator;
import se.spaedtke.dice.simulator.DiceNotationParser;
import se.spaedtke.dice.simulator.Die;
import se.spaedtke.dice.simulator.MonteCarloDiceSimulator;
import se.spaedtke.dice.simulator.WhichToKeep;

import java.util.Arrays;

/**
 * Created by johan on 20/09/16.
 */
public class CountingDiceSimulatorTest
{
    @Test
    public void foo()
    {
        //CountingDiceSimulator cd = new CountingDiceSimulator(Arrays.asList(new Die(4,4,4,4,0,0), new Die(3,3,3,3,3,3)), 0, WhichToKeep.ALL);
        CountingDiceSimulator cd = new CountingDiceSimulator(Arrays.asList(new Die(6), new Die(6)), -3, WhichToKeep.ALL);
        System.out.println(cd.expectedValue());
        System.out.println(cd.getNormalizedHistogram());

        MonteCarloDiceSimulator md = MonteCarloDiceSimulator.from(DiceNotationParser.diceSpecification("2d6-3")).build();
        System.out.println(md.expectedValue());
        System.out.println(md.getNormalizedHistogram());
    }
}
