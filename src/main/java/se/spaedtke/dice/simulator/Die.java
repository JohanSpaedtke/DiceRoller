package se.spaedtke.dice.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by johan on 21/09/16.
 */
public class Die
{
    private final Integer[] faces;

    public Die(Integer max)
    {
        this.faces = genFaces(max);
    }

    private Integer[] genFaces(int max)
    {
        Integer[] res = new Integer[max];
        for (int i = 1; i <= max; i++)
        {
            res[i - 1] = i;
        }
        return res;
    }

    public Die(Integer... faces){
        this.faces = faces;
    }

    public List<Integer> getFaces()
    {
        List<Integer> res = new ArrayList<>(faces.length);
        for(int face : faces){
            res.add(face);
        }
        return res;
    }

    public Integer getNumFaces()
    {
        return faces.length;
    }

    public Integer getFace(int faceIndex)
    {
        return faces[faceIndex];
    }

    public Integer roll()
    {
        return faces[ThreadLocalRandom.current().nextInt(0, faces.length)];
    }

    @Override
    public String toString()
    {
        return Arrays.toString(faces);
    }
}
