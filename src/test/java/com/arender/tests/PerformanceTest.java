package com.arender.tests;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    private int numberUploadOK = 0, numberGetLayoutOK = 0, numberGetBookmarksOK = 0, numberGetImage100pxOK = 0,
            numberGetImage800pxOK = 0, numberGetTextPositionOK = 0, numberEvictOK = 0, passed = 0, warning = 0,
            failed = 0;

    private AtomicInteger completed;

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

    private void verifTime(ArrayList<Long> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i) <= 800)
            {
                passed++;
            }
            else if (list.get(i) > 800 && list.get(i) < 120000)
            {
                warning++;
            }
            else
            {
                failed++;
            }
        }
    }

    public void testMultipleRequests() throws Exception
    {
        completed = new AtomicInteger();
        ArrayList<Tasks> tasks = new ArrayList<Tasks>();

        ExecutorService executorGroup = Executors.newFixedThreadPool(numberOfUsers);

        for (int i = 0; i < numberOfUsers; i++)
        {
            executorGroup.submit(() -> {
                try
                {
                    List<Integer> randomRead = Arrays.asList(0, 1, 2, 3, 4);
                    Collections.shuffle(randomRead);
                    for (int j = 0; j < listFiles.size(); j++)
                    {
                        File fileToUpload = listFiles.get(randomRead.get(j));
                        Tasks task = new Tasks(fileToUpload);
                        addTasks(tasks, task);
                    }
                    completed.incrementAndGet();
                }
                catch (Exception e)
                {
                    LOGGER.error("exception : " + e.getMessage() + " Time : " + LocalTime.now());
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
                if (task.getUploadResponse().getStatusCode() == 200)
                {
                    numberUploadOK++;
                }
            }
            if (task.getGetLayoutResponse() != null)
            {
                getLayoutList.add(task.getGetLayoutResponse().time());
                if (task.getGetLayoutResponse().getStatusCode() == 200)
                {
                    numberGetLayoutOK++;
                }
            }
            if (task.getGetBookmarksResponse() != null)
            {
                getBookmarksList.add(task.getGetBookmarksResponse().time());
                if (task.getGetBookmarksResponse().getStatusCode() == 200)
                {
                    numberGetBookmarksOK++;
                }
            }
            if (task.getEvictResponse() != null)
            {
                evictList.add(task.getEvictResponse().time());
                if (task.getEvictResponse().getStatusCode() == 200)
                {
                    numberEvictOK++;
                }
            }

            for (int l = 0; l < task.getGetImage100pxResponses().size(); l++)
            {
                getImage100pxList.add(task.getGetImage100pxResponses().get(l).time());
                if (task.getGetImage100pxResponses().get(l).getStatusCode() == 200)
                {
                    numberGetImage100pxOK++;
                }
            }
            for (int m = 0; m < task.getGetImage800pxResponses().size(); m++)
            {
                getImage800pxList.add(task.getGetImage800pxResponses().get(m).time());
                if (task.getGetImage800pxResponses().get(m).getStatusCode() == 200)
                {
                    numberGetImage800pxOK++;
                }
            }
            for (int n = 0; n < task.getGetTextPositionResponses().size(); n++)
            {
                getTextPositionList.add(task.getGetTextPositionResponses().get(n).time());
                if (task.getGetTextPositionResponses().get(n).getStatusCode() == 200)
                {
                    numberGetTextPositionOK++;
                }
            }

        }
        LOGGER.info("Total users : " + completed.get());
        LOGGER.info("Total upload : " + uploadList.size() + " Total OK : " + numberUploadOK);
        LOGGER.info("Total getLayout : " + getLayoutList.size() + " Total OK : " + numberGetLayoutOK);
        LOGGER.info("Total getBookmarks : " + getBookmarksList.size() + " Total OK : " + numberGetBookmarksOK);
        LOGGER.info("Total getImage100px : " + getImage100pxList.size() + " Total OK : " + numberGetImage100pxOK);
        LOGGER.info("Total getImage800px : " + getImage800pxList.size() + " Total OK : " + numberGetImage800pxOK);
        LOGGER.info("Total getTextPosition : " + getTextPositionList.size() + " Total OK : " + numberGetTextPositionOK);
        LOGGER.info("Total evictDocument : " + evictList.size() + " Total OK : " + numberEvictOK);

    }

    private synchronized void addTasks(ArrayList<Tasks> tasks, Tasks task) throws Exception
    {
        tasks.add(task);
    }

    @Test()
    public void PerforamnceTestInShortDuration() throws Exception, InterruptedException, IOException
    {
        LOGGER.info("Test has been started : " + LocalTime.now());
        LOGGER.info("Available processors : " + Runtime.getRuntime().availableProcessors());

        Instant start = Instant.now();
        Duration duration = Duration.ofMinutes(2);
        while (Duration.between(start, Instant.now()).compareTo(duration) < 0)
        {
            testMultipleRequests();

        }
        verifTime(uploadList);
        verifTime(getLayoutList);
        verifTime(getImage100pxList);
        verifTime(getImage800pxList);
        verifTime(getBookmarksList);
        verifTime(getTextPositionList);
        verifTime(evictList);

        ArrayList<String> nameOfAxis = new ArrayList<String>(Arrays
                .asList(new String[] { "Min", "Percentile50", "Percentile75", "Percentile95", "Percentile99", "Max" }));
        GraphGenerator.globalGraph(passed, failed, warning, "Global graph");
        GraphGenerator.generateGraph(stat(uploadList), nameOfAxis, "Upload",
                "Total of request : " + uploadList.size() + "  Total Passed : " + numberUploadOK, "Time (ms)",
                "report of Upload");
        GraphGenerator.generateGraph(stat(getLayoutList), nameOfAxis, "Get Layout",
                "Total of request : " + getLayoutList.size() + "  Total Passed : " + numberGetLayoutOK, "Time (ms)",
                "report of getLayout");
        GraphGenerator.generateGraph(stat(getImage100pxList), nameOfAxis, "Get image 100px",
                "Total of reuquest : " + getImage100pxList.size() + "  Total Passed : " + numberGetImage100pxOK,
                "Time (ms)", "report of Get image 100px");
        GraphGenerator.generateGraph(stat(getImage800pxList), nameOfAxis, "Get image 800px",
                "Total of reuquest : " + getImage800pxList.size() + "  Total Passed : " + numberGetImage800pxOK,
                "Time (ms)", "report of Get image 800px");
        GraphGenerator.generateGraph(stat(getBookmarksList), nameOfAxis, "Get Bookmarks",
                "Total of reuquest : " + getBookmarksList.size() + "  Total Passed : " + numberGetBookmarksOK,
                "Time (ms)", "report of getBookmarks");
        GraphGenerator.generateGraph(stat(getTextPositionList), nameOfAxis, "Get Text position",
                "Total of reuquest : " + getTextPositionList.size() + "  Total Passed : " + numberGetTextPositionOK,
                "Time (ms)", "report of getTextPosition");
        GraphGenerator.generateGraph(stat(evictList), nameOfAxis, "Evict",
                "Total of reuquest : " + evictList.size() + "  Total Passed : " + numberEvictOK, "Time (ms)",
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
