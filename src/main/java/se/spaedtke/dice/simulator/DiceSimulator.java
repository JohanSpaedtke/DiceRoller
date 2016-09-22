package se.spaedtke.dice.simulator;

import java.util.Map;

/**
 * Created by johan on 21/09/16.
 */
public interface DiceSimulator
{
    String getSpecification();

    Map<Integer, Double> getNormalizedHistogram();

    double expectedValue();
}
