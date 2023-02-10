package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class BookmarkTest extends AssertActions
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
    public void documentWithBookmarks()
    {
        String documentId = uploadDocument("bookmark");
        Response response = Documents.getBookmarks(documentId);
        verifyStatusCode(response, 200);
        String responseContent = response.jsonPath().getString("bookmarks");
        Assert.assertTrue(responseContent.contains("title"));
        Assert.assertTrue(responseContent.contains("page"));
        Assert.assertTrue(responseContent.contains("namedDestination"));
        Assert.assertTrue(responseContent.contains("textPosition"));

    }

    @Test()
    public void documentWithoutBookmark()
    {
        String documentId = uploadDocument("imageA");

        Response response2 = Documents.getBookmarks(documentId);
        String responseContent = response2.jsonPath().getString("bookmarks");
        Assert.assertTrue(responseContent.contains("[]"));
    }

    @Test()
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
