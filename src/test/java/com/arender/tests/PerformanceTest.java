package com.arender.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    private static File fileToUploadFromConfig;

    private final static Logger LOGGER = Logger.getLogger(PerformanceTest.class);

    private ArrayList<Tasks> tabAllTasks = new ArrayList<Tasks>();

    private static CategoryChart chart;

    long totalUpload = 0, totalLayout = 0, totalGetImage100px = 0, totalGetImage800px = 0, totalEvic = 0,
            numberOfGetLayout100px = 0, numberOfGetLayout800px = 0, maxOfUpload = 0, maxOfLayout = 0,
            maxOfGetImage100px = 0, maxOfGetImage800px = 0, maxOfEvic = 0, minOfUpload = 0, minOfLayout = 0,
            minOfGetImage100px = 0, minOfGetImage800px = 0, minOfEvic = 0;

    @BeforeSuite
    public static void initialization()
    {

        String fileFromConfig = System.getProperty("user.dir") + prop.getProperty(file);
        fileToUploadFromConfig = new File(fileFromConfig);

    }

    // public long findMax()

    public void testMultipleRequests(File fileToUploadGP1, File fileToUploadGP2, File fileToUploadGP3)
            throws InterruptedException, IOException
    {
        ArrayList<Tasks> tabTasks = new ArrayList<Tasks>();

        ExecutorService executorGroup = Executors.newFixedThreadPool(numberOfUsers);

        AtomicInteger completed = new AtomicInteger();
        for (int i = 1; i <= numberOfUsers; i++)
        {
            executorGroup.submit(() -> {
                try
                {
                    tabTasks.add(new Tasks(fileToUploadGP1));
                    completed.incrementAndGet();
                }
                catch (Exception e)
                {
                    LOGGER.error(e.getMessage());
                }
            });

        }
        executorGroup.shutdown();
        executorGroup.awaitTermination(3, TimeUnit.MINUTES);
        for (int i = 0; i < tabTasks.size(); i++)
        {
            Tasks task = tabTasks.get(i);
            tabAllTasks.add(task);
            if (maxOfUpload < task.getUploadResponse().time())
            {
                maxOfUpload = task.getUploadResponse().time();
            }
            if (maxOfLayout < task.getGetLayoutResponse().time())
            {
                maxOfLayout = task.getGetLayoutResponse().time();
            }
            if (maxOfEvic < task.getEvicResponses().time())
            {
                maxOfEvic = task.getGetLayoutResponse().time();
            }

            if (minOfUpload == 0)
            {
                minOfUpload = tabTasks.get(0).getUploadResponse().time();
            }
            else if (minOfUpload > task.getUploadResponse().time())
            {
                minOfUpload = task.getUploadResponse().time();
            }

            if (minOfLayout == 0)
            {
                minOfLayout = tabTasks.get(0).getGetLayoutResponse().time();
            }
            else if (minOfLayout > task.getGetLayoutResponse().time())
            {
                minOfLayout = task.getGetLayoutResponse().time();
            }
            if (minOfEvic == 0)
            {
                minOfEvic = tabTasks.get(0).getEvicResponses().time();
            }
            else if (minOfEvic > task.getEvicResponses().time())
            {
                minOfEvic = task.getEvicResponses().time();
            }
            totalUpload += task.getUploadResponse().time();

            totalLayout += task.getGetLayoutResponse().time();
            numberOfGetLayout100px += task.getTabGetImage100pxResponses().size();
            numberOfGetLayout800px += task.getTabGetImage800pxResponses().size();

            for (int l = 0; l < task.getTabGetImage100pxResponses().size(); l++)
            {
                totalGetImage100px += task.getTabGetImage100pxResponses().get(l).time();
                if (maxOfGetImage100px < task.getTabGetImage100pxResponses().get(l).time())
                {
                    maxOfGetImage100px = task.getTabGetImage100pxResponses().get(l).time();
                }
                if (minOfGetImage100px == 0)
                {
                    minOfGetImage100px = task.getTabGetImage100pxResponses().get(0).time();
                }
                else if (minOfGetImage100px > task.getTabGetImage100pxResponses().get(l).time())
                {
                    minOfGetImage100px = task.getTabGetImage100pxResponses().get(l).time();
                }
            }
            for (int m = 0; m < task.getTabGetImage800pxResponses().size(); m++)
            {
                totalGetImage800px += task.getTabGetImage800pxResponses().get(m).time();
                if (maxOfGetImage800px < task.getTabGetImage800pxResponses().get(m).time())
                {
                    maxOfGetImage800px = task.getTabGetImage800pxResponses().get(m).time();
                }
                if (minOfGetImage800px == 0)
                {
                    minOfGetImage800px = task.getTabGetImage800pxResponses().get(0).time();
                }
                else if (minOfGetImage800px > task.getTabGetImage800pxResponses().get(m).time())
                {
                    minOfGetImage800px = task.getTabGetImage800pxResponses().get(m).time();
                }
            }

            totalEvic += task.getEvicResponses().time();

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
            testMultipleRequests(fileToUploadFromConfig, fileToUploadFromConfig, fileToUploadFromConfig);

        }
        chart = new CategoryChartBuilder().title("Average of requests for : " + file).xAxisTitle("Request")
                .yAxisTitle("Time in milliseonds").theme(ChartTheme.Matlab).build();
        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setLabelsVisible(true);
        // chart.getStyler().setPlotGridLinesVisible(true);
        ArrayList<Long> averageOfUpload = new ArrayList<Long>();
        ArrayList<Long> averageOfLayout = new ArrayList<Long>();
        ArrayList<Long> averageOfGetImage100px = new ArrayList<Long>();
        ArrayList<Long> averageOfGetImage800px = new ArrayList<Long>();
        ArrayList<Long> averageOfEvic = new ArrayList<Long>();
        ArrayList<String> nameOfAxis = new ArrayList<String>(Arrays.asList(new String[] { "Average", "Max", "Min" }));

        averageOfUpload.add(totalUpload / tabAllTasks.size());
        averageOfUpload.add(maxOfUpload);
        averageOfUpload.add(minOfUpload);

        averageOfLayout.add(totalLayout / tabAllTasks.size());
        averageOfLayout.add(maxOfLayout);
        averageOfLayout.add(minOfLayout);

        averageOfGetImage100px.add(totalGetImage100px / numberOfGetLayout100px);
        averageOfGetImage100px.add(maxOfGetImage100px);
        averageOfGetImage100px.add(minOfGetImage100px);

        averageOfGetImage800px.add(totalGetImage800px / numberOfGetLayout800px);
        averageOfGetImage800px.add(maxOfGetImage800px);
        averageOfGetImage800px.add(minOfGetImage800px);

        averageOfEvic.add(totalEvic / tabAllTasks.size());
        averageOfEvic.add(maxOfEvic);
        averageOfEvic.add(minOfEvic);

        chart.addSeries("upload", nameOfAxis, averageOfUpload);
        chart.addSeries("get layout ", nameOfAxis, averageOfLayout);
        chart.addSeries("get image 100px ", nameOfAxis, averageOfGetImage100px);
        chart.addSeries("get image 800px ", nameOfAxis, averageOfGetImage800px);
        chart.addSeries("evic ", nameOfAxis, averageOfEvic);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BitmapEncoder.saveBitmap(chart, outputStream, BitmapFormat.PNG);
        byte[] imageBytes = outputStream.toByteArray();
        Allure.addAttachment("Average of requests", "image/png", new ByteArrayInputStream(imageBytes), ".png");
        LOGGER.info("average of upload  : " + averageOfUpload.get(0));
        LOGGER.info("average of get layout  : " + averageOfLayout.get(0));
        LOGGER.info("average of get image 100px  : " + averageOfGetImage100px.get(0));
        LOGGER.info("average of get image 800px  : " + averageOfGetImage800px.get(0));
        LOGGER.info("average of evic   : " + averageOfEvic.get(0));

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
        ;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BitmapEncoder.saveBitmap(chart, outputStream, BitmapFormat.PNG);
        byte[] imageBytes = outputStream.toByteArray();
        Allure.addAttachment("Graph of responses", "image/png", new ByteArrayInputStream(imageBytes), ".png");
    }

}
