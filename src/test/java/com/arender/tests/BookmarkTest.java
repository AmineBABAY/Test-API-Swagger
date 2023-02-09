package com.arender.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class BookmarkTest extends AssertActions
{

    String documentId = "";

    @Test(priority = 1)
    public void uploadDocument() throws InterruptedException
    {

        // upload document with bookmark
        Response response = Documents.uploadDocument("bookmark");
        // get reponse body
        JsonPath jsonPath = JsonPath.from(response.asString());
        // verify status of request is ok
        verifyStatusCode(response, 200);
        // get th id of document
        documentId = jsonPath.get("id");
    }

    @Test(priority = 2)
    public void documentWithBookmarks()
    {

        // Make a GET request on the end point bookmark
        Response response = Documents.getBookmarks(documentId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        // Get the list of boomark
        String responseContent = response.jsonPath().getString("bookmarks");
        System.out.print(responseContent);
        // verify that the list of bookmark is not empty
        Assert.assertTrue(responseContent.contains("title"));
        Assert.assertTrue(responseContent.contains("page"));
        Assert.assertTrue(responseContent.contains("namedDestination"));
        Assert.assertTrue(responseContent.contains("textPosition"));

    }

    @Test(priority = 3)
    public void documentWithoutBookmark()
    {

        // upload document without bookmark from rendition
        Response response = Documents.uploadDocument("imageA");
        JsonPath jsonPath = JsonPath.from(response.asString());
        verifyStatusCode(response, 200);

        documentId = jsonPath.get("id");
        Response response2 = Documents.getBookmarks(documentId);
        String responseContent = response2.jsonPath().getString("bookmarks");

        // verify that the list of bookmark is empty
        Assert.assertTrue(responseContent.contains("[]"));
    }

    @Test(priority = 4)
    public void uploadDocumentwithWrongId()
    {

        //// Make a GET request on the end point bookmark with an id of a
        //// document
        //// that does not exist.
        Response response = Documents.getBookmarks("bad id");
        // Check that the status of request is failed
        verifyStatusCode(response, 404);

    }

}
