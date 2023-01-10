package com.arender.tests;

import static org.testng.Assert.assertTrue;


import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class DeleteTest extends AssertActions{
	
	String documentId="";
	
	
	@Test(priority=1)
	public void  uploadDocument()
	{	
		Response response= Documents.uploadDocument(file);
		JsonPath jsonPath = JsonPath.from (response.asString ());
	   
		verifyStatusCode(response, 200);
		documentId=jsonPath.get("id");
		
	}
	
	@Test(priority=2)
	public void  deleteEvictDocument()
	{	
		Response response= Documents.evictDocument(documentId);
		
		verifyStatusCode(response, 200);
		
		Response checkDeletedDoc = Documents.getDocument(documentId);
		
		verifyStatusCode(checkDeletedDoc, 404);
		
	}
	

}
