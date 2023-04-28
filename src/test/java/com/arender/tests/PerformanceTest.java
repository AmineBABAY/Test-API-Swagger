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

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.actions.Result;
import com.arender.actions.SendDataToOpenSearch;
import com.arender.actions.Tasks;

public class PerformanceTest extends AssertActions
{

    private final static Logger LOGGER = Logger.getLogger(PerformanceTest.class);

    private static ArrayList<File> listFiles = new ArrayList<>();

    private static Result pdf100KO = new Result("PDF_100KO.pdf");

    private static Result pdf1MO = new Result("PDF_1MO.pdf");

    private static Result tiffLowSize = new Result("TIFF_LOWSIZE.tiff");

    private static Result doc100KO = new Result("DOC_100KO.doc");

    private static Result jpeg100KO = new Result("JPEG_100KO.jpeg");

    private int numberUploadOK = 0, numberGetLayoutOK = 0, numberGetBookmarksOK = 0, numberGetImage100pxOK = 0,
            numberGetImage800pxOK = 0, numberGetTextPositionOK = 0, numberEvictOK = 0, passed = 0, warning = 0,
            failed = 0, totalUpload = 0, totalGetLayout = 0, totalGetBookmarks = 0, totalGetTextPosition = 0,
            totalGetImage100px = 0, totalGetImage800px = 0, totalEvict = 0;

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
                    if (task.getUploadResponse().getStatusCode() == 200)
                    {
                        pdf1MO.incrementTotalUploadOK();
                    }
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getUploadList().add(task.getUploadResponse().time());
                    if (task.getUploadResponse().getStatusCode() == 200)
                    {
                        pdf100KO.incrementTotalUploadOK();
                    }
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getUploadList().add(task.getUploadResponse().time());
                    if (task.getUploadResponse().getStatusCode() == 200)
                    {
                        jpeg100KO.incrementTotalUploadOK();
                    }
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getUploadList().add(task.getUploadResponse().time());
                    if (task.getUploadResponse().getStatusCode() == 200)
                    {
                        doc100KO.incrementTotalUploadOK();
                    }
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getUploadList().add(task.getUploadResponse().time());
                    if (task.getUploadResponse().getStatusCode() == 200)
                    {
                        tiffLowSize.incrementTotalUploadOK();
                    }
                    break;
                default:
                    break;
                }

            }
            if (task.getGetLayoutResponse() != null)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getGetLayoutList().add(task.getUploadResponse().time());
                    if (task.getGetLayoutResponse().getStatusCode() == 200)
                    {
                        pdf1MO.incrementTotalgetLayoutOK();
                    }
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getGetLayoutList().add(task.getGetLayoutResponse().time());
                    if (task.getGetLayoutResponse().getStatusCode() == 200)
                    {
                        pdf100KO.incrementTotalgetLayoutOK();
                    }
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getGetLayoutList().add(task.getGetLayoutResponse().time());
                    if (task.getGetLayoutResponse().getStatusCode() == 200)
                    {
                        jpeg100KO.incrementTotalgetLayoutOK();
                    }
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getGetLayoutList().add(task.getGetLayoutResponse().time());
                    if (task.getGetLayoutResponse().getStatusCode() == 200)
                    {
                        doc100KO.incrementTotalgetLayoutOK();
                    }
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getGetLayoutList().add(task.getGetLayoutResponse().time());
                    if (task.getGetLayoutResponse().getStatusCode() == 200)
                    {
                        tiffLowSize.incrementTotalgetLayoutOK();
                    }
                    break;
                default:
                    break;
                }

            }
            if (task.getGetBookmarksResponse() != null)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getGetBookmarksList().add(task.getGetBookmarksResponse().time());
                    if (task.getGetBookmarksResponse().getStatusCode() == 200)
                    {
                        pdf1MO.incrementTotalGetBoomarksOK();
                    }
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getGetBookmarksList().add(task.getGetBookmarksResponse().time());
                    if (task.getGetBookmarksResponse().getStatusCode() == 200)
                    {
                        pdf100KO.incrementTotalGetBoomarksOK();
                    }
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getGetBookmarksList().add(task.getGetBookmarksResponse().time());
                    if (task.getGetBookmarksResponse().getStatusCode() == 200)
                    {
                        jpeg100KO.incrementTotalGetBoomarksOK();
                    }
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getGetBookmarksList().add(task.getGetBookmarksResponse().time());
                    if (task.getGetBookmarksResponse().getStatusCode() == 200)
                    {
                        doc100KO.incrementTotalGetBoomarksOK();
                    }
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getGetBookmarksList().add(task.getGetBookmarksResponse().time());
                    if (task.getGetBookmarksResponse().getStatusCode() == 200)
                    {
                        tiffLowSize.incrementTotalGetBoomarksOK();
                    }
                    break;
                default:
                    break;
                }

            }
            if (task.getEvictResponse() != null)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getEvictList().add(task.getEvictResponse().time());
                    if (task.getEvictResponse().getStatusCode() == 200)
                    {
                        pdf1MO.incrementTotalEvictOK();
                    }
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getEvictList().add(task.getEvictResponse().time());
                    if (task.getEvictResponse().getStatusCode() == 200)
                    {
                        pdf100KO.incrementTotalEvictOK();
                    }
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getEvictList().add(task.getEvictResponse().time());
                    if (task.getEvictResponse().getStatusCode() == 200)
                    {
                        jpeg100KO.incrementTotalEvictOK();
                    }
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getEvictList().add(task.getEvictResponse().time());
                    if (task.getEvictResponse().getStatusCode() == 200)
                    {
                        doc100KO.incrementTotalEvictOK();
                    }
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getEvictList().add(task.getEvictResponse().time());
                    if (task.getEvictResponse().getStatusCode() == 200)
                    {
                        tiffLowSize.incrementTotalEvictOK();
                    }
                    break;
                default:
                    break;
                }

            }

            for (int l = 0; l < task.getGetImage100pxResponses().size(); l++)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getGetImage100pxList().add(task.getGetImage100pxResponses().get(l).time());
                    if (task.getGetImage100pxResponses().get(l).getStatusCode() == 200)
                    {
                        pdf1MO.incrementTotalGetImage100pxOK();
                    }
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getGetImage100pxList().add(task.getGetImage100pxResponses().get(l).time());
                    if (task.getGetImage100pxResponses().get(l).getStatusCode() == 200)
                    {
                        pdf100KO.incrementTotalGetImage100pxOK();
                    }
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getGetImage100pxList().add(task.getGetImage100pxResponses().get(l).time());
                    if (task.getGetImage100pxResponses().get(l).getStatusCode() == 200)
                    {
                        jpeg100KO.incrementTotalGetImage100pxOK();
                    }
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getGetImage100pxList().add(task.getGetImage100pxResponses().get(l).time());
                    if (task.getGetImage100pxResponses().get(l).getStatusCode() == 200)
                    {
                        doc100KO.incrementTotalGetImage100pxOK();
                    }
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getGetImage100pxList().add(task.getGetImage100pxResponses().get(l).time());
                    if (task.getGetImage100pxResponses().get(l).getStatusCode() == 200)
                    {
                        tiffLowSize.incrementTotalGetImage100pxOK();
                    }
                    break;
                default:
                    break;
                }
            }
            for (int m = 0; m < task.getGetImage800pxResponses().size(); m++)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getGetImage800pxList().add(task.getGetImage800pxResponses().get(m).time());
                    if (task.getGetImage800pxResponses().get(m).getStatusCode() == 200)
                    {
                        pdf1MO.incrementTotalGetImage800pxOK();
                    }
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getGetImage800pxList().add(task.getGetImage800pxResponses().get(m).time());
                    if (task.getGetImage800pxResponses().get(m).getStatusCode() == 200)
                    {
                        pdf100KO.incrementTotalGetImage800pxOK();
                    }
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getGetImage800pxList().add(task.getGetImage800pxResponses().get(m).time());
                    if (task.getGetImage800pxResponses().get(m).getStatusCode() == 200)
                    {
                        jpeg100KO.incrementTotalGetImage800pxOK();
                    }
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getGetImage800pxList().add(task.getGetImage800pxResponses().get(m).time());
                    if (task.getGetImage800pxResponses().get(m).getStatusCode() == 200)
                    {
                        doc100KO.incrementTotalGetImage800pxOK();
                    }
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getGetImage800pxList().add(task.getGetImage800pxResponses().get(m).time());
                    if (task.getGetImage800pxResponses().get(m).getStatusCode() == 200)
                    {
                        tiffLowSize.incrementTotalGetImage800pxOK();
                    }
                    break;
                default:
                    break;
                }

            }
            for (int n = 0; n < task.getGetTextPositionResponses().size(); n++)
            {
                switch (task.getName())
                {
                case "PDF_1MO.pdf":
                    pdf1MO.getGetTextPositionList().add(task.getGetTextPositionResponses().get(n).time());
                    if (task.getGetTextPositionResponses().get(n).getStatusCode() == 200)
                    {
                        pdf1MO.incrementTotalGetTextPositionOK();
                    }
                    break;
                case "PDF_100KO.pdf":
                    pdf100KO.getGetTextPositionList().add(task.getGetTextPositionResponses().get(n).time());
                    if (task.getGetTextPositionResponses().get(n).getStatusCode() == 200)
                    {
                        pdf100KO.incrementTotalGetTextPositionOK();
                    }
                    break;
                case "JPEG_100KO.jpeg":
                    jpeg100KO.getGetTextPositionList().add(task.getGetTextPositionResponses().get(n).time());
                    if (task.getGetTextPositionResponses().get(n).getStatusCode() == 200)
                    {
                        jpeg100KO.incrementTotalGetTextPositionOK();
                    }
                    break;
                case "DOC_100KO.doc":
                    doc100KO.getGetTextPositionList().add(task.getGetTextPositionResponses().get(n).time());
                    if (task.getGetTextPositionResponses().get(n).getStatusCode() == 200)
                    {
                        doc100KO.incrementTotalGetTextPositionOK();
                    }
                    break;
                case "TIFF_LOWSIZE.tiff":
                    tiffLowSize.getGetTextPositionList().add(task.getGetTextPositionResponses().get(n).time());
                    if (task.getGetTextPositionResponses().get(n).getStatusCode() == 200)
                    {
                        tiffLowSize.incrementTotalGetTextPositionOK();
                    }
                    break;
                default:
                    break;
                }

            }

        }

    }

    private synchronized void addTasks(ArrayList<Tasks> tasks, Tasks task) throws Exception
    {
        tasks.add(task);
    }

    private void calculTotalRequest()
    {
        totalUpload = doc100KO.getUploadList().size() + jpeg100KO.getUploadList().size()
                + pdf100KO.getUploadList().size() + pdf1MO.getUploadList().size() + tiffLowSize.getUploadList().size();
        totalGetLayout = doc100KO.getGetLayoutList().size() + jpeg100KO.getGetLayoutList().size()
                + pdf100KO.getGetLayoutList().size() + pdf1MO.getGetLayoutList().size()
                + tiffLowSize.getGetLayoutList().size();

        totalGetBookmarks = doc100KO.getGetBookmarksList().size() + jpeg100KO.getGetBookmarksList().size()
                + pdf100KO.getGetBookmarksList().size() + pdf1MO.getGetBookmarksList().size()
                + tiffLowSize.getGetBookmarksList().size();

        totalGetTextPosition = doc100KO.getGetTextPositionList().size() + jpeg100KO.getGetTextPositionList().size()
                + pdf100KO.getGetTextPositionList().size() + pdf1MO.getGetTextPositionList().size()
                + tiffLowSize.getGetTextPositionList().size();

        totalGetImage100px = doc100KO.getGetImage100pxList().size() + jpeg100KO.getGetImage100pxList().size()
                + pdf100KO.getGetImage100pxList().size() + pdf1MO.getGetImage100pxList().size()
                + tiffLowSize.getGetImage100pxList().size();

        totalGetImage800px = doc100KO.getGetImage800pxList().size() + jpeg100KO.getGetImage800pxList().size()
                + pdf100KO.getGetImage800pxList().size() + pdf1MO.getGetImage800pxList().size()
                + tiffLowSize.getGetImage800pxList().size();

        totalEvict = doc100KO.getEvictList().size() + jpeg100KO.getEvictList().size() + pdf100KO.getEvictList().size()
                + pdf1MO.getEvictList().size() + tiffLowSize.getEvictList().size();
    }

    private void calculTotalRequestOK()
    {
        numberUploadOK = doc100KO.getTotalUploadOK() + jpeg100KO.getTotalUploadOK() + pdf100KO.getTotalUploadOK()
                + pdf1MO.getTotalUploadOK() + tiffLowSize.getTotalUploadOK();

        numberGetLayoutOK = doc100KO.getTotalgetLayoutOK() + jpeg100KO.getTotalgetLayoutOK()
                + pdf100KO.getTotalgetLayoutOK() + pdf1MO.getTotalgetLayoutOK() + tiffLowSize.getTotalgetLayoutOK();

        numberGetBookmarksOK = doc100KO.getTotalGetBoomarksOK() + jpeg100KO.getTotalGetBoomarksOK()
                + pdf100KO.getTotalGetBoomarksOK() + pdf1MO.getTotalGetBoomarksOK()
                + tiffLowSize.getTotalGetBoomarksOK();

        numberGetImage100pxOK = doc100KO.getTotalGetImage100pxOK() + jpeg100KO.getTotalGetImage100pxOK()
                + pdf100KO.getTotalGetImage100pxOK() + pdf1MO.getTotalGetImage100pxOK()
                + tiffLowSize.getTotalGetImage100pxOK();

        numberGetImage800pxOK = doc100KO.getTotalGetImage800pxOK() + jpeg100KO.getTotalGetImage800pxOK()
                + pdf100KO.getTotalGetImage800pxOK() + pdf1MO.getTotalGetImage800pxOK()
                + tiffLowSize.getTotalGetImage800pxOK();

        numberGetTextPositionOK = doc100KO.getTotalGetTextPositionOK() + jpeg100KO.getTotalGetTextPositionOK()
                + pdf100KO.getTotalGetTextPositionOK() + pdf1MO.getTotalGetTextPositionOK()
                + tiffLowSize.getTotalGetTextPositionOK();

        numberEvictOK = doc100KO.getTotalEvictOK() + jpeg100KO.getTotalEvictOK() + pdf100KO.getTotalEvictOK()
                + pdf1MO.getTotalEvictOK() + tiffLowSize.getTotalEvictOK();
    }

    private void generateResult()
    {
        // checkTimePerDoc(doc100KO);
        // checkTimePerDoc(jpeg100KO);
        // checkTimePerDoc(pdf100KO);
        // checkTimePerDoc(pdf1MO);
        // checkTimePerDoc(tiffLowSize);
        // calculTotalRequest();
        // calculTotalRequestOK();
        //
        // ArrayList<String> nameOfAxis = new ArrayList<String>(Arrays
        // .asList(new String[] { "Min", "Percentile50", "Percentile75",
        // "Percentile95", "Percentile99", "Max" }));
        //
        // GraphGenerator.globalGraph(passed, failed, warning, "Global graph",
        // completed.get());
        //
        // GraphGenerator.generateGraph(Stat.calculStat(doc100KO.getUploadList()),
        // Stat.calculStat(pdf100KO.getUploadList()),
        // Stat.calculStat(tiffLowSize.getUploadList()),
        // Stat.calculStat(jpeg100KO.getUploadList()),
        // Stat.calculStat(pdf1MO.getUploadList()), nameOfAxis,
        // "Upload", "Total of request : " + totalUpload + " Total Passed : " +
        // numberUploadOK, "Time (ms)",
        // "report of Upload", completed.get());
        //
        // GraphGenerator.generateGraph(Stat.calculStat(doc100KO.getGetLayoutList()),
        // Stat.calculStat(pdf100KO.getGetLayoutList()),
        // Stat.calculStat(tiffLowSize.getGetLayoutList()),
        // Stat.calculStat(jpeg100KO.getGetLayoutList()),
        // Stat.calculStat(pdf1MO.getGetLayoutList()), nameOfAxis,
        // "Get Layout", "Total of request : " + totalGetLayout + " Total Passed
        // : " + numberGetLayoutOK,
        // "Time (ms)", "report of get layout", completed.get());
        //
        // GraphGenerator.generateGraph(Stat.calculStat(doc100KO.getGetBookmarksList()),
        // Stat.calculStat(pdf100KO.getGetBookmarksList()),
        // Stat.calculStat(tiffLowSize.getGetBookmarksList()),
        // Stat.calculStat(jpeg100KO.getGetBookmarksList()),
        // Stat.calculStat(pdf1MO.getGetBookmarksList()),
        // nameOfAxis, "Get Bookmarks",
        // "Total of request : " + totalGetBookmarks + " Total Passed : " +
        // numberGetBookmarksOK, "Time (ms)",
        // "report of get bookmarks", completed.get());
        //
        // GraphGenerator.generateGraph(Stat.calculStat(doc100KO.getGetTextPositionList()),
        // Stat.calculStat(pdf100KO.getGetTextPositionList()),
        // Stat.calculStat(tiffLowSize.getGetTextPositionList()),
        // Stat.calculStat(jpeg100KO.getGetTextPositionList()),
        // Stat.calculStat(pdf1MO.getGetTextPositionList()),
        // nameOfAxis, "Get text position",
        // "Total of request : " + totalGetTextPosition + " Total Passed : " +
        // numberGetTextPositionOK,
        // "Time (ms)", "report of get text position", completed.get());
        //
        // GraphGenerator.generateGraph(Stat.calculStat(doc100KO.getGetImage100pxList()),
        // Stat.calculStat(pdf100KO.getGetImage100pxList()),
        // Stat.calculStat(tiffLowSize.getGetImage100pxList()),
        // Stat.calculStat(jpeg100KO.getGetImage100pxList()),
        // Stat.calculStat(pdf1MO.getGetImage100pxList()),
        // nameOfAxis, "Get image 100px",
        // "Total of request : " + totalGetImage100px + " Total Passed : " +
        // numberGetImage100pxOK, "Time (ms)",
        // "report of get image 100px", completed.get());
        //
        // GraphGenerator.generateGraph(Stat.calculStat(doc100KO.getGetImage800pxList()),
        // Stat.calculStat(pdf100KO.getGetImage800pxList()),
        // Stat.calculStat(tiffLowSize.getGetImage800pxList()),
        // Stat.calculStat(jpeg100KO.getGetImage800pxList()),
        // Stat.calculStat(pdf1MO.getGetImage800pxList()),
        // nameOfAxis, "Get image 800px",
        // "Total of request : " + totalGetImage800px + " Total Passed : " +
        // numberGetImage800pxOK, "Time (ms)",
        // "report of get image 800px", completed.get());
        //
        // GraphGenerator.generateGraph(Stat.calculStat(doc100KO.getEvictList()),
        // Stat.calculStat(pdf100KO.getEvictList()),
        // Stat.calculStat(tiffLowSize.getEvictList()),
        // Stat.calculStat(jpeg100KO.getEvictList()),
        // Stat.calculStat(pdf1MO.getEvictList()), nameOfAxis, "Evict document",
        // "Total of request : " + totalEvict + " Total Passed : " +
        // numberEvictOK, "Time (ms)",
        // "report of evict", completed.get());
        // List<Result> listOfResults = Arrays.asList(doc100KO, pdf100KO,
        // tiffLowSize, jpeg100KO, pdf1MO);
        // GraphGenerator.generateTable(listOfResults);

        LOGGER.info("Total users : " + completed.get());
        LOGGER.info("Total upload : " + totalUpload + " Total OK : " + numberUploadOK);
        LOGGER.info("Total getLayout : " + totalGetLayout + " Total OK : " + numberGetLayoutOK);
        LOGGER.info("Total getBookmarks : " + totalGetBookmarks + " Total OK : " + numberGetBookmarksOK);
        LOGGER.info("Total getImage100px : " + totalGetImage100px + " Total OK : " + numberGetImage100pxOK);
        LOGGER.info("Total getImage800px : " + totalGetImage800px + " Total OK : " + numberGetImage800pxOK);
        LOGGER.info("Total getTextPosition : " + totalGetTextPosition + " Total OK : " + numberGetTextPositionOK);
        LOGGER.info("Total evictDocument : " + totalEvict + " Total OK : " + numberEvictOK);
        LOGGER.info(" \n ******************************* Done ******************************************* \n");
        List<Result> resultList = Arrays.asList(pdf100KO, pdf1MO, tiffLowSize, doc100KO, jpeg100KO);
        SendDataToOpenSearch.SendData(resultList);
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
