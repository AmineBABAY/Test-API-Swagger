package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.json.JSONObject;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Comparisons;
import com.arender.endpoint.Documents;
import com.arender.utlis.Config;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ComparisonsTest extends AssertActions
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
    public String compareTwoDocuments() throws InterruptedException
    {
        String documentAId = uploadDocument("imageA");
        String documentBId = uploadDocument("imageB");
        String body;

        JSONObject json = Config.readJsonFile("comparisonsBody");
        json.put("fuzz", 3);
        json.put("highlightColor", "ff0000");
        json.put("leftDocumentId", documentAId);
        json.put("lowlightColor", "null");
        json.put("rightDocumentId", documentBId);
        body = json.toString();
        Response response = Comparisons.compareDocuments(body);
        verifyStatusCode(response, 200);
        // convert the response and save the body
        JsonPath jsonPath = JsonPath.from(response.asString());
        // save comparisonsId
        return jsonPath.get("comparisonOrderId.id");

    }

    @Test()
    public void compareTwoDocumentTest() throws InterruptedException
    {
        compareTwoDocuments();
    }

    public String checkComparisonsOrder() throws InterruptedException
    {
        String comparisonOrderId = compareTwoDocuments();
        Response responseComparisonsOrder = Comparisons.getComparisonsOrder(comparisonOrderId);
        // check status code is 200
        verifyStatusCode(responseComparisonsOrder, 200);
        // convert the response and save the body
        JsonPath jsonPath = JsonPath.from(responseComparisonsOrder.asString());
        // save currentState
        String currentState = jsonPath.get("currentState");
        // check the current state is PROCESSED
        assertTrue(currentState.equals("PROCESSED"), "The current state is not PROCESSED");
        // save the targetDocumentId
        return jsonPath.get("targetDocumentId.id");
    }

    @Test
    public void checkComparisonsOrderTest() throws InterruptedException
    {
        checkComparisonsOrder();
    }

    @Test()
    public void checkComparedFile() throws InterruptedException
    {
        String targetDocumentId = checkComparisonsOrder();
        Response responseComparedFile = Documents.getDocumentContent(targetDocumentId, "jpg");
        verifyStatusCode(responseComparedFile, 200);
    }

}
