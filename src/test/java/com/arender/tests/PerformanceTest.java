package com.arender.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
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
            numberOfGetLayout100px = 0, numberOfGetLayout800px = 0;

    @BeforeSuite
    public static void initialization()
    {

        String fileFromConfig = System.getProperty("user.dir") + prop.getProperty(file);
        fileToUploadFromConfig = new File(fileFromConfig);

    }

    public void testMultipleRequests(File fileToUploadGP1, File fileToUploadGP2, File fileToUploadGP3)
            throws InterruptedException, IOException
    {
        ArrayList<Tasks> tabTasks = new ArrayList<Tasks>();

        LOGGER.info("Test has been started.");
        LOGGER.info("Available processors : " + Runtime.getRuntime().availableProcessors());
        ExecutorService executorGroup1 = Executors.newFixedThreadPool(numberOfUsers);
        ExecutorService executorGroup2 = Executors.newFixedThreadPool(numberOfUsers);
        ExecutorService executorGroup3 = Executors.newFixedThreadPool(numberOfUsers);
        AtomicInteger completed = new AtomicInteger();
        for (int i = 1; i <= numberOfUsers; i++)
        {
            executorGroup1.submit(() -> {
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
            executorGroup2.submit(() -> {
                try
                {
                    tabTasks.add(new Tasks(fileToUploadGP2));
                    completed.incrementAndGet();
                }
                catch (Exception e)
                {
                    LOGGER.error(e.getMessage());
                    ;
                }

            });
            executorGroup3.submit(() -> {
                try
                {
                    tabTasks.add(new Tasks(fileToUploadGP3));
                    completed.incrementAndGet();
                }
                catch (Exception e)
                {
                    LOGGER.error(e.getMessage());
                }

            });
        }
        executorGroup1.shutdown();
        executorGroup1.awaitTermination(3, TimeUnit.MINUTES);
        executorGroup2.shutdown();
        executorGroup2.awaitTermination(3, TimeUnit.MINUTES);
        executorGroup3.shutdown();
        executorGroup3.awaitTermination(3, TimeUnit.MINUTES);

        for (int i = 0; i < tabTasks.size(); i++)
        {
            Tasks task = tabTasks.get(i);
            tabAllTasks.add(task);
            LOGGER.info("\n Im the user " + task.getName() + "\n");
            totalUpload += task.getUploadResponse().time();

            totalLayout += task.getGetLayoutResponse().time();
            numberOfGetLayout100px += task.getTabGetImage100pxResponses().size();
            numberOfGetLayout800px += task.getTabGetImage800pxResponses().size();

            for (int l = 0; l < task.getTabGetImage100pxResponses().size(); l++)
            {
                totalGetImage100px += task.getTabGetImage100pxResponses().get(l).time();
            }
            for (int m = 0; m < task.getTabGetImage800pxResponses().size(); m++)
            {
                totalGetImage800px += task.getTabGetImage800pxResponses().get(m).time();
            }

            totalEvic += task.getEvicResponses().time();

        }
        LOGGER.info("Total number of users : " + completed.get());

    }

    @Test()
    public void PerforamnceTestInShortDuration() throws InterruptedException, IOException
    {
        Instant start = Instant.now();
        Duration duration = Duration.ofMinutes(2);
        while (Duration.between(start, Instant.now()).compareTo(duration) < 0)
        {
            testMultipleRequests(fileToUploadFromConfig, fileToUploadFromConfig, fileToUploadFromConfig);

        }
        chart = new CategoryChartBuilder().width(1920).height(1080).title("Graph of requests")
                .xAxisTitle("Name of request").yAxisTitle("Time").build();
        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        ArrayList<Long> averageOfUpload = new ArrayList<Long>();
        ArrayList<Long> averageOfLayout = new ArrayList<Long>();
        ArrayList<Long> averageOfGetImage100px = new ArrayList<Long>();
        ArrayList<Long> averageOfGetImage800px = new ArrayList<Long>();
        ArrayList<Long> averageOfEvic = new ArrayList<Long>();

        averageOfUpload.add(totalUpload / tabAllTasks.size());
        averageOfLayout.add(totalLayout / tabAllTasks.size());
        averageOfGetImage100px.add(totalGetImage100px / numberOfGetLayout100px);
        averageOfGetImage800px.add(totalGetImage800px / numberOfGetLayout800px);
        averageOfEvic.add(totalEvic / tabAllTasks.size());
        ArrayList<String> uploadRequests = new ArrayList<String>();
        uploadRequests.add("upload");
        ArrayList<String> layoutRequests = new ArrayList<String>();
        layoutRequests.add("get layout");
        ArrayList<String> getImage100pxRequests = new ArrayList<String>();
        getImage100pxRequests.add("get image 100px");
        ArrayList<String> getImage800pxRequests = new ArrayList<String>();
        getImage800pxRequests.add("get image 800px");
        ArrayList<String> evicRequests = new ArrayList<String>();
        evicRequests.add("evic");
        chart.addSeries("upload", uploadRequests, averageOfUpload);
        chart.addSeries("get layout ", layoutRequests, averageOfLayout);
        chart.addSeries("get image 100px ", getImage100pxRequests, averageOfGetImage100px);
        chart.addSeries("get image 800px ", getImage800pxRequests, averageOfGetImage800px);
        chart.addSeries("evic ", evicRequests, averageOfEvic);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BitmapEncoder.saveBitmap(chart, outputStream, BitmapFormat.PNG);
        byte[] imageBytes = outputStream.toByteArray();
        Allure.addAttachment("Graph of requests", "image/png", new ByteArrayInputStream(imageBytes), ".png");
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
