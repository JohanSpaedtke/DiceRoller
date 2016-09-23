package se.spaedtke.dice.simulator;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by johan on 21/09/16.
 */
public class WhichToKeep
{
    public final static WhichToKeep HIGH = new WhichToKeep().sortingOrder((x, y) -> Integer.compare(y, x));
    public final static WhichToKeep LOW = new WhichToKeep().sortingOrder((x, y) -> Integer.compare(x, y));
    public final static WhichToKeep RANDOM = new WhichToKeep().sortingOrder((x, y) -> Integer.compare(1, ThreadLocalRandom.current().nextInt(3)));
    public final static WhichToKeep ALL = new WhichToKeep();

    public Comparator<Integer> sortingOrder;
    public Integer howManyToKeep;

    public WhichToKeep()
    {
        this((x, y) -> 0, -1);
    }

    private WhichToKeep(Comparator<Integer> sortingOrder, Integer howManyToKeep)
    {
        this.howManyToKeep = howManyToKeep;
        this.sortingOrder = sortingOrder;
    }

    public WhichToKeep howManyToKeep(int howManyToKeep)
    {
        return new WhichToKeep(sortingOrder, howManyToKeep);
    }

    public WhichToKeep sortingOrder(Comparator<Integer> sortingOrder)
    {
        return new WhichToKeep(sortingOrder, howManyToKeep);
    }

    public static WhichToKeep fromChar(Character c)
    {
        if (c == null)
        {
            throw new IllegalArgumentException("WhichToKeep specifier can't be null");
        }
        else if (c.equals('H') || c.equals('h'))
        {
            return HIGH;
        }
        else if (c.equals('L') || c.equals('l'))
        {
            return LOW;
        }
        else if (c.equals('R') || c.equals('r'))
        {
            return RANDOM;
        }
        else{
            throw new IllegalArgumentException("Unknown WhichToKeep: " + c);
        }
    }

    public static WhichToKeep fromString(String string)
    {
        if (string == null || string.isEmpty())
        {
            return HIGH;
        }
        switch (string.toLowerCase())
        {
            case "l":
            case "low":
            case "lowest":
                return LOW;
            default:
                return HIGH;
        }
    }
}
