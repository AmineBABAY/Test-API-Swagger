package com.arender.tests;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.arender.actions.AssertActions;
import com.arender.endpoint.Conversions;
import com.arender.endpoint.Documents;
import com.arender.utlis.Config;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ConversionTest extends AssertActions
{
    private static Logger LOGGER = Logger.getLogger(ConversionTest.class);

    private String uploadDocument(String f)
    {
        Response response = Documents.uploadDocument(f);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        String id = jsonPath.get("id");
        assertTrue(id != null && !id.isEmpty(), "Your id is empty or null");
        return id;
    }

    private String convertDocxToTxt()
    {
        String documentId = uploadDocument("testDocx");
        String body;

        JSONObject json = Config.readJsonFile("conversionsBody");
        json.put("documentId", documentId);
        json.put("format", "txt");
        body = json.toString();

        Response response = Conversions.convertDocumentToTargetFormat(body);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        return jsonPath.get("conversionOrderId.id");
    }

    @Test()
    public void convertDocxToTxtTest() throws InterruptedException
    {
        convertDocxToTxt();

    }

    @Test()
    public void checkConversionOrder() throws InterruptedException
    {
        String conversionOrderId = convertDocxToTxt();
        for (int iteration = 0; iteration < 60; iteration++)
        {
            Response response2 = Conversions.getConversionOrder(conversionOrderId);
            verifyStatusCode(response2, 200);
            JsonPath jsonPath = JsonPath.from(response2.asString());
            String currentState = jsonPath.get("currentState");
            LOGGER.info("conversion order state " + " : " + currentState);
            if (currentState.equals("PROCESSED"))
            {
                LOGGER.info("Done");
                return;
            }
            else if (currentState.equals("PROCESSING"))
            {
                LOGGER.info("Not yet");
            }
            else
            {
                assertFalse(true, "there is a problem !");
            }
        }

    }

    public String postConversion()
    {
        String documentId = uploadDocument("testDocx");
        String body;

        JSONObject json = Config.readJsonFile("conversionsBody");
        json.put("documentId", documentId);
        json.put("format", "mp4");
        body = json.toString();

        Response response = Conversions.convertDocumentToTargetFormat(body);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        return jsonPath.get("conversionOrderId.id");
    }

    @Test()
    public void postConversionTest()

    {
        Assert.assertTrue(postConversion().contains("-mp4"), "Your id does not contains -mp4");
    }

    @Test()
    public void checkCurrentState()
    {
        String conversionOrderId = postConversion();
        for (int iteration = 0; iteration < 60; iteration++)
        {
            Response response2 = Conversions.getConversionOrder(conversionOrderId);
            verifyStatusCode(response2, 200);
            JsonPath jsonPath = JsonPath.from(response2.asString());
            String currentState = jsonPath.get("currentState");
            LOGGER.info("conversion order state " + " : " + currentState);
            if (currentState.equals("FAILED"))
            {
                LOGGER.info("Done");
                return;
            }
            else if (currentState.equals("PROCESSING"))
            {
                LOGGER.info("Not yet");
            }
            else
            {
                Assert.fail("Your current state is not FAILED !");
            }
        }
    }

}
