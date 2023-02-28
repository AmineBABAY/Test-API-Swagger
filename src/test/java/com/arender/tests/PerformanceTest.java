package com.arender.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.actions.Tasks;

import io.qameta.allure.Allure;

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

    private byte[] generateImageForGraph(CategoryChart chart) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BitmapEncoder.saveBitmap(chart, outputStream, BitmapFormat.PNG);
        return outputStream.toByteArray();
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

    private CategoryChart chartForRequest(String nameRequest)
    {
        CategoryChart chart = new CategoryChartBuilder().title(nameRequest + " " + file).xAxisTitle(nameRequest)
                .yAxisTitle("Time in milliseonds").theme(ChartTheme.Matlab).build();
        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setLabelsVisible(true);
        return chart;
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
            uploadList.add(task.getUploadResponse().time());
            getLayoutList.add(task.getGetLayoutResponse().time());
            getBookmarksList.add(task.getGetBookmarkstResponse().time());
            evictList.add(task.getEvictResponse().time());

            for (int l = 0; l < task.getGetImage100pxResponses().size(); l++)
            {
                getImage100pxList.add(task.getGetImage100pxResponses().get(l).time());
                getImage800pxList.add(task.getGetImage800pxResponses().get(l).time());
                getTextPositionList.add(task.getGetTextPositionResponses().get(l).time());
            }

        }
        LOGGER.info("Total number of users : " + completed.get());
        LOGGER.info("Total number of upload : " + uploadList.size());

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

        CategoryChart chartUpload = chartForRequest("Upload");
        chartUpload.addSeries("Upload", nameOfAxis, stat(uploadList));
        CategoryChart chartLayout = chartForRequest("Get Layout");
        chartLayout.addSeries("Get Layout", nameOfAxis, stat(getLayoutList));
        CategoryChart chartImage100px = chartForRequest("Get image 100px");
        chartImage100px.addSeries("Get image 100px", nameOfAxis, stat(getImage100pxList));
        CategoryChart chartImage800px = chartForRequest("Get image 800px");
        chartImage800px.addSeries("Get image 800px", nameOfAxis, stat(getImage800pxList));
        CategoryChart chartBookmarks = chartForRequest("Get Bookmarks");
        chartBookmarks.addSeries("Get Bookmarks", nameOfAxis, stat(getBookmarksList));
        CategoryChart chartTextPosition = chartForRequest("Get Text position");
        chartTextPosition.addSeries("Get Text position", nameOfAxis, stat(getTextPositionList));
        CategoryChart chartEvict = chartForRequest("Evict");
        chartEvict.addSeries("Evict", nameOfAxis, stat(evictList));
        //

        Allure.addAttachment("report of Upload", "image/png",
                new ByteArrayInputStream(generateImageForGraph(chartUpload)), ".png");
        Allure.addAttachment("report of getLayout", "image/png",
                new ByteArrayInputStream(generateImageForGraph(chartLayout)), ".png");
        Allure.addAttachment("report of getBookmarks", "image/png",
                new ByteArrayInputStream(generateImageForGraph(chartBookmarks)), ".png");
        Allure.addAttachment("report of getImage100px", "image/png",
                new ByteArrayInputStream(generateImageForGraph(chartImage100px)), ".png");
        Allure.addAttachment("report of getImage800px", "image/png",
                new ByteArrayInputStream(generateImageForGraph(chartImage800px)), ".png");
        Allure.addAttachment("report of getTextPosition", "image/png",
                new ByteArrayInputStream(generateImageForGraph(chartTextPosition)), ".png");
        Allure.addAttachment("report of evict", "image/png",
                new ByteArrayInputStream(generateImageForGraph(chartEvict)), ".png");

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
