package se.spaedtke.dice;

import junit.framework.Assert;
import org.junit.Test;
import se.spaedtke.dice.simulator.DiceNotationParser;
import se.spaedtke.dice.simulator.Die;
import se.spaedtke.dice.simulator.SimulationSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.lang.Character.isDigit;

/**
 * Created by johan on 21/09/16.
 */
public class DiceNotationParserTest
{
    private SimulationSpecification sim;

    @Test
    public void shouldParseDiceWithSingleDigitNumberOfSides()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("d4");
        Assert.assertEquals(1, sim.getDice().size());
        Assert.assertEquals("[1, 2, 3, 4]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseDiceWithMultipleDigitNumberOfSides()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("d12");
        Assert.assertEquals(1, sim.getDice().size());
        Assert.assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseDiceWithOneSingleDigitCustomFace()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("d{2}");
        Assert.assertEquals(1, sim.getDice().size());
        Assert.assertEquals("[2]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseDiceWithOneMultipleDigitCustomFace()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("d{22}");
        Assert.assertEquals(1, sim.getDice().size());
        Assert.assertEquals("[22]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseDiceWithSingleDigitCustomFaces()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("d{2, 2, 3}");
        Assert.assertEquals(1, sim.getDice().size());
        Assert.assertEquals("[2, 2, 3]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseDiceWithMultipleDigitCustomFaces()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("d{12, 23, 13}");
        Assert.assertEquals(1, sim.getDice().size());
        Assert.assertEquals("[12, 23, 13]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseDiceWithMixedDigitCustomFaces()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("d{1, 231, 13}");
        Assert.assertEquals(1, sim.getDice().size());
        Assert.assertEquals("[1, 231, 13]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseSingleDigitNumberDiceWithSingleDigitNumberOfSides()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("3d6");
        Assert.assertEquals(3, sim.getDice().size());
        Assert.assertEquals("[1, 2, 3, 4, 5, 6]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseSingleDigitNumberDiceWithMultipleDigitNumberOfSides()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("2d12");
        Assert.assertEquals(2, sim.getDice().size());
        Assert.assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseSingleDigitNumberDiceWithOneSingleDigitCustomFace()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("2d{4}");
        Assert.assertEquals(2, sim.getDice().size());
        Assert.assertEquals("[4]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseSingleDigitNumberDiceWithOneMultipleDigitCustomFace()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("2d{12}");
        Assert.assertEquals(2, sim.getDice().size());
        Assert.assertEquals("[12]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseSingleDigitNumberDiceWithSingleDigitCustomFaces()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("2d{1,2,3}");
        Assert.assertEquals(2, sim.getDice().size());
        Assert.assertEquals("[1, 2, 3]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseSingleDigitNumberDiceWithMultipleDigitCustomFaces()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("2d{12,22,32}");
        Assert.assertEquals(2, sim.getDice().size());
        Assert.assertEquals("[12, 22, 32]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseSingleDigitNumberDiceWithMixedDigitCustomFaces()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("2d{1, 232,31}");
        Assert.assertEquals(2, sim.getDice().size());
        Assert.assertEquals("[1, 232, 31]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseMultipleDigitNumberDiceWithSingleDigitNumberOfSides()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("13d6");
        Assert.assertEquals(13, sim.getDice().size());
        Assert.assertEquals("[1, 2, 3, 4, 5, 6]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseMultipleDigitNumberDiceWithMultipleDigitNumberOfSides()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("12d12");
        Assert.assertEquals(12, sim.getDice().size());
        Assert.assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseMultipleDigitNumberDiceWithOneSingleDigitCustomFace()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("11d{4}");
        Assert.assertEquals(11, sim.getDice().size());
        Assert.assertEquals("[4]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseMultipleDigitNumberDiceWithOneMultipleDigitCustomFace()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("12d{12}");
        Assert.assertEquals(12, sim.getDice().size());
        Assert.assertEquals("[12]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseMultipleDigitNumberDiceWithSingleDigitCustomFaces()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("21d{1,2,3}");
        Assert.assertEquals(21, sim.getDice().size());
        Assert.assertEquals("[1, 2, 3]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseMultipleDigitNumberDiceWithMultipleDigitCustomFaces()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("31d{12,22,32}");
        Assert.assertEquals(31, sim.getDice().size());
        Assert.assertEquals("[12, 22, 32]", sim.getDice().get(0).toString());
    }

    @Test
    public void shouldParseMultipleDigitNumberDiceWithMixedDigitCustomFaces()
    {
        DiceNotationParser dp = new DiceNotationParser();
        SimulationSpecification sim = dp.parse("12d{1, 232,31}");
        Assert.assertEquals(12, sim.getDice().size());
        Assert.assertEquals("[1, 232, 31]", sim.getDice().get(0).toString());
    }
}
