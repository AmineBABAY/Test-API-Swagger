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
    public void getDocumentLayoutForDocx()
    {
        String docxDocumentId = uploadDocument("docx");
        Response response = Documents.getDocumentLayout(docxDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("type").toString().contains("DocumentPageLayout"),
                "The document layout is not present !");
        assertTrue(jsonPath.get("pageDimensionsList.dpi").toString() != null, "The value of DPI is null !");

    }

    @Test()
    public void getDocumentLayoutForZip()
    {
        String zipDocumentId = uploadDocument("zip");
        Response response = Documents.getDocumentLayout(zipDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("children.children.documentId.id").toString().contains("/1/1"),
                "Problem with the children of zip");
        assertTrue(jsonPath.get("type").toString().contains("DocumentContainer"),
                "The document layout is not present !");
    }

    @Test()
    public void getDocumentLayoutForPdf()
    {
        String pdfDocumentId = uploadDocument("pdf");
        Response response = Documents.getDocumentLayout(pdfDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("type").toString().contains("DocumentPageLayout"),
                "The document layout is not present !");
        assertTrue(jsonPath.get("pageDimensionsList.dpi").toString() != null, "The value of DPI is null !");
    }

    @Test()
    public void getDocumentLayoutForOrientedTiff()
    {
        String tiffDocumentId = uploadDocument("tiff");
        Response response = Documents.getDocumentLayout(tiffDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("type").toString().contains("DocumentPageLayout"),
                "The document layout is not present !");
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
        assertTrue(jsonPath.get("type").toString().contains("DocumentContainer"),
                "The document layout is not present !");
    }

    @Test()
    public void getDocumentLayoutForJpg()
    {
        String jpegDocumentId = uploadDocument("imageA");
        Response response = Documents.getDocumentLayout(jpegDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("type").toString().contains("DocumentPageLayout"),
                "The document layout is not present !");
        assertTrue(jsonPath.get("pageDimensionsList.dpi").toString() != null, "The value of DPI is null !");
    }

    @Test()
    public void getDocumentLayoutForTxt()
    {
        String jpegDocumentId = uploadDocument("txt");
        Response response = Documents.getDocumentLayout(jpegDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("type").toString().contains("DocumentPageLayout"),
                "The document layout is not present !");
        assertTrue(jsonPath.get("pageDimensionsList.dpi").toString() != null, "The value of DPI is null !");
    }

    @Test()
    public void getDocumentLayoutForTiff()
    {
        String jpegDocumentId = uploadDocument("tiff_with_1MO");
        Response response = Documents.getDocumentLayout(jpegDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("type").toString().contains("DocumentPageLayout"),
                "The document layout is not present !");
        assertTrue(jsonPath.get("pageDimensionsList.dpi").toString() != null, "The value of DPI is null !");
    }

    @Test()
    public void getDocumentLayoutForPng()
    {
        String jpegDocumentId = uploadDocument("png");
        Response response = Documents.getDocumentLayout(jpegDocumentId);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.get("type").toString().contains("DocumentPageLayout"),
                "The document layout is not present !");
        assertTrue(jsonPath.get("pageDimensionsList.dpi").toString() != null, "The value of DPI is null !");
    }

}
