package se.spaedtke.dice;

import junit.framework.Assert;
import org.junit.Test;
import se.spaedtke.dice.simulator.Die;

import java.util.Arrays;

/**
 * Created by johan on 21/09/16.
 */
public class DieTest
{
    @Test
    public void canGenerateStandardDiceFromMaxValue(){
        Assert.assertEquals(Arrays.asList(1,2,3,4,5,6), new Die(6).getFaces());
        Assert.assertEquals(Arrays.asList(1,2), new Die(2).getFaces());
        Assert.assertEquals(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12), new Die(12).getFaces());
    }

    @Test
    public void canSpecifyCustomDie(){
        Assert.assertEquals(Arrays.asList(0,0,4,4,5,6), new Die(0,0,4,4,5,6).getFaces());
        Assert.assertEquals(Arrays.asList(2,3,2), new Die(2,3,2).getFaces());
    }
}
