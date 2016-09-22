package se.spaedtke.dice.simulator;

import java.util.Comparator;

/**
 * Created by johan on 21/09/16.
 */
public class WhichToKeep
{
    public final static WhichToKeep HIGH = new WhichToKeep().sortingOrder((x, y) -> Integer.compare(y, x));
    public final static WhichToKeep LOW = new WhichToKeep().sortingOrder((x, y) -> Integer.compare(x, y));
    public final static WhichToKeep ALL = new WhichToKeep().howManyToKeep(-1);

    public Comparator<Integer> sortingOrder;
    public Integer howManyToKeep;

    private WhichToKeep()
    {
        this((x, y) -> Integer.compare(y, x), -1);
    }

    public WhichToKeep(Comparator<Integer> sortingOrder, Integer howManyToKeep)
    {
        this.howManyToKeep = howManyToKeep;
        this.sortingOrder = sortingOrder;
    }

    private WhichToKeep howManyToKeep(int howManyToKeep)
    {
        return new WhichToKeep(sortingOrder, howManyToKeep);
    }

    public WhichToKeep sortingOrder(Comparator<Integer> sortingOrder)
    {
        return new WhichToKeep(sortingOrder, howManyToKeep);
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
