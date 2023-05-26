package com.arender.tests;

import static org.testng.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class DestinationTest extends AssertActions
{

    private String uploadDocument(String f)
    {
        String id;
        Response response = Documents.uploadDocument(f);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        id = jsonPath.get("id");
        assertTrue(id != null && !id.isEmpty(), "Your id is empty or null");
        return id;
    }

    @Test()
    public void getNameDestinationForDocumentWithDestination() throws UnsupportedEncodingException
    {
        String documentId = uploadDocument("destination");
        // Make a GET request on the end point destinations
        Response response = Documents.getNameDestination(documentId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        // Get the result content of request
        String responseContent = response.jsonPath().getString("namedDestinations");

        // check the namedestination contains the attribute name
        Assert.assertTrue(responseContent.contains("name"));
        // check the namedestination contains the attribute page
        Assert.assertTrue(responseContent.contains("page"));

    }

    @Test()
    public void documentWithoutDestination() throws UnsupportedEncodingException
    {
        String documentId = uploadDocument("without_destination");
        // Make a GET request on the end point destinations
        Response response = Documents.getNameDestination(documentId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        // Get the result content of request
        String responseContent = response.jsonPath().getString("namedDestinations");

        // check the namedestination is empty !
        Assert.assertTrue(responseContent.contains("[]"));

    }

    @Test()
    public void getNameDestinationWithWrongId()
    {

        //// Make a GET request on the end point destinations with an id of a
        //// document
        //// that does not exist.
        Response response = Documents.getNameDestination("bad id");
        // Check that the status of request is failed
        verifyStatusCode(response, 404);
    }

}
