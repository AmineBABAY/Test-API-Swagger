package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class SignatureTest extends AssertActions
{

    private String uploadDocument(String f)
    {
        Response response = Documents.uploadDocument(f);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        String id = jsonPath.get("id");
        assertTrue(id != null && !id.isEmpty(), "Your id is empty or null");
        return id;
    }

    @Test()
    public void documentWithSignatures()
    {

        String documentId = uploadDocument("signature");
        Response response = Documents.getSignatures(documentId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        // Get the list of signatures
        String responseContent = response.jsonPath().getString("signatures");
        System.out.print(responseContent);
        // verify that the list of signature is not empty
        Assert.assertTrue(responseContent.contains("name"));
        Assert.assertTrue(responseContent.contains("date"));
        Assert.assertTrue(responseContent.contains("signer"));
        Assert.assertTrue(responseContent.contains("location"));
        Assert.assertTrue(responseContent.contains("reason"));

    }

    @Test()
    public void documentWithoutSignature()
    {

        // upload document without signatures
        String documentId = uploadDocument("imageA");

        Response response = Documents.getSignatures(documentId);
        String responseContent = response.jsonPath().getString("signatures");

        // verify that the list of signatures is empty
        Assert.assertTrue(responseContent.contains("[]"));
    }

    @Test()
    public void getSignaturesWithWrongId()
    {

        //// Make a GET request on the end point signature with an id of a
        //// document
        //// that does not exist.
        Response response = Documents.getSignatures("bad id");
        // Check that the status of request is failed
        verifyStatusCode(response, 404);

    }

}
