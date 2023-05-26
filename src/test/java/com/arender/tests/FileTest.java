package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class FileTest extends AssertActions
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
    public void getDocumentAndCheckFormatForPdf()
    {
        String documentId = uploadDocument("pdf");
        Response response = Documents.getDocumentContent(documentId, "pdf");
        verifyStatusCode(response, 200);
        assertTrue(response.getHeaders().get("Content-Type").toString().contains("application/pdf"),
                "This file does not contain the correct format");

    }

    @Test()
    public void getDocumentAndCheckFormatForDoc()
    {
        String documentId = uploadDocument("doc_with_100KO");
        Response response = Documents.getDocumentContent(documentId, "doc");
        verifyStatusCode(response, 200);
        assertTrue(response.getHeaders().get("Content-Type").toString().contains("application/msword"),
                "This file does not contain the correct format");

    }

    @Test()
    public void getDocumentAndCheckFormatForTiff()
    {
        String documentId = uploadDocument("tiff");
        Response response = Documents.getDocumentContent(documentId, "tiff");
        verifyStatusCode(response, 200);
        assertTrue(response.getHeaders().get("Content-Type").toString().contains("image/tiff"),
                "This file does not contain the correct format");

    }

    @Test()
    public void getDocumentAndCheckFormatForPng()
    {
        String documentId = uploadDocument("png");
        Response response = Documents.getDocumentContent(documentId, "png");
        verifyStatusCode(response, 200);
        assertTrue(response.getHeaders().get("Content-Type").toString().contains("image/png"),
                "This file does not contain the correct format");

    }

    @Test()
    public void getDocumentAndCheckFormatForEml()
    {
        String documentId = uploadDocument("eml");
        Response response = Documents.getDocumentContent(documentId, "eml");
        verifyStatusCode(response, 200);
        assertTrue(response.getHeaders().get("Content-Type").toString().contains("message/rfc822"),
                "This file does not contain the correct format");

    }

    @Test()
    public void getDocumentWithConvertType()
    {
        String documentId = uploadDocument("docx");
        Response response = Documents.getDocumentContent(documentId, "txt");
        verifyStatusCode(response, 200);
        assertTrue(response.getHeaders().get("Content-Type").toString().contains("text"),
                "This file does not contain the correct format");
    }

    @Test()
    public void getDocumentWithDifferentFormat()
    {
        String documentId = uploadDocument("docx");
        Response response = Documents.getDocumentContent(documentId, "avi");
        verifyStatusCode(response, 406);
    }

}
