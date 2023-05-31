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
    public void docxDocumentWithBookmarks()
    {
        String documentId = uploadDocument("bookmark_docx");
        Response response = Documents.getBookmarks(documentId);
        verifyStatusCode(response, 200);
        String responseContent = response.jsonPath().getString("bookmarks");
        Assert.assertTrue(responseContent.contains("title"));
        Assert.assertTrue(responseContent.contains("page"));
        Assert.assertTrue(responseContent.contains("namedDestination"));
        Assert.assertTrue(responseContent.contains("textPosition"));

    }

    @Test()
    public void pptDocumentWithBookmarks()
    {
        String documentId = uploadDocument("bookmark_ppt");
        Response response = Documents.getBookmarks(documentId);
        verifyStatusCode(response, 200);
        String responseContent = response.jsonPath().getString("bookmarks");
        Assert.assertTrue(responseContent.contains("title"));
        Assert.assertTrue(responseContent.contains("page"));
        Assert.assertTrue(responseContent.contains("namedDestination"));
        Assert.assertTrue(responseContent.contains("textPosition"));

    }

    @Test()
    public void pdfDocumentWithBookmarks()
    {
        String documentId = uploadDocument("bookmark_pdf");
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
        String documentId = uploadDocument("bookmark_empty");

        Response response2 = Documents.getBookmarks(documentId);
        String responseContent = response2.jsonPath().getString("bookmarks");
        Assert.assertTrue(responseContent.contains("[]"));
    }

}
