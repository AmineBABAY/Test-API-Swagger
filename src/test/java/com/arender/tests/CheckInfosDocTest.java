package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class CheckInfosDocTest extends AssertActions
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
    public void getDocumentForPdf()
    {
        String documentId = uploadDocument("pdf");
        Response response = Documents.getDocument(documentId);
        verifyStatusCode(response, 200);
        Assert.assertNotNull(response.body(), "Body of your document is null !");

    }

    @Test()
    public void getDocumentForDoc()
    {
        String documentId = uploadDocument("doc_with_100KO");
        Response response = Documents.getDocument(documentId);
        verifyStatusCode(response, 200);
        Assert.assertNotNull(response.body(), "Body of your document is null !");

    }

    @Test()
    public void getDocumentForJpg()
    {
        String documentId = uploadDocument("imageB");
        Response response = Documents.getDocument(documentId);
        verifyStatusCode(response, 200);
        Assert.assertNotNull(response.body(), "Body of your document is null !");

    }

    @Test()
    public void getDocumentForTiff()
    {
        String documentId = uploadDocument("tiff");
        Response response = Documents.getDocument(documentId);
        verifyStatusCode(response, 200);
        Assert.assertNotNull(response.body(), "Body of your document is null !");

    }

    @Test()
    public void getDocumentForMsg()
    {
        String documentId = uploadDocument("msg");
        Response response = Documents.getDocument(documentId);
        verifyStatusCode(response, 200);
        Assert.assertNotNull(response.body(), "Body of your document is null !");

    }

    @Test()
    public void getDocumentForZip()
    {
        String documentId = uploadDocument("zip");
        Response response = Documents.getDocument(documentId);
        verifyStatusCode(response, 200);
        Assert.assertNotNull(response.body(), "Body of your document is null !");

    }

    @Test()
    public void checkPdfDocument()

    {
        String documentId = uploadDocument("pdf");
        Response response = Documents.checkDocument(documentId);
        verifyStatusCode(response, 200);

    }

    @Test()
    public void checkVideoDocument()

    {
        String documentId = uploadDocument("mp4");
        Response response = Documents.checkDocument(documentId);
        verifyStatusCode(response, 200);

    }

    @Test()
    public void checkGifDocument()

    {
        String documentId = uploadDocument("gif");
        Response response = Documents.checkDocument(documentId);
        verifyStatusCode(response, 200);

    }

    @Test()
    public void checkPngDocument()

    {
        String documentId = uploadDocument("png");
        Response response = Documents.checkDocument(documentId);
        verifyStatusCode(response, 200);

    }

    @Test()
    public void checkTiffDocument()

    {
        String documentId = uploadDocument("tiff");
        Response response = Documents.checkDocument(documentId);
        verifyStatusCode(response, 200);

    }

    @Test()
    public void checkZipDocument()

    {
        String documentId = uploadDocument("zip");
        Response response = Documents.checkDocument(documentId);
        verifyStatusCode(response, 200);

    }

    @Test()
    public void checkEmlDocument()

    {
        String documentId = uploadDocument("eml");
        Response response = Documents.checkDocument(documentId);
        verifyStatusCode(response, 200);

    }

    @Test()
    public void checkDocDocument()

    {
        String documentId = uploadDocument("doc_with_100KO");
        Response response = Documents.checkDocument(documentId);
        verifyStatusCode(response, 200);

    }

    @Test()
    public void getDocumentWithWrongDocumentIdTest()
    {

        Response response = Documents.getDocument("A");
        verifyStatusCode(response, 404);

    }

    @Test()
    public void checkDocumentWithWrongDocumentIdTest()
    {
        Response response = Documents.checkDocument("A");
        verifyStatusCode(response, 404);
    }
}
