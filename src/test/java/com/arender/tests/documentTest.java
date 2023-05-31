package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class documentTest extends AssertActions
{

    private String uploadDocument(String file)
    {
        Response response = Documents.uploadDocument(file);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        String id = jsonPath.get("id");
        assertTrue(id != null && !id.isEmpty(), "Your id is empty or null");
        return id;
    }

    @Test()
    public void uploadPdfDocument()
    {
        uploadDocument("pdf");

    }

    @Test()
    public void uploadJpgDocument()
    {
        uploadDocument("jpg");

    }

    @Test()
    public void uploadTiffDocument()
    {
        uploadDocument("tiff");

    }

    @Test()
    public void uploadDocDocument()
    {
        uploadDocument("doc_with_100KO");

    }

    @Test()
    public void uploadZipDocument()
    {
        uploadDocument("zip");

    }

    @Test()
    public void uploadEmlDocument()
    {
        uploadDocument("eml");

    }

    @Test()
    public void uploadMp4Document()
    {
        uploadDocument("mp4");

    }

    @Test()
    public void getDocumentWithWrongDocumentId()
    {

        Response response = Documents.getDocument("bad id");
        verifyStatusCode(response, 404);

    }

    @Test()
    public void getBookmarksWithWrongId()
    {

        Response response = Documents.getBookmarks("bad id");
        verifyStatusCode(response, 404);

    }

    @Test()
    public void checkDocumentWithWrongDocumentId()
    {
        Response response = Documents.checkDocument("bad id");
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getNameDestinationWithWrongId()
    {

        Response response = Documents.getNameDestination("bad id");
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getDocumentContentWithWrongId()
    {

        Response response = Documents.getDocumentContent("bad id");
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getDocumentAnnotationsWithWrongId()
    {

        Response response = Documents.getDocumentAnnotations("bad id");
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getFileChunkWithWrongId()
    {

        Response response = Documents.getFileChunk("bad id", "bytes=5-3");
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getDocumentLayoutWithWrongId()
    {

        Response response = Documents.getDocumentLayout("bad id");
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getPagesSearchResultWithWrongId()
    {

        Response response = Documents.getPagesSearchResult("bad id", "any param");
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getPageImageWithWrongId()
    {

        Response response = Documents.getPageImage("bad id", 0);
        verifyStatusCode(response, 404);
    }

    @Test()
    public void SearchForTextPositionsWithWrongId()
    {

        Response response = Documents.searchPageForTextPositions("bad id", "bad param", 0);
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getTextPositionWithWrongId()
    {

        Response response = Documents.getTextPosition("bad id", 0);
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getPrintableDocumentWithWrongId()
    {

        Response response = Documents.getPrintableDocument("bad id");
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getSignaturesWithWrongId()
    {

        Response response = Documents.getSignatures("bad id");
        verifyStatusCode(response, 404);
    }
}
