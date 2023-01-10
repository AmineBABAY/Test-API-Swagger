package com.arender.tests;

import static org.testng.Assert.assertTrue;


import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class FileTest extends AssertActions{
	
	String documentId="";
	String zipDocumentId="";
	
	@Test(priority=1)
	public void  uploadDocument()
	{	
		Response response= Documents.uploadDocument("docx");
		JsonPath jsonPath = JsonPath.from (response.asString ());
		verifyStatusCode(response, 200);
		documentId=jsonPath.get("id");
	}
	@Test(priority=2) 
	public void getDocumentAndCheckFormat() {
		Response response=Documents.getDocumentContent(documentId, "docx");
		verifyStatusCode(response, 200);
		assertTrue(response.getHeaders().get("Content-Type").toString().contains("officedocument"),"This file does not contain the correct format");
		

	}
	@Test(priority=3) 
	public void getDocumentWithConvertType () {
        Response response=Documents.getDocumentContent(documentId, "txt");       
        verifyStatusCode(response, 200);
        assertTrue(response.getHeaders().get("Content-Type").toString().contains("text"),"This file does not contain the correct format");
	}
	   @Test(priority=4) 
	    public void getDocumentWithDifferentFormat() {
	        Response response=Documents.getDocumentContent(documentId, "avi");       
	        verifyStatusCode(response, 406);
	    }

}
