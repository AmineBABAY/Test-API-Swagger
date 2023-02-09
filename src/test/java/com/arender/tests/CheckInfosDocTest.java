package com.arender.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class CheckInfosDocTest extends AssertActions
{

    public static String documentId = "";

    @Test(priority = 1)
    public void uploadDocument()
    {

        Response response = Documents.uploadDocument(file);
        JsonPath jsonPath = JsonPath.from(response.asString());
        verifyStatusCode(response, 200);
        documentId = jsonPath.get("id");
    }

    @Test(priority = 2)
    public void casPassant()
    {

        Response response = Documents.getDocument(documentId);
        verifyStatusCode(response, 200);
        Response response2 = Documents.checkDocument(documentId);
        verifyStatusCode(response2, 200);
        ResponseBody body = response.body();
        Assert.assertNotNull(body);

    }

    @Test(priority = 3)
    public void casAbsentDocument()
    {

        Response response = Documents.getDocument("A");
        Response response2 = Documents.getDocument("A");
        verifyStatusCode(response, 404);
        verifyStatusCode(response2, 404);

    }
}
