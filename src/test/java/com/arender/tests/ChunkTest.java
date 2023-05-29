package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ChunkTest extends AssertActions
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
    public void getFileChunkWithPositiveRange()
    {
        String documentAId = uploadDocument("text-to-Ascii");
        Response response = Documents.getFileChunk(documentAId, "bytes=3-9");
        verifyStatusCode(response, 206);
        Assert.assertTrue(response.asString().contains("105 108"));
    }

    @Test()
    public void getFileChunkWithPositiveDescendingRange()
    {
        String documentAId = uploadDocument("text-to-Ascii");
        Response response = Documents.getFileChunk(documentAId, "bytes=6-2");
        verifyStatusCode(response, 400);
    }

    @Test()
    public void getFileChunkWithNegativeRange()
    {
        String documentAId = uploadDocument("text-to-Ascii");
        Response response = Documents.getFileChunk(documentAId, "bytes=-3-9");
        verifyStatusCode(response, 400);
    }

    @Test()
    public void getFileChunkWithLettersRange()
    {
        String documentAId = uploadDocument("text-to-Ascii");
        Response response = Documents.getFileChunk(documentAId, "bytes=a-e");
        verifyStatusCode(response, 400);
    }

    @Test()
    public void getFileChunkWithWrongDocument()
    {
        Response response = Documents.getFileChunk("WrongId", "bytes=3-9");
        verifyStatusCode(response, 404);
    }

    @Test()
    public void getFileChunkWithPositiveRange_Docx()
    {
        String documentAId = uploadDocument("docx");
        Response response = Documents.getFileChunk(documentAId, "bytes=3-9");
        verifyStatusCode(response, 206);
        Assert.assertTrue(response.asString().contains(""));
    }

    @Test()
    public void getFileChunkWithPositiveRangeAndFormatPDF_Docx() throws InterruptedException
    {
        String documentAId = uploadDocument("docx");

        Documents.getDocumentLayout(documentAId);
        Response response = Documents.getFileChunk(documentAId, "bytes=3-9", "pdf");
        verifyStatusCode(response, 206);
        Assert.assertTrue(response.asString().contains("F-1.6"));
    }

}
