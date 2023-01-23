package com.arender.tests;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.actions.Tasks;

public class PerformanceTest extends AssertActions
{

    String documentId = "";

    @Test(priority = 1)
    public static void uploadDocument() throws InterruptedException
    {
        ArrayList<Tasks> tabTasks = new ArrayList<Tasks>();
        ExecutorService executorPDF = Executors.newFixedThreadPool(numberOfUsers);
        ExecutorService executorTXT = Executors.newFixedThreadPool(numberOfUsers);
        ExecutorService executorDOCX = Executors.newFixedThreadPool(numberOfUsers);
        AtomicInteger completed = new AtomicInteger();
        for (int i = 1; i <= numberOfUsers; i++)
        {

            executorPDF.submit(() -> {
                tabTasks.add(new Tasks("pdf"));
                completed.incrementAndGet();

            });
            executorTXT.submit(() -> {
                tabTasks.add(new Tasks("txt"));
                completed.incrementAndGet();
            });
            executorDOCX.submit(() -> {
                tabTasks.add(new Tasks("docx"));
                completed.incrementAndGet();

            });
        }

        executorPDF.shutdown();
        executorPDF.awaitTermination(10, TimeUnit.MINUTES);
        executorTXT.shutdown();
        executorTXT.awaitTermination(10, TimeUnit.MINUTES);
        executorDOCX.shutdown();
        executorDOCX.awaitTermination(10, TimeUnit.MINUTES);
        //System.out.println(completed.get());
        for (int i = 0; i < tabTasks.size(); i++)
        {
            Tasks task=tabTasks.get(i);
            System.out.println("\t user name : " + task.getName());
            System.out.println("\t number of succes request is  : " + task.getNumberOfSuccessRequest());
            for (int j = 0; j < task.getTabResponses().size(); j++)
            {
                
            System.out.print("\t code status of response  : " + (j+1) + " "+ task.getTabResponses().get(j).getStatusCode());
            System.out.print("\t time of response "+ (j+1)  +" : "+ task.getTabResponses().get(j).time());
            System.out.println(" ");
        }

    }
        System.out.println("Total number of users : " + completed.get());
}
}
