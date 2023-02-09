package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class PagesTest extends AssertActions
{

    String documentId = "";

    String documentId2 = "";

    @Test(priority = 1)
    public void uploadDocument()
    {
        Response response = Documents.uploadDocument(file);
        JsonPath jsonPath = JsonPath.from(response.asString());
        System.out.println(response.asString());
        verifyStatusCode(response, 200);
        documentId = jsonPath.get("id");
    }

    @Test(priority = 2)
    public void getPageImageWithoutDescription()
    {
        Response response = Documents.getPageImage(documentId, 0);
        verifyStatusCode(response, 200);
        assertTrue(response.header("content-type").equals("image/png"));

    }

    @Test(priority = 3)
    public void getPageImageWithDescription()
    {
        Response response = Documents.getPageImage(documentId, 0, "IM_200_90_FILTERS~C~35~B~-100~I~50");
        verifyStatusCode(response, 200);
        assertTrue(response.header("content-type").equals("image/png"));

    }

    @Test(priority = 4)
    public void getPageImageWithWrongIdDocument()
    {
        Response response = Documents.getPageImage("bad id", 0);
        verifyStatusCode(response, 404);
    }

    @Test(priority = 5)
    public void getPageImageWithWrongPage()
    {
        Response response = Documents.getPageImage(documentId, 1000);
        verifyStatusCode(response, 500);
    }

    @Test(priority = 1)
    public void uploadDocument2() throws InterruptedException
    {

        // upload a document with text
        Response response = Documents.uploadDocument("pdf");
        // get reponse body
        JsonPath jsonPath = JsonPath.from(response.asString());
        // verify status of request is ok
        verifyStatusCode(response, 200);
        // get th id of document
        documentId2 = jsonPath.get("id");
    }

    @Test(priority = 2)
    public void searchOnDocumentWithText()
    {

        // search for a text on a specific page
        Response response = Documents.searchPageForTextPositions(documentId2, "arondor", 4);

        // verify status of request is ok
        verifyStatusCode(response, 200);
        // Get the content of request
        String responseContent = response.jsonPath().getString("searchResults");
        // get the value of pageNumber
        String nubmerPage = response.jsonPath().getString("searchResults.positionText.pageNumber");

        // verify that the search result contains the field positionText
        Assert.assertTrue(responseContent.contains("positionText"));
        // verify that the number page returned is the number of page entered
        Assert.assertEquals(nubmerPage, "[4]");

    }

    @Test(priority = 3)
    public void searchTextOnNoExistentPage()
    {

        // search for text in a page that does not exsite in the document
        Response response = Documents.searchPageForTextPositions(documentId2, "arondor", 20);

        // verify status of request is ok
        verifyStatusCode(response, 500);

    }

    @Test(priority = 4)
    public void PagesOfSearchResult()
    {

        // make a search to have the numbers of the pages that contain the
        // search word
        Response response = Documents.getPagesSearchResult(documentId2, "arondor");
        // get the list of the numbers of pages
        String nubmerPage = response.jsonPath().getString("pageList");
        String ListOfNumber = "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]";

        // verify status of request is ok
        verifyStatusCode(response, 200);
        // verify that the list is the the correct expected list
        Assert.assertEquals(nubmerPage, ListOfNumber);

    }

    @Test(priority = 5)
    public void searchOnDocumentWithoutText()
    {

        // upload document without text
        Response response = Documents.uploadDocument("imageA");
        JsonPath jsonPath = JsonPath.from(response.asString());
        verifyStatusCode(response, 200);

        documentId2 = jsonPath.get("id");
        Response response2 = Documents.searchPageForTextPositions(documentId2, "arondor", 4);
        String responseContent = response2.jsonPath().getString("searchResults");

        // verify that the list of search result is empty
        Assert.assertEquals(responseContent, "[]");
    }

    @Test(priority = 6)
    public void PagesOfSearchResultOnDocumentWithoutText()
    {

        // upload document without text
        Response response = Documents.uploadDocument("imageA");
        JsonPath jsonPath = JsonPath.from(response.asString());
        documentId2 = jsonPath.get("id");
        // make a search on document without text
        Response response2 = Documents.getPagesSearchResult(documentId2, "arondor");
        // verify status of request is ok
        verifyStatusCode(response2, 200);

        String responseContent = response2.jsonPath().getString("pageList");

        // verify that the list of search result is empty
        Assert.assertEquals(responseContent, "[]");
        // verify that the returned id is the same and is not empty
        Assert.assertEquals(documentId2, response2.jsonPath().getString("uuid.id"));
    }

    @Test(priority = 7)
    public void uploadDocumentwithWrongId()
    {

        //// Make a GET request on the end point searchPageForTextPositions with
        //// an id
        //// of a document
        //// that does not exist.
        Response response = Documents.searchPageForTextPositions("bad id", "arondor", 4);
        // Check that the status of request is failed
        verifyStatusCode(response, 404);

    }

    @Test(priority = 8)
    public void uploadDocumentwithWrongIdforsearchPages()
    {

        //// Make a GET request on the end point getPagesSearchResult with an id
        //// of a document
        //// that does not exist.
        Response response = Documents.getPagesSearchResult("bad id", "arondor");
        // Check that the status of request is failed
        verifyStatusCode(response, 404);

    }

    @Test(priority = 9)
    public void positionTextOnDocumentWithText()
    {

        // search for a text on a specific page
        Response response = Documents.getTextPosition(documentId2, 2);

        // verify status of request is ok
        verifyStatusCode(response, 200);
        // Get the content of request
        String responseContent = response.jsonPath().getString("positionTextList");
        // get the value of pageNumber
        int nubmerPage = response.jsonPath().getInt("pageNumber");

        // verify that the search result contains the field positionText
        Assert.assertTrue(responseContent.contains("position"));
        // verify that the number page returned is the number of page entered
        Assert.assertEquals(nubmerPage, 2);

    }

    @Test(priority = 10)
    public void positionTextOnNoExistentPage()
    {

        // search for text in a page that does not exsite in the document
        Response response = Documents.getTextPosition(documentId2, 20);

        // verify status of request is ok
        verifyStatusCode(response, 500);

    }

    @Test(priority = 11)
    public void positionTextOnDocumentWithoutText()
    {

        // upload document without text
        Response response = Documents.uploadDocument("imageA");
        JsonPath jsonPath = JsonPath.from(response.asString());
        documentId2 = jsonPath.get("id");
        // make a search on document without text
        Response response2 = Documents.getTextPosition(documentId2, 0);
        // verify status of request is ok
        verifyStatusCode(response2, 200);

        String responseContent = response2.jsonPath().getString("positionTextList");

        // verify that the list of search result is empty
        Assert.assertEquals(responseContent, "[]");
    }

    @Test(priority = 12)
    public void positionTextWithWrongId()
    {

        //// Make a GET request on the end point getPagesSearchResult with an id
        //// of a document
        //// that does not exist.
        Response response = Documents.getTextPosition("bad id", 0);
        // Check that the status of request is failed
        verifyStatusCode(response, 404);
    }

}
