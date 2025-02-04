package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class PrintableTest extends AssertActions
{

    private String bodyContains = "" + "<<" + "\n/Type /Action" + "\n/S /JavaScript"
            + "\n/JS (this.print\\(\\);this.closeDoc\\(true\\))" + "\n>>";

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
    public void getPrintableDocumentOfPdf()
    {
        String documentId = uploadDocument("pdf");
        Response response = Documents.getPrintableDocument(documentId);
        verifyStatusCode(response, 200);
        assertTrue(String.valueOf(response.getHeaders().get("content-type")).equals("Content-Type=application/pdf"));

        assertTrue(response.asString().contains(bodyContains),
                "the body of your response does not contains the right code");
    }

    @Test()
    public void getPrintableDocumentOfJpg()
    {
        String documentId = uploadDocument("jpg");
        Response response = Documents.getPrintableDocument(documentId);
        verifyStatusCode(response, 200);
        assertTrue(String.valueOf(response.getHeaders().get("content-type")).equals("Content-Type=application/pdf"));

        assertTrue(response.asString().contains(bodyContains),
                "the body of your response does not contains the right code");
    }

    @Test()
    public void getPrintableDocumentOfTiff()
    {
        String documentId = uploadDocument("tiff");
        Response response = Documents.getPrintableDocument(documentId);
        verifyStatusCode(response, 200);
        assertTrue(String.valueOf(response.getHeaders().get("content-type")).equals("Content-Type=application/pdf"));

        assertTrue(response.asString().contains(bodyContains),
                "the body of your response does not contains the right code");
    }

    @Test()
    public void getPrintableDocumentOfDoc()
    {
        String documentId = uploadDocument("doc_with_100KO");
        Response response = Documents.getPrintableDocument(documentId);
        verifyStatusCode(response, 200);
        assertTrue(String.valueOf(response.getHeaders().get("content-type")).equals("Content-Type=application/pdf"));

        assertTrue(response.asString().contains(bodyContains),
                "the body of your response does not contains the right code");
    }

    @Test()
    public void getPrintableDocumentOfZip()
    {
        String documentId = uploadDocument("zip");
        Response response = Documents.getPrintableDocument(documentId);
        verifyStatusCode(response, 200);
        assertTrue(String.valueOf(response.getHeaders().get("content-type")).equals("Content-Type=application/pdf"));

        assertTrue(response.asString().contains(bodyContains),
                "the body of your response does not contains the right code");
    }

    @Test()
    public void getPrintableDocumentWithWrongDocumentIdTest()
    {
        Response response = Documents.getPrintableDocument("bad id");
        verifyStatusCode(response, 404);
    }

}
