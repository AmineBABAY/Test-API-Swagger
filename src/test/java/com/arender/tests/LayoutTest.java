package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class LayoutTest extends AssertActions
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
    public void getDocumentLayoutForDoc()
    {
        String docxDocumentId = uploadDocument("docx");
        Response response = Documents.getDocumentLayout(docxDocumentId);

        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("type").toString().contains("DocumentPageLayout"));

    }

    @Test()
    public void getDocumentLayoutForZip()
    {
        String zipDocumentId = uploadDocument("zip");
        Response response = Documents.getDocumentLayout(zipDocumentId);

        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("children.children.documentId.id").toString().contains("/1/1"));
        assertTrue(jsonPath.get("type").toString().contains("DocumentContainer"));
    }

    @Test()
    public void getDocumentLayoutForPdf()
    {
        String pdfDocumentId = uploadDocument("pdf");
        Response response = Documents.getDocumentLayout(pdfDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("pageDimensionsList.dpi").toString() != null);
    }

    @Test()
    public void getDocumentLayoutForTiff()
    {
        String tiffDocumentId = uploadDocument("tiff");
        Response response = Documents.getDocumentLayout(tiffDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("pageDimensionsList.dpi").toString().equals("[300]"));
    }

    @Test()
    public void getDocumentLayoutForMsg()
    {
        String txtDocumentId = uploadDocument("msg");
        Response response = Documents.getDocumentLayout(txtDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("children.mimeType").toString().contains("text/html"));
        assertTrue(jsonPath.get("type").toString().contains("DocumentContainer"));
    }

    @Test()
    public void getDocumentLayoutForJpg()
    {
        String jpegDocumentId = uploadDocument("imageA");
        Response response = Documents.getDocumentLayout(jpegDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("pageDimensionsList.dpi").toString() != null);
    }

}
