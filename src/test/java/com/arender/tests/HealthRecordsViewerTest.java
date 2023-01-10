package com.arender.tests;

import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.endpoint.HealthRecordsViewer;
import io.restassured.response.Response;


public class HealthRecordsViewerTest extends AssertActions{
	
	String documentId="";
	String zipDocumentId="";
	
	@Test(priority=1)
	public void  checkReadiness()
	{	
		Response response= HealthRecordsViewer.readiness();
		verifyStatusCode(response, 200);
	}
}