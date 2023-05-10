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

    private void generateResult()
    {

        LOGGER.info("Total users : " + completed.get());
        LOGGER.info(" \n ******************************* Done ******************************************* \n");
        pdf100KO.setNumberOfUsers(completed.get());
        pdf1MO.setNumberOfUsers(completed.get());
        tiffLowSize.setNumberOfUsers(completed.get());
        doc100KO.setNumberOfUsers(completed.get());
        jpeg100KO.setNumberOfUsers(completed.get());
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
