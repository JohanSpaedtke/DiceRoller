package se.spaedtke.dice.simulator;

import java.util.ArrayList;
import java.util.List;
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
     *
     * totalExpression ::= <partialExpression>(<operator>"("<partialExpression>")")*
     * partialExpression ::= <staticBonus> | <die><expressionPart>*[<whichToKeep>]
     * expressionPart ::= <operator><die> | <staticBonus>
     * staticBonus ::= <operator><number>+
     * operator ::= + | - | (x | *) | (/ | ÷)
     * whichToKeep ::= "-"<number>*("h" | "H" | "l" | "L") | "keep" <number>* ("highest" | "h" | "H" | "lowest" | "l" | "L")
     * die ::= <number>*"d"(<number>+ | <customFace>)
     * customeFace ::= "{"<number>+<csvNumber>*"}"
     * csvNumber ::= "" | ","<number>+
     * number ::= 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0
      */
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

    private static String whichToKeepFrom(String diceSpecification)
    {
        Pattern p = Pattern.compile(".* k.* \\d* ?(.*).*");
        Matcher m = p.matcher(diceSpecification);
        if (m.matches())
        {
            return m.group(1);
        }
        return null;
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
