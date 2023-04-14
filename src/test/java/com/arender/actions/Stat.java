package com.arender.actions;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.stat.StatUtils;

public class Stat
{
    public static long calculPercentile(ArrayList<Long> list, double percentile)
    {
        double[] myArray = list.stream().mapToDouble(Long::doubleValue).toArray();
        Arrays.sort(myArray);
        return (long) StatUtils.percentile(myArray, percentile);
    }

    public static long calculMax(ArrayList<Long> list)
    {
        return list.stream().max(Long::compare).orElse(null);
    }

    public static long calculMin(ArrayList<Long> list)
    {
        return list.stream().min(Long::compare).orElse(null);
    }

    public static ArrayList<Long> calculStat(ArrayList<Long> RequestList)
    {
        ArrayList<Long> result = new ArrayList<>();
        result.add(calculMin(RequestList));
        result.add(calculPercentile(RequestList, 50.0));
        result.add(calculPercentile(RequestList, 75.0));
        result.add(calculPercentile(RequestList, 95.0));
        result.add(calculPercentile(RequestList, 99.0));
        result.add(calculMax(RequestList));
        return result;
    }
}
