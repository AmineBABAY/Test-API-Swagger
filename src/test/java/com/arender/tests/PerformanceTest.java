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
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.actions.Tasks;

public class PerformanceTest extends AssertActions
{   private static File pdf;
    private static File txt;
    private static File jpeg;
    
    private final static Logger LOGGER = Logger.getLogger(PerformanceTest.class);
    private static int totalSuccessRequest=0;
    private static int totalRequest=0;
    
    @BeforeTest
    public static void initialization()
    {
        String filepathPDF = System.getProperty("user.dir") + prop.getProperty("pdf");
        pdf = new File(filepathPDF);
        String filepathTXT = System.getProperty("user.dir") + prop.getProperty("txt");
        txt = new File(filepathTXT);
        String filepathJPPEG = System.getProperty("user.dir") + prop.getProperty("jpeg");
        jpeg = new File(filepathJPPEG);
    }

    @Test(priority = 1)
    public static void testMultipleRequests() throws InterruptedException, IOException
    {   
    	

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
                    tabTasks.add(new Tasks(pdf));
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

                    tabTasks.add(new Tasks(txt));
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
                    tabTasks.add(new Tasks(jpeg));
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
                LOGGER.info("\t Response  "+ (j + 1) +" " +task.getNameOfResponses().get(j));
                LOGGER.info("\t \t code status of response   :" 
                        + task.getTabResponses().get(j).getStatusCode());
                LOGGER.info("\t \t time of response " + (j + 1) + " : " + task.getTabResponses().get(j).time());

            }

        }
        LOGGER.info("Total number of users : " + completed.get());
        LOGGER.info("Total  request  : " + totalRequest);
        LOGGER.info("Total success request  : " + totalSuccessRequest);
        LOGGER.info("Percentage  : " + (totalSuccessRequest/totalRequest)*100 +"%");

    }
//    @Test(priority = 2)
//    public static void scheduledTestDuration() throws InterruptedException, IOException
//    {      Instant start = Instant.now();
//           Duration duration = Duration.ofMinutes(20);
//        while(Duration.between(start, Instant.now()).compareTo(duration) < 0)
//        {
//            testMultipleRequests();
//
//        }
//
//    }
}
