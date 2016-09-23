package se.spaedtke.dice.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by johan on 20/09/16.
 */
public class CountingDiceSimulator implements DiceSimulator
{
    private final List<Die> dice;
    private final Integer staticBonus;
    private final WhichToKeep whichToKeep;

    private final Set<List<Integer>> possibleRolls = new HashSet<>();

    public CountingDiceSimulator(List<Die> dice, Integer staticBonus, WhichToKeep whichToKeep)
    {
        this.dice = dice;
        this.staticBonus = staticBonus;
        this.whichToKeep = whichToKeep;
        generatePossibleRolls();
    }

    private void generatePossibleRolls()
    {
        generatePossibleRolls(possibleRolls, new int[dice.size()], dice, 0);
    }

    private static void generatePossibleRolls(Set<List<Integer>> possibleRolls, int[] roll, List<Die> dice, int fixedDie)
    {
        if (fixedDie == dice.size())
        {
            possibleRolls.add(copyToList(roll));
        }
        else
        {
            for (int currentFace = 0; currentFace <= dice.get(fixedDie).getNumFaces() - 1; currentFace++)
            {
                roll[fixedDie] = dice.get(fixedDie).getFace(currentFace);
                generatePossibleRolls(possibleRolls, roll, dice, fixedDie + 1);
            }
        }
    }

    private static List<Integer> copyToList(int[] roll)
    {
        List<Integer> res = new ArrayList<>(roll.length);
        for (int i : roll)
        {
            res.add(i);
        }
        return res;
    }

    @Override
    public String getSpecification()
    {
        return "";
    }

    @Override
    public Map<Integer, Double> getNormalizedHistogram()
    {
        Integer numRolls = possibleRolls.size();
        return modifiedRolls()
                .collect(Collectors.toMap(
                        list -> list.stream().mapToInt(i -> i.intValue()).sum(),
                        list -> 1d / numRolls,
                        (d1, d2) -> d1 + d2));
    }

    @Override
    public double expectedValue()
    {
        return (
                (double) modifiedRolls()
                        .flatMap(list -> list.stream())
                        .mapToInt(i -> i.intValue())
                        .sum()
        ) / possibleRolls.size();
    }

    private Stream<List<Integer>> modifiedRolls()
    {
        return (Stream<List<Integer>>) possibleRolls.stream()
                .map(cloneList())
                .map(removeUnvantedDice())
                .map(addStaticBonus())
                .collect(Collectors.toList())
                .stream();
    }

    private Function<? super List<Integer>,? extends List<Integer>> cloneList()
    {
        return list -> {
            List<Integer> res = new ArrayList<>(list.size());
            for(Integer i : list){
                res.add(i);
            }
            return res;
        };
    }

    private Function<? super List<Integer>, ? extends List<Integer>> removeUnvantedDice()
    {
        return list ->
        {
            if (whichToKeep.howManyToKeep < 0)
            {
                return list;
            }
            return list.stream().sorted(whichToKeep.sortingOrder).limit(whichToKeep.howManyToKeep).collect(Collectors.toList());
        };
    }

    private Function<? super List<Integer>, ? extends List<Integer>> addStaticBonus()
    {
        return list ->
        {
            list.add(staticBonus);
            return list;
        };
    }

}
