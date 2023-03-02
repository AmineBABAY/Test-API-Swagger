package com.arender.tests;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.actions.GraphGenerator;
import com.arender.actions.Tasks;

public class PerformanceTest extends AssertActions
{
    private static File fileToUploadFromConfig;

    private final static Logger LOGGER = Logger.getLogger(PerformanceTest.class);

    private ArrayList<Long> uploadList = new ArrayList<>();

    private ArrayList<Long> getLayoutList = new ArrayList<>();

    private ArrayList<Long> getImage100pxList = new ArrayList<>();

    private ArrayList<Long> getImage800pxList = new ArrayList<>();

    private ArrayList<Long> evictList = new ArrayList<>();

    @BeforeSuite
    private static void initialization()
    {

        String fileFromConfig = System.getProperty("user.dir") + prop.getProperty(file);
        fileToUploadFromConfig = new File(fileFromConfig);

    }

    private ArrayList<Long> stat(ArrayList<Long> RequestList)
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

    private long calculPercentile(ArrayList<Long> list, double percentile)
    {
        double[] myArray = list.stream().mapToDouble(Long::doubleValue).toArray();
        Arrays.sort(myArray);
        return (long) StatUtils.percentile(myArray, percentile);
    }

    private long calculMax(ArrayList<Long> list)
    {
        return list.stream().max(Long::compare).orElse(null);
    }

    private long calculMin(ArrayList<Long> list)
    {
        return list.stream().min(Long::compare).orElse(null);
    }

    private long calculMean(ArrayList<Long> list)
    {
        return (long) list.stream().mapToLong(Long::longValue).average().orElse(Double.NaN);
    }

    public void testMultipleRequests(File fileToUploadGP) throws InterruptedException, IOException
    {
        ArrayList<Tasks> tasks = new ArrayList<Tasks>();

        ExecutorService executorGroup = Executors.newFixedThreadPool(numberOfUsers);

        AtomicInteger completed = new AtomicInteger();
        for (int i = 1; i <= numberOfUsers; i++)
        {
            executorGroup.submit(() -> {
                try
                {
                    tasks.add(new Tasks(fileToUploadGP));
                    completed.incrementAndGet();
                }
                catch (Exception e)
                {
                    LOGGER.error("exception : " + e.getMessage());
                }
            });

        }
        executorGroup.shutdown();
        executorGroup.awaitTermination(3, TimeUnit.MINUTES);
        for (int i = 0; i < tasks.size(); i++)
        {
            Tasks task = tasks.get(i);

            uploadList.add(task.getUploadResponse().time());
            getLayoutList.add(task.getGetLayoutResponse().time());
            evictList.add(task.getEvictResponse().time());

            for (int l = 0; l < task.getTabGetImage100pxResponses().size(); l++)
            {
                getImage100pxList.add(task.getTabGetImage100pxResponses().get(l).time());
                getImage800pxList.add(task.getTabGetImage800pxResponses().get(l).time());
            }

        }
        LOGGER.info("Total number of users : " + completed.get());

    }

    @Test()
    public void PerforamnceTestInShortDuration() throws InterruptedException, IOException
    {
        LOGGER.info("Test has been started.");
        LOGGER.info("Available processors : " + Runtime.getRuntime().availableProcessors());
        Instant start = Instant.now();
        Duration duration = Duration.ofMinutes(2);
        while (Duration.between(start, Instant.now()).compareTo(duration) < 0)
        {
            testMultipleRequests(fileToUploadFromConfig);

        }

        ArrayList<String> nameOfAxis = new ArrayList<String>(Arrays
                .asList(new String[] { "Min", "Percentile50", "Percentile75", "Percentile95", "Percentile99", "Max" }));

        // Generate the graphs for each list of data
        GraphGenerator.generateGraph(stat(uploadList), nameOfAxis, "Upload", "Request Number", "Time (ms)",
                "report of Upload");
        GraphGenerator.generateGraph(stat(getLayoutList), nameOfAxis, "Get Layout", "Request Number", "Time (ms)",
                "report of getLayout");
        GraphGenerator.generateGraph(stat(getImage100pxList), nameOfAxis, "Get image 100px", "Request Number",
                "Time (ms)", "report of Get image 100px");
        GraphGenerator.generateGraph(stat(getImage800pxList), nameOfAxis, "Get image 800px", "Request Number",
                "Time (ms)", "report of Get image 800px");
        GraphGenerator.generateGraph(stat(evictList), nameOfAxis, "Evict", "Request Number", "Time (ms)",
                "report of Evict");
    }

    @Test(enabled = false)
    public void PerforamnceTestWithConfiguredDuration() throws InterruptedException, IOException
    {
        Instant start = Instant.now();
        Duration duration = Duration.ofMinutes(durationOfTest);
        while (Duration.between(start, Instant.now()).compareTo(duration) < 0)
        {
            // testMultipleRequests(fileToUploadFromConfig,
            // fileToUploadFromConfig, fileToUploadFromConfig);

        }

    }

}
