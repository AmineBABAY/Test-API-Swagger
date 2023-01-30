package com.arender.tests;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.actions.Tasks;

public class PerformanceTest extends AssertActions
{
    private final static Logger LOGGER = Logger.getLogger(PerformanceTest.class);

    @Test(priority = 1)
    public static void testMultipleRequests() throws InterruptedException
    {   int totalSuccessRequest=0;
        int totalRequest=0;
        LOGGER.info("Test has been started.");
        LOGGER.info("Available processors : " + Runtime.getRuntime().availableProcessors());
        ArrayList<Tasks> tabTasks = new ArrayList<Tasks>();
        ExecutorService executorPDF = Executors.newFixedThreadPool(numberOfUsers);
        ExecutorService executorTXT = Executors.newFixedThreadPool(numberOfUsers);
        ExecutorService executorDOCX = Executors.newFixedThreadPool(numberOfUsers);
        AtomicInteger completed = new AtomicInteger();
        for (int i = 1; i <= numberOfUsers; i++)
        {

            executorPDF.submit(() -> {
                try
                {
                    tabTasks.add(new Tasks("pdf"));
                    completed.incrementAndGet();
                }
                catch (Exception e)
                {
                    LOGGER.error(e.getMessage());
                }

            });
            executorTXT.submit(() -> {
                try
                {

                    tabTasks.add(new Tasks("txt"));
                    completed.incrementAndGet();
                }
                catch (Exception e)
                {
                    LOGGER.error(e.getMessage());
                    ;
                }

            });
            executorDOCX.submit(() -> {
                try
                {
                    tabTasks.add(new Tasks("docx"));
                    completed.incrementAndGet();
                }
                catch (Exception e)
                {
                    LOGGER.error(e.getMessage());
                }

            });
        }

        executorPDF.shutdown();
        executorPDF.awaitTermination(10, TimeUnit.MINUTES);
        executorTXT.shutdown();
        executorTXT.awaitTermination(10, TimeUnit.MINUTES);
        executorDOCX.shutdown();
        executorDOCX.awaitTermination(10, TimeUnit.MINUTES);
        LOGGER.info("Available processors : " + Runtime.getRuntime());
        for (int i = 0; i < tabTasks.size(); i++)
        {
            Tasks task = tabTasks.get(i);
            LOGGER.info("\n Im the user " + task.getName() + "\n");
            LOGGER.info("\t number of succes request is  : " + task.getNumberOfSuccessRequest() + "/" +task.getTabResponses().size());
            totalSuccessRequest+=task.getNumberOfSuccessRequest();
            totalRequest+=task.getTabResponses().size();
            for (int j = 0; j < task.getTabResponses().size(); j++)
            {
                //LOGGER.info("Response : " +task.getTabResponses());
                LOGGER.info("\t code status of response  " + (j + 1) + " : "
                        + task.getTabResponses().get(j).getStatusCode());
                LOGGER.info("\t time of response " + (j + 1) + " : " + task.getTabResponses().get(j).time());

            }

        }
        LOGGER.info("Total number of users : " + completed.get());
        LOGGER.info("Total  request  : " + totalRequest);
        LOGGER.info("Total success request  : " + totalSuccessRequest);
    }
    @Test(priority = 2)
    public static void scheduledTestDuration()
    {
        
    }
}
