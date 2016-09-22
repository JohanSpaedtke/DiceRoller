package se.spaedtke.dice.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by johan on 20/09/16.
 */
public class SimulationSpecification
{
    private List<Die> dice;
    private Integer diceToKeep;
    private Integer staticBonus;
    private String whichToKeep;

    private SimulationSpecification(Builder builder)
    {
        dice = builder.dice == null ? Collections.emptyList() : Collections.unmodifiableList(builder.dice);
        diceToKeep = builder.diceToKeep;
        staticBonus = builder.staticBonus;
        whichToKeep = builder.whichToKeep;
    }

    public List<Die> getDice()
    {
        return dice;
    }

    public Integer getDiceToKeep()
    {
        return diceToKeep;
    }

    public Integer getStaticBonus()
    {
        return staticBonus;
    }

    public String getWhichToKeep()
    {
        return whichToKeep;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static Builder newBuilder(SimulationSpecification copy)
    {
        Builder builder = new Builder();
        builder.dice = copy.dice;
        builder.diceToKeep = copy.diceToKeep;
        builder.staticBonus = copy.staticBonus;
        builder.whichToKeep = copy.whichToKeep;
        return builder;
    }

    public static final class Builder
    {
        private List<Die> dice;
        private Integer diceToKeep;
        private Integer staticBonus;
        private String whichToKeep;

        private Builder()
        {
        }

        public Builder dice(Die val)
        {
            if (dice == null)
            {
                dice = new ArrayList<Die>();
            }
            dice.add(val);
            return this;
        }

        public Builder dice(Die... val)
        {
            if (dice == null)
            {
                dice = new ArrayList<Die>();
            }
            dice.addAll(Arrays.asList(val));
            return this;
        }

        public Builder dice(List<Die> val)
        {
            dice = val;
            return this;
        }

        public Builder diceToKeep(Integer val)
        {
            diceToKeep = val;
            return this;
        }

        public Builder staticBonus(Integer val)
        {
            staticBonus = val;
            return this;
        }

        public Builder whichToKeep(String val)
        {
            whichToKeep = val;
            return this;
        }

        public SimulationSpecification build()
        {
            return new SimulationSpecification(this);
        }
    }
}
