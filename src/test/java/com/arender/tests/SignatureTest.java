package com.arender.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.arender.actions.AssertActions;
import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class SignatureTest extends AssertActions {

	String documentId = "";

	@Test(priority = 1)
	public void uploadDocument() throws InterruptedException {

		// upload document with signature
		Response response = Documents.uploadDocument("signature");
		// get reponse body
		JsonPath jsonPath = JsonPath.from(response.asString());
		// verify status of request is ok
		verifyStatusCode(response, 200);
		// get th id of document
		documentId = jsonPath.get("id");
	}

	@Test(priority = 2)
	public void documentWithSignatures() {

		// Make a GET request on the end point bookmark
		Response response = Documents.getSignatures(documentId);
		// verify status of request is ok
		verifyStatusCode(response, 200);
		// Get the list of signatures
		String responseContent = response.jsonPath().getString("signatures");
		System.out.print(responseContent);
		// verify that the list of signature is not empty
		Assert.assertTrue(responseContent.contains("name"));
		Assert.assertTrue(responseContent.contains("date"));
		Assert.assertTrue(responseContent.contains("signer"));
		Assert.assertTrue(responseContent.contains("location"));
		Assert.assertTrue(responseContent.contains("reason"));

	}

	@Test(priority = 3)
	public void documentWithoutSignature() {

		// upload document without signatures
		Response response = Documents.uploadDocument("imageA");
		JsonPath jsonPath = JsonPath.from(response.asString());
		verifyStatusCode(response, 200);

		documentId = jsonPath.get("id");
		Response response2 = Documents.getSignatures(documentId);
		String responseContent = response2.jsonPath().getString("signatures");

		// verify that the list of signatures is empty
		Assert.assertTrue(responseContent.contains("[]"));
	}

	@Test(priority = 4)
	public void uploadDocumentwithWrongId() {

		//// Make a GET request on the end point signature with an id of a document
		//// that does not exist.
		Response response = Documents.getSignatures("bad id");
		// Check that the status of request is failed
		verifyStatusCode(response, 404);

	}

}
