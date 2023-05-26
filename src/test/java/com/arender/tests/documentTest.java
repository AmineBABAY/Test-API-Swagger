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
        String documentId = uploadDocument("pdf");

    }

    @Test()
    public void uploadJpgDocument()
    {
        String documentId = uploadDocument("jpg");

    }

    @Test()
    public void uploadTiffDocument()
    {
        String documentId = uploadDocument("tiff");

    }

    @Test()
    public void uploadDocDocument()
    {
        String documentId = uploadDocument("doc_with_100KO");

    }

    @Test()
    public void uploadZipDocument()
    {
        String documentId = uploadDocument("zip");

    }

    @Test()
    public void uploadEmlDocument()
    {
        String documentId = uploadDocument("eml");

    }

    @Test()
    public void uploadMp4Document()
    {
        String documentId = uploadDocument("mp4");

    }

}
