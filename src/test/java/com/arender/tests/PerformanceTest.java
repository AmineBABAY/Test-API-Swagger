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
import com.arender.actions.Result;
import com.arender.actions.Tasks;

public class PerformanceTest extends AssertActions
{

    private final static Logger LOGGER = Logger.getLogger(PerformanceTest.class);

    private static ArrayList<File> listFiles = new ArrayList<>();

    private Result pdf100KO = new Result("PDF_100KO.pdf");

    private Result pdf1MO = new Result("PDF_1MO.pdf");

    private Result tiffLowSize = new Result("TIFF_LOWSIZE.tiff");

    private Result doc100KO = new Result("DOC_100KO.doc");

    private Result jpeg100KO = new Result("JPEG_100KO.jpeg");

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

    // private long calculMean(ArrayList<Long> list)
    // {
    // return (long)
    // list.stream().mapToLong(Long::longValue).average().orElse(Double.NaN);
    // }
    private void checkTime(ArrayList<Long> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i) <= 800)
            {
                passed++;
            }
            else if (list.get(i) > 800 && list.get(i) < 60000)
            {
                warning++;
            }
            else
            {
                failed++;
            }
        }
    }

    private void checkTimePerDoc(Result result)
    {
        checkTime(result.getUploadList());
        checkTime(result.getGetLayoutList());
        checkTime(result.getGetBookmarksList());
        checkTime(result.getGetTextPositionList());
        checkTime(result.getGetImage100pxList());
        checkTime(result.getGetImage800pxList());
        checkTime(result.getEvictList());

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

                }
                catch (Exception e)
                {
                    LOGGER.error("exception : " + e.getMessage() + " Time : " + LocalTime.now());
                }
                finally
                {
                    completed.incrementAndGet();
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
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getUploadList().add(task.getUploadResponse().time());
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getUploadList().add(task.getUploadResponse().time());
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getUploadList().add(task.getUploadResponse().time());
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getUploadList().add(task.getUploadResponse().time());
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getUploadList().add(task.getUploadResponse().time());
                    break;
                default:
                    break;
                }

                if (task.getUploadResponse().getStatusCode() == 200)
                {
                    numberUploadOK++;
                }
            }
            if (task.getGetLayoutResponse() != null)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getGetLayoutList().add(task.getGetLayoutResponse().time());
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getGetLayoutList().add(task.getGetLayoutResponse().time());
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getGetLayoutList().add(task.getGetLayoutResponse().time());
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getGetLayoutList().add(task.getGetLayoutResponse().time());
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getGetLayoutList().add(task.getGetLayoutResponse().time());
                    break;
                default:
                    break;
                }

                if (task.getGetLayoutResponse().getStatusCode() == 200)
                {
                    numberGetLayoutOK++;
                }
            }
            if (task.getGetBookmarksResponse() != null)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getGetBookmarksList().add(task.getGetBookmarksResponse().time());
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getGetBookmarksList().add(task.getGetBookmarksResponse().time());
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getGetBookmarksList().add(task.getGetBookmarksResponse().time());
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getGetBookmarksList().add(task.getGetBookmarksResponse().time());
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getGetBookmarksList().add(task.getGetBookmarksResponse().time());
                    break;
                default:
                    break;
                }

                if (task.getGetBookmarksResponse().getStatusCode() == 200)
                {
                    numberGetBookmarksOK++;
                }
            }
            if (task.getEvictResponse() != null)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getEvictList().add(task.getEvictResponse().time());
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getEvictList().add(task.getEvictResponse().time());
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getEvictList().add(task.getEvictResponse().time());
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getEvictList().add(task.getEvictResponse().time());
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getEvictList().add(task.getEvictResponse().time());
                    break;
                default:
                    break;
                }

                if (task.getEvictResponse().getStatusCode() == 200)
                {
                    numberEvictOK++;
                }
            }

            for (int l = 0; l < task.getGetImage100pxResponses().size(); l++)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getGetImage100pxList().add(task.getGetImage100pxResponses().get(l).time());
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getGetImage100pxList().add(task.getGetImage100pxResponses().get(l).time());
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getGetImage100pxList().add(task.getGetImage100pxResponses().get(l).time());
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getGetImage100pxList().add(task.getGetImage100pxResponses().get(l).time());
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getGetImage100pxList().add(task.getGetImage100pxResponses().get(l).time());
                    break;
                default:
                    break;
                }
                if (task.getGetImage100pxResponses().get(l).getStatusCode() == 200)
                {
                    numberGetImage100pxOK++;
                }
            }
            for (int m = 0; m < task.getGetImage800pxResponses().size(); m++)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getGetImage800pxList().add(task.getGetImage800pxResponses().get(m).time());
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getGetImage800pxList().add(task.getGetImage800pxResponses().get(m).time());
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getGetImage800pxList().add(task.getGetImage800pxResponses().get(m).time());
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getGetImage800pxList().add(task.getGetImage800pxResponses().get(m).time());
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getGetImage800pxList().add(task.getGetImage800pxResponses().get(m).time());
                    break;
                default:
                    break;
                }

                if (task.getGetImage800pxResponses().get(m).getStatusCode() == 200)
                {
                    numberGetImage800pxOK++;
                }
            }
            for (int n = 0; n < task.getGetTextPositionResponses().size(); n++)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getGetTextPositionList().add(task.getGetTextPositionResponses().get(n).time());
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getGetTextPositionList().add(task.getGetTextPositionResponses().get(n).time());
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getGetTextPositionList().add(task.getGetTextPositionResponses().get(n).time());
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getGetTextPositionList().add(task.getGetTextPositionResponses().get(n).time());
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getGetTextPositionList().add(task.getGetTextPositionResponses().get(n).time());
                    break;
                default:
                    break;
                }

                if (task.getGetTextPositionResponses().get(n).getStatusCode() == 200)
                {
                    numberGetTextPositionOK++;
                }
            }

        }
        LOGGER.info(" \n ******************************* Done ******************************************* \n");
    }

    private synchronized void addTasks(ArrayList<Tasks> tasks, Tasks task) throws Exception
    {
        tasks.add(task);
    }

    private void generateResult()
    {
        checkTimePerDoc(doc100KO);
        checkTimePerDoc(jpeg100KO);
        checkTimePerDoc(pdf100KO);
        checkTimePerDoc(pdf1MO);
        checkTimePerDoc(tiffLowSize);

        int totalUpload = doc100KO.getUploadList().size() + jpeg100KO.getUploadList().size()
                + pdf100KO.getUploadList().size() + pdf1MO.getUploadList().size() + tiffLowSize.getUploadList().size();
        int totalGetLayout = doc100KO.getGetLayoutList().size() + jpeg100KO.getGetLayoutList().size()
                + pdf100KO.getGetLayoutList().size() + pdf1MO.getGetLayoutList().size()
                + tiffLowSize.getGetLayoutList().size();

        int totalGetBookmarks = doc100KO.getGetBookmarksList().size() + jpeg100KO.getGetBookmarksList().size()
                + pdf100KO.getGetBookmarksList().size() + pdf1MO.getGetBookmarksList().size()
                + tiffLowSize.getGetBookmarksList().size();

        int totalGetTextPosition = doc100KO.getGetTextPositionList().size() + jpeg100KO.getGetTextPositionList().size()
                + pdf100KO.getGetTextPositionList().size() + pdf1MO.getGetTextPositionList().size()
                + tiffLowSize.getGetTextPositionList().size();

        int totalGetImage100px = doc100KO.getGetImage100pxList().size() + jpeg100KO.getGetImage100pxList().size()
                + pdf100KO.getGetImage100pxList().size() + pdf1MO.getGetImage100pxList().size()
                + tiffLowSize.getGetImage100pxList().size();

        int totalGetImage800px = doc100KO.getGetImage800pxList().size() + jpeg100KO.getGetImage800pxList().size()
                + pdf100KO.getGetImage800pxList().size() + pdf1MO.getGetImage800pxList().size()
                + tiffLowSize.getGetImage800pxList().size();

        int totalEvict = doc100KO.getEvictList().size() + jpeg100KO.getEvictList().size()
                + pdf100KO.getEvictList().size() + pdf1MO.getEvictList().size() + tiffLowSize.getEvictList().size();

        ArrayList<String> nameOfAxis = new ArrayList<String>(Arrays
                .asList(new String[] { "Min", "Percentile50", "Percentile75", "Percentile95", "Percentile99", "Max" }));

        GraphGenerator.globalGraph(passed, failed, warning, "Global graph");

        GraphGenerator.generateGraph(stat(doc100KO.getUploadList()), stat(pdf100KO.getUploadList()),
                stat(tiffLowSize.getUploadList()), stat(jpeg100KO.getUploadList()), stat(pdf1MO.getUploadList()),
                nameOfAxis, "Upload", "Total of request : " + totalUpload + "  Total Passed : " + numberUploadOK,
                "Time (ms)", "report of Upload", completed.get());

        GraphGenerator.generateGraph(stat(doc100KO.getGetLayoutList()), stat(pdf100KO.getGetLayoutList()),
                stat(tiffLowSize.getGetLayoutList()), stat(jpeg100KO.getGetLayoutList()),
                stat(pdf1MO.getGetLayoutList()), nameOfAxis, "Get Layout",
                "Total of request : " + totalGetLayout + "  Total Passed : " + numberGetLayoutOK, "Time (ms)",
                "report of get layout", completed.get());

        GraphGenerator.generateGraph(stat(doc100KO.getGetBookmarksList()), stat(pdf100KO.getGetBookmarksList()),
                stat(tiffLowSize.getGetBookmarksList()), stat(jpeg100KO.getGetBookmarksList()),
                stat(pdf1MO.getGetBookmarksList()), nameOfAxis, "Get Bookmarks",
                "Total of request : " + totalGetBookmarks + "  Total Passed : " + numberGetBookmarksOK, "Time (ms)",
                "report of get bookmarks", completed.get());

        GraphGenerator.generateGraph(stat(doc100KO.getGetTextPositionList()), stat(pdf100KO.getGetTextPositionList()),
                stat(tiffLowSize.getGetTextPositionList()), stat(jpeg100KO.getGetTextPositionList()),
                stat(pdf1MO.getGetTextPositionList()), nameOfAxis, "Get text position",
                "Total of request : " + totalGetTextPosition + "  Total Passed : " + numberGetTextPositionOK,
                "Time (ms)", "report of get text position", completed.get());

        GraphGenerator.generateGraph(stat(doc100KO.getGetImage100pxList()), stat(pdf100KO.getGetImage100pxList()),
                stat(tiffLowSize.getGetImage100pxList()), stat(jpeg100KO.getGetImage100pxList()),
                stat(pdf1MO.getGetImage100pxList()), nameOfAxis, "Get image 100px",
                "Total of request : " + totalGetImage100px + "  Total Passed : " + numberGetImage100pxOK, "Time (ms)",
                "report of get image 100px", completed.get());

        GraphGenerator.generateGraph(stat(doc100KO.getGetImage800pxList()), stat(pdf100KO.getGetImage800pxList()),
                stat(tiffLowSize.getGetImage100pxList()), stat(jpeg100KO.getGetImage100pxList()),
                stat(pdf1MO.getGetImage800pxList()), nameOfAxis, "Get image 800px",
                "Total of request : " + totalGetImage800px + "  Total Passed : " + numberGetImage800pxOK, "Time (ms)",
                "report of get image 800px", completed.get());

        GraphGenerator.generateGraph(stat(doc100KO.getEvictList()), stat(pdf100KO.getEvictList()),
                stat(tiffLowSize.getEvictList()), stat(jpeg100KO.getEvictList()), stat(pdf1MO.getEvictList()),
                nameOfAxis, "Evict document", "Total of request : " + totalEvict + "  Total Passed : " + numberEvictOK,
                "Time (ms)", "report of evict", completed.get());
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
        generateResult();

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
