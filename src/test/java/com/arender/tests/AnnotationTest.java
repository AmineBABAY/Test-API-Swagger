package com.arender.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class AnnotationTest extends AssertActions
{

    @Test(priority = 1)
    private String uploadDocument(String f)
    {
        String id;
        Response response = Documents.uploadDocument(f);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        id = jsonPath.get("id");
        Assert.assertTrue(id != null && !id.isEmpty(), "Your id is empty or null");
        return id;
    }

    /**
     * Make request on the getDocumentAnnotations and check the result body is a
     * file
     **/
    @Test(priority = 2)
    public void getDocumentAnnoationTest()
    {
        String id = uploadDocument("annotation");
        Response response = Documents.getDocumentAnnotations(id);
        verifyStatusCode(response, 200);
        String contentType = response.header("Content-Type");
        Assert.assertTrue(contentType.startsWith("application/"));

    }

}
