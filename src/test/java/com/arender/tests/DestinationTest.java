package com.arender.tests;

import java.io.UnsupportedEncodingException;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class DestinationTest extends AssertActions
{

    String documentId = "";

    @Test(priority = 1)
    public void uploadDocument() throws InterruptedException
    {

        // upload document

        Response response = Documents.uploadDocument("destination");

        // get reponse body
        JsonPath jsonPath = JsonPath.from(response.asString());
        // verify status of request is ok
        verifyStatusCode(response, 200);
        // get th id of document
        documentId = jsonPath.get("id");
    }

    @Test(priority = 2)
    public void casPassant() throws UnsupportedEncodingException
    {

        // Make a GET request on the end point destinations
        Response response = Documents.getNameDestination(documentId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        // Get the result content of request
        String responseContent = response.jsonPath().getString("namedDestinations");

        // if the content of namedestinations is not empty
        if (responseContent.contains("[]"))

        {

            // if not check that the content of namedestination is empty
            throw new SkipException("The list is empty the document does not contain namedestination");

        }
        else
        {
            // check the namedestination contains the attribute name
            Assert.assertTrue(responseContent.contains("name"));
            // check the namedestination contains the attribute page
            Assert.assertTrue(responseContent.contains("page"));
        }

    }

    @Test(priority = 3)
    public void casBloquant()
    {

        //// Make a GET request on the end point destinations with an id of a
        //// document
        //// that does not exist.
        Response response = Documents.getNameDestination("bad id");
        // Check that the status of request is failed
        verifyStatusCode(response, 404);
    }

}
