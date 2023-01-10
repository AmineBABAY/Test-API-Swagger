package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.json.JSONObject;
import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.bodyrequest.ComparisonsBodyRequest;
import com.arender.endpoint.Comparisons;
import com.arender.endpoint.Conversions;
import com.arender.endpoint.Documents;
import com.arender.utlis.Config;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class ComparisonsTest extends AssertActions{
	
	String documentAId="";
	String documentBId="";
	String comparisonOrderId="";
	String targetDocumentId="";
	public static Response responseComparisonsOrder;
	
	@Test(priority=1)
	public void  uploadDocument()
	{	//upload imageA and save the response
		Response response1= Documents.uploadDocument("imageA");
		//upload imageB and save the response
		Response response2= Documents.uploadDocument("imageB");
		//convert the response1 and save the body
		JsonPath jsonPath1 = JsonPath.from (response1.asString ());
		//convert the response2 and save the body
		JsonPath jsonPath2 = JsonPath.from (response1.asString ());
		//check status code of the response 1 and response 2 are  200
		verifyStatusCode(response1, 200);
		verifyStatusCode(response2, 200);
		//save the id of each response 
		documentAId=jsonPath1.get("id");
		documentBId=jsonPath2.get("id");
	}
	@Test(priority=2) 
	public void compareToDocuments() throws InterruptedException {
        String body;
      //set the body of the request with the id of documents 
        JSONObject json = Config.readJsonFile("comparisonsBody");
        json.put("fuzz", 3);
        json.put("highlightColor", "ff0000");
        json.put("leftDocumentId", documentAId);
        json.put("lowlightColor", "null");
        json.put("rightDocumentId", documentBId);
        body = json.toString();
	    
	    //ComparisonsBodyRequest body=new ComparisonsBodyRequest(3,"ff0000",documentAId,"null",documentBId);
	    //compare two documents and save the response
		Response response=Comparisons.compareDocuments(body);
		//check status code is 200
	    verifyStatusCode(response, 200);
	    //convert the response and save the body
	    JsonPath jsonPath = JsonPath.from (response.asString ());
	    //save comparisonsId
	    comparisonOrderId=jsonPath.get("comparisonOrderId.id");
	    Thread.sleep(4000);
			
	}
	@Test(priority=3) 
    public void checkComparisonsOrder()
    {
	    //save the comparisonsOrder
        responseComparisonsOrder = Comparisons.getComparisonsOrder(comparisonOrderId);
        //check status code is 200
        verifyStatusCode(responseComparisonsOrder, 200);
        //convert the response and save the body
        JsonPath jsonPath = JsonPath.from(responseComparisonsOrder.asString());
        //save currentState
        String currentState = jsonPath.get("currentState");
        System.out.println("conversion order state " + " : " + currentState);
        //check the current state is PROCESSED
        assertTrue(currentState.equals("PROCESSED"), "The current state is not PROCESSED");
        //save the targetDocumentId
         targetDocumentId = jsonPath.get("targetDocumentId.id");
    }
	   @Test (priority =4)
	    public void checkComparedFile() throws InterruptedException {

	       Response responseComparedFile=Documents.getDocumentContent(targetDocumentId,"jpg");
	        verifyStatusCode(responseComparedFile, 200);     
	    }

}
