package se.spaedtke.dice.simulator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by johan on 20/09/16.
 */
public class DiceNotationParser
{
    private List<Die> dice;
    private Integer diceToKeep;
    private Integer staticBonus;
    private String whichToKeep;

    /**
     * The supported syntax is described by the following BNF.
     * <p>
     * totalExpression ::= dieExpression(<operator><dieExpression>)
     * dieExpression ::= <number>+"d"(<number>+ | <customFace>)[<whichToKeep>][<staticModifier>]
     * whichToKeep ::= "-"(<high>|<low>)
     * staticModifier ::= <operator><number>+
     * operator ::= "+" | "-" | <mult> | <div>
     * customeFace ::= "{"<number>+<csvNumber>*"}"
     * csvNumber ::= ","<number>+
     * mult ::= "*" | "x"
     * div ::= "/" | "÷"
     * high ::= "H" | "h"
     * low ::= "L" | "l"
     * number ::= 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0
     */

    private final static List<Character> OPERATORS = Arrays.asList('+', '-', '*', '/');
    private final static List<Character> HIGH_LOW = Arrays.asList('h', 'H', 'l', 'L');

    public SimulationSpecification parse(String foo)
    {
        SimulationSpecification.Builder simBuilder = SimulationSpecification.newBuilder();
        parseDieExpression(simBuilder, asStack(foo));
        return simBuilder.build();
    }

    private void parseDieExpression(SimulationSpecification.Builder simBuilder, Stack<Character> diceNotation)
    {
        boolean foundWhichToKeepBefore = false;
        Character lastOperator;
        while (!diceNotation.isEmpty())
        {
            if (isDie(diceNotation))
            {
                simBuilder.dice(parseDie(diceNotation));
            }
//        else if (ifStaticBonus(diceNotation))
//        {
//            parseStaticBonus(diceNotation);
//        }
            else if (ifWhichToKeep(diceNotation))
            {
                if (!foundWhichToKeepBefore)
                {
                    simBuilder.whichToKeep(parseWhichToKeep(diceNotation));
                    foundWhichToKeepBefore = true;
                }
                else
                {
                    throw new RuntimeException("There can only be one whichToKeep per partial expression");
                }
            }
        }
    }

    private boolean ifWhichToKeep(Stack<Character> diceNotation)
    {
        boolean isWhichToKeep = false;
        Stack<Character> cache = new Stack<>();
        while (!diceNotation.isEmpty() && !OPERATORS.contains(diceNotation.peek()))
        {
            if (!HIGH_LOW.contains(diceNotation.peek()))
            {
                isWhichToKeep = true;
                break;
            }
            cache.push(diceNotation.pop());
        }
        while (!cache.isEmpty())
        {
            diceNotation.push(cache.pop());
        }
        return isWhichToKeep;

    }

    private WhichToKeep parseWhichToKeep(Stack<Character> diceNotation)
    {
        Deque<Character> deq = new ArrayDeque<>();
        while (!diceNotation.isEmpty() && !OPERATORS.contains(diceNotation.peek()))
        {
            deq.addLast(diceNotation.pop());
        }
        int numDice = getNumDiceFrom(deq);
        Character whichToKeep = deq.pop();
        return WhichToKeep.fromChar(whichToKeep).howManyToKeep(numDice);
    }

    private boolean isDie(Stack<Character> diceNotation)
    {
        boolean isDie = false;
        Stack<Character> cache = new Stack<>();
        while (!OPERATORS.contains(diceNotation.peek()))
        {
            if (diceNotation.peek().equals('d'))
            {
                isDie = true;
                break;
            }
            cache.push(diceNotation.pop());
        }
        while (!cache.isEmpty())
        {
            diceNotation.push(cache.pop());
        }
        return isDie;
    }

    private List<Die> parseDie(Stack<Character> diceNotation)
    {
        Deque<Character> deq = new ArrayDeque<>();
        while (!diceNotation.isEmpty() && !OPERATORS.contains(diceNotation.peek()))
        {
            deq.addLast(diceNotation.pop());
        }
        return parseDiceSpec(deq);
    }

    private List<Die> parseDiceSpec(Deque<Character> dieSpec)
    {
        List<Die> dice = new ArrayList<>();
        Integer numDice = getNumDiceFrom(dieSpec);
        dieSpec.pop(); //remove d
        List<Integer> faces = facesFrom(dieSpec);
        for (int i = 0; i < numDice; i++)
        {
            dice.add(new Die(faces));
        }
        return dice;
    }

    private Stack<Character> asStack(String die)
    {
        Stack<Character> res = new Stack<>();
        for (Character c : new StringBuffer(die).reverse().toString().toCharArray())
        {
            res.push(c);
        }
        return res;
    }

    private Integer getNumDiceFrom(Deque<Character> dieSpec)
    {
        if (dieSpec.peek().equals('d') || HIGH_LOW.contains(dieSpec.peek()))
        {
            return 1;
        }
        else
        {
            StringBuilder numBuilder = new StringBuilder();
            while (!dieSpec.peek().equals('d') || !HIGH_LOW.contains(dieSpec.peek()))
            {
                numBuilder.append(dieSpec.pop());
            }
            return Integer.parseInt(numBuilder.toString());
        }
    }

    private List<Integer> facesFrom(Deque<Character> faceSpec)
    {
        if (!faceSpec.peek().equals('{'))
        {
            StringBuilder numBuilder = new StringBuilder();
            while (!faceSpec.isEmpty())
            {
                numBuilder.append(faceSpec.pop());
            }
            return genFaces(Integer.parseInt(numBuilder.toString()));
        }
        else
        {
            List<Integer> res = new ArrayList<>();
            faceSpec.pop(); //remove {
            while (!faceSpec.peek().equals('}'))
            {
                System.out.println("faceSpec" + faceSpec);
                StringBuilder numBuilder = new StringBuilder();
                while (!(faceSpec.peek().equals(',') || faceSpec.peek().equals('}')))
                {
                    Character pop = faceSpec.pop();
                    System.out.println("pop" + pop);
                    numBuilder.append(pop);
                }
                if (faceSpec.peek().equals(','))
                {
                    System.out.println("throwing away" + faceSpec.pop());
                }
                System.out.println("Adding " + numBuilder.toString());
                res.add(Integer.parseInt(numBuilder.toString().trim()));
                System.out.println("Current faces " + res);
                System.out.println("NextLoop" + faceSpec.peek() + " - " + !faceSpec.peek().equals('}'));
            }
            System.out.println("Returning faces " + res);
            return res;
        }
    }

    private List<Integer> genFaces(int max)
    {
        List<Integer> res = new ArrayList<>(max);
        for (int i = 1; i <= max; i++)
        {
            res.add(i);
        }
        return res;
    }

    public static SimulationSpecification diceSpecification(String diceSpecification)
    {
        return SimulationSpecification.newBuilder()
                .dice(diceFrom(diceSpecification))
                .diceToKeep(diceToKeepFrom(diceSpecification))
                .staticBonus(staticBonusFrom(diceSpecification))
                .whichToKeep(whichToKeepFrom(diceSpecification))
                .build();
    }

    private static Integer staticBonusFrom(String diceSpecification)
    {
        Pattern p = Pattern.compile(".*([+-]\\d*).*");
        Matcher m = p.matcher(diceSpecification);
        if (m.matches())
        {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    private static List<Die> diceFrom(String diceSpecification)
    {
        List<Die> res = new ArrayList<>();
        Pattern p = Pattern.compile(",?(\\d*)[dD](\\d*)(,.*)?(?:[+-]?.*)?");
        Matcher m = p.matcher(diceSpecification);
        if (m.matches())
        {
            for (int i = 0; i < Integer.parseInt(m.group(1)); i++)
            {
                res.add(new Die(Integer.parseInt(m.group(2))));
            }
            if (m.group(3) != null)
            {
                res.addAll(diceFrom(m.group(3)));
            }
        }
        return res;
    }

    private static WhichToKeep whichToKeepFrom(String diceSpecification)
    {
        Pattern p = Pattern.compile(".* k.* \\d* ?(.*).*");
        Matcher m = p.matcher(diceSpecification);
        if (m.matches())
        {
            return WhichToKeep.fromString(m.group(1));
        }
        return WhichToKeep.ALL;
    }

    private static Integer diceToKeepFrom(String diceSpecification)
    {
        Pattern p = Pattern.compile(".* k.* (\\d*) .*");
        Matcher m = p.matcher(diceSpecification);
        if (m.matches())
        {
            return Integer.parseInt(m.group(1));
        }
        return null;
    }
}
