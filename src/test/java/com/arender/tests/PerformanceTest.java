package com.arender.tests;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private final static Logger LOGGER = Logger.getLogger(PerformanceTest.class);

    private static ArrayList<File> listFiles = new ArrayList<>();

    private ArrayList<Long> uploadList = new ArrayList<>();

    private ArrayList<Long> getLayoutList = new ArrayList<>();

    private ArrayList<Long> getBookmarksList = new ArrayList<>();

    private ArrayList<Long> getImage100pxList = new ArrayList<>();

    private ArrayList<Long> getImage800pxList = new ArrayList<>();

    private ArrayList<Long> getTextPositionList = new ArrayList<>();

    private ArrayList<Long> evictList = new ArrayList<>();

    @BeforeSuite
    private static void initialization()
    {
        listFiles.add(new File(System.getProperty("user.dir") + prop.getProperty("pdf_with_100KO")));
        listFiles.add(new File(System.getProperty("user.dir") + prop.getProperty("pdf_with_1MO")));
        listFiles.add(new File(System.getProperty("user.dir") + prop.getProperty("doc_with_100KO")));
        listFiles.add(new File(System.getProperty("user.dir") + prop.getProperty("tiff_with_lowSize")));
        listFiles.add(new File(System.getProperty("user.dir") + prop.getProperty("jpeg_with_100KO")));
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

    public void testMultipleRequests() throws InterruptedException
    {
        ArrayList<Tasks> tasks = new ArrayList<Tasks>();

        ExecutorService executorGroup = Executors.newFixedThreadPool(numberOfUsers);

        AtomicInteger completed = new AtomicInteger();
        for (int i = 0; i < numberOfUsers; i++)
        {
            Collections.shuffle(listFiles);
            executorGroup.submit(() -> {
                try
                {

                    for (int j = 0; j < listFiles.size(); j++)
                    {
                        File fileToUpload = listFiles.get(j);
                        Tasks task = new Tasks(fileToUpload);
                        addTasks(tasks, task);
                    }
                    completed.incrementAndGet();
                }
                catch (Exception e)
                {
                    LOGGER.error("exception : " + e.getMessage());
                }
            });

        }
        executorGroup.shutdown();
        executorGroup.awaitTermination(5, TimeUnit.MINUTES);
        for (int i = 0; i < tasks.size(); i++)
        {
            Tasks task = tasks.get(i);
            if (task.getUploadResponse() != null)
            {
                uploadList.add(task.getUploadResponse().time());
            }
            if (task.getGetLayoutResponse() != null)
            {
                getLayoutList.add(task.getGetLayoutResponse().time());
            }
            if (task.getGetBookmarksResponse() != null)
            {
                getBookmarksList.add(task.getGetBookmarksResponse().time());
            }
            if (task.getEvictResponse() != null)
            {
                evictList.add(task.getEvictResponse().time());
            }

            for (int l = 0; l < task.getGetImage100pxResponses().size(); l++)
            {
                getImage100pxList.add(task.getGetImage100pxResponses().get(l).time());
            }
            for (int m = 0; m < task.getGetImage800pxResponses().size(); m++)
            {
                getImage800pxList.add(task.getGetImage800pxResponses().get(m).time());
            }
            for (int n = 0; n < task.getGetTextPositionResponses().size(); n++)
            {
                getTextPositionList.add(task.getGetTextPositionResponses().get(n).time());
            }

        }
        LOGGER.info("Total users : " + completed.get());
        LOGGER.info("Total upload : " + uploadList.size());
        LOGGER.info("Total getBookmarks : " + getBookmarksList.size());
        LOGGER.info("Total getImage100px : " + getImage100pxList.size());
        LOGGER.info("Total getImage800px : " + getImage800pxList.size());
        LOGGER.info("Total getTextPosition : " + getTextPositionList.size());
        LOGGER.info("Total evictDocument : " + evictList.size());

    }

    private synchronized void addTasks(ArrayList<Tasks> tasks, Tasks task) throws Exception
    {
        tasks.add(task);
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
            testMultipleRequests();

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
        GraphGenerator.generateGraph(stat(getBookmarksList), nameOfAxis, "Get Bookmarks", "Request Number", "Time (ms)",
                "report of getBookmarks");
        GraphGenerator.generateGraph(stat(getTextPositionList), nameOfAxis, "Get Text position", "Request Number",
                "Time (ms)", "report of getTextPosition");
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
