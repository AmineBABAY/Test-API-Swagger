package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class PrintableTest extends AssertActions
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

    @Test(priority = 1)
    public void casPassant()
    {
        String documentId = uploadDocument(file);
        Response response = Documents.getPrintableDocument(documentId);
        verifyStatusCode(response, 200);
        assertTrue(String.valueOf(response.getHeaders().get("content-type")).equals("Content-Type=application/pdf"));
        String bodyContains = "" + "<<" + "\n/Type /Action" + "\n/S /JavaScript"
                + "\n/JS (this.print\\(\\);this.closeDoc\\(true\\))" + "\n>>";

        assertTrue(response.asString().contains(bodyContains),
                "the body of your response does not contains the right code");
    }

    @Test(priority = 2)
    public void casBloquant()
    {
        Response response = Documents.getPrintableDocument("bad id");
        verifyStatusCode(response, 404);
    }

}
