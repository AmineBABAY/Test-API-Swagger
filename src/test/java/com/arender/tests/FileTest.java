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
    public void getDocumentAndCheckFormat()
    {
        String documentId = uploadDocument("docx");
        Response response = Documents.getDocumentContent(documentId, "docx");
        verifyStatusCode(response, 200);
        assertTrue(response.getHeaders().get("Content-Type").toString().contains("officedocument"),
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
