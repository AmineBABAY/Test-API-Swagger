package com.arender.tests;

import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.HealthRecordsViewer;

import io.restassured.response.Response;

public class HealthRecordsViewerTest extends AssertActions
{

    @Test()
    public void checkReadiness()
    {
        Response response = HealthRecordsViewer.readiness();
        verifyStatusCode(response, 200);
    }

    public void checkDisplayRecords()
    {
        Response response = HealthRecordsViewer.displayRecords();
        verifyStatusCode(response, 200);
    }
}