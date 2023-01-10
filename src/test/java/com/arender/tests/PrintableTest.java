package com.arender.tests;

import static org.testng.Assert.assertTrue;


import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class PrintableTest extends AssertActions{
	
	String documentId="";
	
	@Test(priority=1)
	public void  uploadDocument()
	{	
		Response response= Documents.uploadDocument(file);
		JsonPath jsonPath = JsonPath.from (response.asString ());
		System.out.println(response.asString());
		verifyStatusCode(response, 200);
		documentId=jsonPath.get("id");
	}
	@Test(priority=2) 
	public void casPassant() {
		Response response=Documents.getPrintableDocument(documentId);
		System.out.println(response.asString());
		verifyStatusCode(response, 200);
		assertTrue(String.valueOf(response.getHeaders().get("content-type")).equals("Content-Type=application/pdf"));
		String bodyContains =""
				+ "<<"
				+ "\n/Type /Action"
				+ "\n/S /JavaScript"
				+ "\n/JS (this.print\\(\\);this.closeDoc\\(true\\))"
				+ "\n>>";
			
		assertTrue(response.asString().contains(bodyContains),"the body of your response does not contains the right code");
	}
	@Test(priority=3) 
	public void casBloquant () {
		Response response=Documents.getPrintableDocument("bad id");
		verifyStatusCode(response, 404);
	}

}
