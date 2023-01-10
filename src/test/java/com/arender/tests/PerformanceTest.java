package com.arender.tests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class PerformanceTest extends AssertActions
{
    
    String documentId="";
    
    
    @Test(priority =1)
    public void  uploadDocument() throws InterruptedException
    {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);

        // Submit 10 tasks to the ExecutorService
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                Response response= Documents.uploadDocument(file);
                verifyStatusCode(response, 200);
            });
        }

        // Shut down the ExecutorService and wait for all tasks to complete
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

    }
}
