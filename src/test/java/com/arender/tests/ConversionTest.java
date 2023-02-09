package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.json.JSONObject;
import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.endpoint.Conversions;
import com.arender.endpoint.Documents;
import com.arender.utlis.Config;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ConversionTest extends AssertActions
{

    public static Response response1;

    public static Response response2;

    public static Response response3;

    public static String documentId = "";

    public static String conversionOrderId = "";

    @Test(priority = 1)
    public void uploadDocument()
    {
        Response response = Documents.uploadDocument("testDocx");
        System.out.println(response.asString());
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        documentId = jsonPath.get("id");
    }

    @Test(priority = 2)
    public void convertDocxToTxt() throws InterruptedException
    {

        String body;

        JSONObject json = Config.readJsonFile("conversionsBody");
        json.put("documentId", documentId);
        json.put("format", "txt");
        body = json.toString();

        response1 = Conversions.convertDocumentToTargetFormat(body);
        System.out.println(response1.asString());
        verifyStatusCode(response1, 200);
        JsonPath jsonPath = JsonPath.from(response1.asString());
        conversionOrderId = jsonPath.get("conversionOrderId.id");
        System.out.println("conversion order id " + " : " + conversionOrderId);
        Thread.sleep(4000);

    }

    @Test(priority = 3)
    public void checkConversionOrder() throws InterruptedException
    {

        response2 = Conversions.getConversionOrder(conversionOrderId);
        System.out.println(response2.asString());
        verifyStatusCode(response2, 200);
        JsonPath jsonPath = JsonPath.from(response2.asString());
        String currentState = jsonPath.get("currentState");
        System.out.println("conversion order state " + " : " + currentState);
        assertTrue(currentState.equals("PROCESSED"), "The current state is not PROCESSED");
    }

    @Test(priority = 4)
    public void checkConvertedFile() throws InterruptedException
    {

        response3 = Documents.getDocumentContent(documentId, "txt");
        System.out.println(response3.asString());
        verifyStatusCode(response3, 200);
        assertTrue(response3.asString().contains("TEST"), "The current file does not contains the word TEST");
    }

}
