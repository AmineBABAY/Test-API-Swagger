package com.arender.tests;

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
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.actions.Tasks;

public class PerformanceTest extends AssertActions
{
    private static File fileToUploadFromConfig;

    private final static Logger LOGGER = Logger.getLogger(PerformanceTest.class);

    private static int totalSuccessRequest = 0;

    private static int totalRequest = 0;

    @BeforeSuite
    public static void initialization()
    {
        String fileFromConfig = System.getProperty("user.dir") + prop.getProperty(file);
        fileToUploadFromConfig = new File(fileFromConfig);
    }

    public static void testMultipleRequests(File fileToUploadGP1, File fileToUploadGP2, File fileToUploadGP3)
            throws InterruptedException, IOException
    {

        LOGGER.info("Test has been started.");
        LOGGER.info("Available processors : " + Runtime.getRuntime().availableProcessors());
        ArrayList<Tasks> tabTasks = new ArrayList<Tasks>();
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

        LOGGER.info("Available processors : " + Runtime.getRuntime());
        for (int i = 0; i < tabTasks.size(); i++)
        {
            Tasks task = tabTasks.get(i);
            LOGGER.info("\n Im the user " + task.getName() + "\n");
            LOGGER.info("\t number of succes request is  : " + task.getNumberOfSuccessRequest() + "/"
                    + task.getTabResponses().size());
            totalSuccessRequest += task.getNumberOfSuccessRequest();
            totalRequest += task.getTabResponses().size();
            for (int j = 0; j < task.getTabResponses().size(); j++)
            {
                LOGGER.info("\t Response  " + (j + 1) + " " + task.getNameOfResponses().get(j));
                LOGGER.info("\t \t code status of response   :" + task.getTabResponses().get(j).getStatusCode());
                LOGGER.info("\t \t time of response " + (j + 1) + " : " + task.getTabResponses().get(j).time());

            }

        }
        LOGGER.info("Total number of users : " + completed.get());
        LOGGER.info("Total  request  : " + totalRequest);
        LOGGER.info("Total success request  : " + totalSuccessRequest);
        LOGGER.info("Percentage  : " + (totalSuccessRequest / totalRequest) * 100 + "%");

    }

    @Test()
    public static void PerforamnceTestInShortDuration() throws InterruptedException, IOException
    {
        Instant start = Instant.now();
        Duration duration = Duration.ofMinutes(2);
        while (Duration.between(start, Instant.now()).compareTo(duration) < 0)
        {
            testMultipleRequests(fileToUploadFromConfig, fileToUploadFromConfig, fileToUploadFromConfig);

        }
    }

    @Test()
    public static void PerforamnceTestWithConfiguredDuration() throws InterruptedException, IOException
    {
        Instant start = Instant.now();
        Duration duration = Duration.ofMinutes(durationOfTest);
        while (Duration.between(start, Instant.now()).compareTo(duration) < 0)
        {
            testMultipleRequests(fileToUploadFromConfig, fileToUploadFromConfig, fileToUploadFromConfig);

        }
    }

}
