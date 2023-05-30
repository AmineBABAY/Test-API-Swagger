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
    public void getPageImageWithoutDescription()
    {
        String documentId = uploadDocument("pdf");
        Response response = Documents.getPageImage(documentId, 0);
        verifyStatusCode(response, 200);
        assertTrue(response.header("content-type").equals("image/png"));

    }

    @Test()
    public void getPageImageWithDescription()
    {
        String documentId = uploadDocument("pdf");
        Response response = Documents.getPageImage(documentId, 0, "IM_200_90_FILTERS~C~35~B~-100~I~50");
        verifyStatusCode(response, 200);
        assertTrue(response.header("content-type").equals("image/png"));

    }

    @Test()
    public void getPageImageWithWrongIdDocument()
    {
        Response response = Documents.getPageImage("bad id", 0);
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getPageImageWithWrongPage()
    {
        String documentId = uploadDocument("pdf");
        Response response = Documents.getPageImage(documentId, 1000);
        verifyStatusCode(response, 500);
    }

    @Test()
    public void searchOnDocumentWithText()
    {

        String documentId = uploadDocument("pdf");
        Response response = Documents.searchPageForTextPositions(documentId, "arondor", 4);

        verifyStatusCode(response, 200);

        String responseContent = response.jsonPath().getString("searchResults");

        String nubmerPage = response.jsonPath().getString("searchResults.positionText.pageNumber");

        Assert.assertTrue(responseContent.contains("positionText"));

        Assert.assertEquals(nubmerPage, "[4]");

    }

    @Test()
    public void searchOnDocumentWithNoExistingText()
    {

        String documentId = uploadDocument("pdf");
        Response response = Documents.searchPageForTextPositions(documentId, "testtest", 4);

        verifyStatusCode(response, 200);

        String responseContent = response.jsonPath().getString("searchResults");

        Assert.assertEquals(responseContent, "[]");

    }

    @Test()
    public void searchTextOnNoExistentPage()
    {
        String documentId = uploadDocument("pdf");
        // search for text in a page that does not exsite in the document
        Response response = Documents.searchPageForTextPositions(documentId, "arondor", 20);

        // verify status of request is ok
        verifyStatusCode(response, 400);

    }

    @Test()
    public void PagesOfSearchResultWithExistingText()
    {
        String documentId = uploadDocument("pdf");
        Response response = Documents.getPagesSearchResult(documentId, "arondor");
        verifyStatusCode(response, 200);
        String nubmerPage = response.jsonPath().getString("pageList");
        String searchText = response.jsonPath().getString("searchText");
        String ListOfNumber = "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]";

        Assert.assertEquals(nubmerPage, ListOfNumber);
        Assert.assertEquals(searchText, "arondor");

    }

    @Test()
    public void PagesOfSearchResultWithNonExistentText()
    {

        String documentId = uploadDocument("pdf");
        Response response = Documents.getPagesSearchResult(documentId, "testtest");
        verifyStatusCode(response, 200);

        String responseContent = response.jsonPath().getString("pageList");
        String searchText = response.jsonPath().getString("searchText");

        Assert.assertEquals(documentId, response.jsonPath().getString("uuid.id"));
        Assert.assertEquals(searchText, "testtest");
        Assert.assertEquals(responseContent, "[]");
    }

    @Test()
    public void searchOnDocumentWithoutText()
    {

        String documentId = uploadDocument("imageA");

        Response response = Documents.searchPageForTextPositions(documentId, "arondor", 4);
        verifyStatusCode(response, 200);
        String responseContent = response.jsonPath().getString("searchResults");

        Assert.assertEquals(responseContent, "[]");
    }

    @Test()
    public void SearchResultWithWrongId()
    {

        Response response = Documents.searchPageForTextPositions("bad id", "arondor", 4);
        verifyStatusCode(response, 404);

    }

    @Test()
    public void uploadDocumentwithWrongIdforsearchPages()
    {

        Response response = Documents.getPagesSearchResult("bad id", "arondor");
        // Check that the status of request is failed
        verifyStatusCode(response, 404);

    }

    @Test()
    public void positionTextOnDocumentWithText()
    {
        String documentId = uploadDocument("pdf");
        // search for a text on a specific page
        Response response = Documents.getTextPosition(documentId, 2);

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

    @Test()
    public void positionTextOnNoExistingPage()
    {
        String documentId = uploadDocument("pdf");
        // search for text in a page that does not exsite in the document
        Response response = Documents.getTextPosition(documentId, 20);

        // verify status of request is ok
        verifyStatusCode(response, 400);

    }

    @Test()
    public void positionTextOnDocumentWithoutText()
    {

        String documentId = uploadDocument("imageA");

        Response response2 = Documents.getTextPosition(documentId, 0);
        verifyStatusCode(response2, 200);

        String responseContent = response2.jsonPath().getString("positionTextList");

        // verify that the list of search result is empty
        Assert.assertEquals(responseContent, "[]");
    }

    @Test()
    public void positionTextOnDocumentWithoutTextOnNoExistentPage()
    {
        String documentId = uploadDocument("imageA");
        // search for text in a page that does not exsite in the document
        Response response = Documents.getTextPosition(documentId, 20);

        // verify status of request is ok
        verifyStatusCode(response, 400);

    }

    @Test()
    public void positionTextWithWrongId()
    {
        Response response = Documents.getTextPosition("bad id", 0);
        // Check that the status of request is failed
        verifyStatusCode(response, 404);
    }

}
