package com.arender.tests;

import static org.testng.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;
import com.arender.actions.AssertActions;

import com.arender.endpoint.Documents;
import com.arender.endpoint.Transformations;
import com.arender.utlis.Config;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class TransformationTest extends AssertActions
{

    public static String documentId = "";

    public static String documentId1 = "";

    public static String transformationId = "";

    public static String transformationWithAnnoId = "";

    public static String transformationResultDocumentID = "";

    public static String transformationWithAnnoResultDocumentID = "";

    public static String transformationAlterDocumentId = "";

    public static String transformationAlterDocumentID = "";

    public static String transformationWithFDFAnnoId = "";

    public static String transformationWithFDFAnnoResultDocumentID = "";

    @Test
    public void uploadDocument()
    {

        // upload a first pdf file
        Response response = Documents.uploadDocument("pdf_");
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the id of the file
        documentId = jsonPath.get("id");
        // upload a second pdf file
        Response response1 = Documents.uploadDocument("fw4_pdf");
        JsonPath jsonPath1 = JsonPath.from(response1.asString());
        // get the id of the document
        documentId1 = jsonPath1.get("id");

    }

    @Test(priority = 2)
    public void transformDocumentBuilderWithoutAnnotations() throws InterruptedException
    {

        String body;
        // read the json file for transformation
        JSONObject contentFile = Config.readJsonFile("transformationBodyWithoutAnno");
        // get the transformationdetails object
        JSONObject transformationdetails = contentFile.getJSONObject("transformationDetails");
        // from the transformationdetails object, get the transformationElements
        // array
        JSONArray transformationElements = transformationdetails.getJSONArray("transformationElements");

        // get the first JSON Object in the JSON transformationElements Array
        JSONObject firstItemObject = (JSONObject) transformationElements.get(0);
        // put the documentId stored in the first id object
        firstItemObject.put("documentId", documentId);
        // get second JSON Object in the JSON transformationElements Array
        JSONObject secondItemObject = (JSONObject) transformationElements.get(1);
        // put the documentId1 stored in the second id object
        secondItemObject.put("documentId", documentId1);
        // fill the variable string body with the contentfile as string
        body = contentFile.toString();
        // make the request of transformation for the two pdf
        Response response = Transformations.transformDocuments(body);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the returnd id of transformation
        transformationId = jsonPath.get("transformationOrderId.id");
        System.out.println("transformation id without annotations" + " : " + transformationId);

    }

    @Test(priority = 3)
    public void checkTranformationOrder() throws InterruptedException
    {

        // make a request on getTransformationOrdrer with the id received in
        // transformDocument
        Response response = Transformations.getTransformationOrdrer(transformationId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the status of the body response
        String currentState = jsonPath.get("currentState");
        // get the id of the body response
        transformationResultDocumentID = jsonPath.get("transformationResultDocumentID.id");
        System.out.println("transformation order state without annotations" + " : " + currentState);
        System.out.println("trasformation order id without annotations " + " : " + transformationResultDocumentID);
        // Verify that the state is processed
        assertTrue(currentState.equals("PROCESSED"), "The current state is not PROCESSED");
        // verify that the id is genrated
        assertTrue(transformationResultDocumentID.length() != 0,
                "the transformation result document ID is not generated");

    }

    @Test(priority = 4)
    public void checkTranformedFile() throws InterruptedException
    {

        Response response = Documents.getDocumentContent(transformationResultDocumentID, "pdf");

        verifyStatusCode(response, 200);

        assertTrue(response.header("content-type").contains("application/pdf"));

    }

    @Test(priority = 5)
    public void transformDocumentBuilderWithAnnotations() throws InterruptedException
    {

        String body;
        // read the json file for transformation
        JSONObject contentFile = Config.readJsonFile("transformationBodyWithAnno");
        // get the transformationdetails object
        JSONObject transformationdetails = contentFile.getJSONObject("transformationDetails");
        // from the transformationdetails object, get the transformationElements
        // array
        JSONArray transformationElements = transformationdetails.getJSONArray("transformationElements");

        // get the first JSON Object in the JSON transformationElements Array
        JSONObject firstItemObject = (JSONObject) transformationElements.get(0);
        // put the documentId stored in the first id object
        firstItemObject.put("documentId", documentId);
        // get second JSON Object in the JSON transformationElements Array
        JSONObject secondItemObject = (JSONObject) transformationElements.get(1);
        // put the documentId1 stored in the second id object
        secondItemObject.put("documentId", documentId1);
        // get the array annotations
        JSONObject annotations = contentFile.getJSONObject("annotations");
        assertTrue(!annotations.isEmpty(), "The transform document is without annotations");
        JSONArray anno_array = annotations.getJSONArray("annotations");
        // put the id of the first document in the fieled documentId throughout
        // the
        // length of the annotaion array
        for (int i = 0; i < anno_array.length(); i++)
        {
            JSONObject id = (JSONObject) anno_array.getJSONObject(i).get("documentId");
            id.put("id", documentId);
        }
        // fill the variable string body with the contentfile as string
        body = contentFile.toString();

        // make the request of transformation for the two pdf
        Response response = Transformations.transformDocuments(body);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the returnd id of transformation
        transformationWithAnnoId = jsonPath.get("transformationOrderId.id");
        System.out.println("transformation id with annotations " + " : " + transformationWithAnnoId);

    }

    @Test(priority = 6)
    public void checkTranformationOrderWithAnno() throws InterruptedException
    {

        // make a request on getTransformationOrdrer with the id received in
        // transformDocument
        Response response = Transformations.getTransformationOrdrer(transformationWithAnnoId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the status of the body response
        String currentState = jsonPath.get("currentState");
        // get the id of the body response
        transformationWithAnnoResultDocumentID = jsonPath.get("transformationResultDocumentID.id");
        System.out.println("transformation order state with annotations " + " : " + currentState);
        System.out.println("trasformation order id with annotations " + " : " + transformationWithAnnoResultDocumentID);
        // Verify that the state is processed
        assertTrue(currentState.equals("PROCESSED"), "The current state is not PROCESSED");
        // verify that the id is genrated
        assertTrue(transformationWithAnnoResultDocumentID.length() != 0,
                "the transformation result document ID is not generated");

    }

    @Test(priority = 7)
    public void checkTranformedFileWithAnno() throws InterruptedException
    {

        Response response = Documents.getDocumentContent(transformationWithAnnoResultDocumentID, "pdf");

        verifyStatusCode(response, 200);
        assertTrue(response.header("content-type").contains("application/pdf"));

    }

    /**
     * the alternate DocumentContent method in RenditionRestClient allows
     * documentbuilder to work, download with annotations, and other features
     * that require alter
     **/

    @Test(priority = 8)
    public void uploadAlterDocument() throws InterruptedException
    {

        Response response = Documents.uploadDocument("fw4_pdf");
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the id of the file
        documentId = jsonPath.get("id");
        verifyStatusCode(response, 200);
    }

    @Test(priority = 9)
    public void alterDocumentContent() throws InterruptedException
    {

        String body;
        // read the json file for transformation
        JSONObject contentFile = Config.readJsonFile("alterDocumentContent");

        // get the transformationdetails object
        JSONObject transformationdetails = contentFile.getJSONObject("transformationDetails");
        // from the transformationdetails object, get the transformationElements
        // array
        JSONArray transformationElements = transformationdetails.getJSONArray("transformationElements");
        JSONObject firstItemObject = (JSONObject) transformationElements.get(0);
        // put the documentId stored in the first id object
        firstItemObject.put("documentId", documentId);

        JSONObject annotations = contentFile.getJSONObject("annotations");

        JSONArray anno_array = annotations.getJSONArray("annotations");

        JSONObject id = (JSONObject) anno_array.getJSONObject(0).get("documentId");
        id.put("id", documentId);
        body = contentFile.toString();

        Response transformation = Transformations.transformDocuments(body);
        verifyStatusCode(transformation, 200);
        JsonPath jsonPath = JsonPath.from(transformation.asString());
        // get the returnd id of transformation
        transformationAlterDocumentId = jsonPath.get("transformationOrderId.id");
        System.out.println("transformation id for alter document" + " : " + transformationAlterDocumentId);

    }

    @Test(priority = 10)
    public void checkAlterDocTranformationOrder() throws InterruptedException
    {

        // make a request on getTransformationOrdrer with the id received in
        // transformDocument
        Response response = Transformations.getTransformationOrdrer(transformationAlterDocumentId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the status of the body response
        String currentState = jsonPath.get("currentState");
        // get the id of the body response
        transformationAlterDocumentID = jsonPath.get("transformationResultDocumentID.id");
        System.out.println("transformation order state for alter document " + " : " + currentState);
        System.out.println("trasformation order id for alter document " + " : " + transformationAlterDocumentID);
        // Verify that the state is processed
        assertTrue(currentState.equals("PROCESSED"), "The current state is not PROCESSED");
        // verify that the id is genrated
        assertTrue(transformationAlterDocumentID.length() != 0,
                "the transformation result document ID is not generated");

    }

    @Test(priority = 11)
    public void checkAlterDOcTranformedFile() throws InterruptedException
    {

        Response response = Documents.getDocumentContent(transformationAlterDocumentID, "pdf");

        verifyStatusCode(response, 200);
        assertTrue(response.header("content-type").contains("application/pdf"));

    }

    @Test(priority = 12)
    public void transformDocumentWithFDFAnnotations() throws InterruptedException
    {

        String body;
        // read the json file for transformation
        JSONObject contentFile = Config.readJsonFile("transformationWithFDFAnnotation");
        // get the transformationdetails object
        JSONObject transformationdetails = contentFile.getJSONObject("transformationDetails");
        // from the transformationdetails object, get the transformationElements
        // array
        JSONArray transformationElements = transformationdetails.getJSONArray("transformationElements");

        // get the first JSON Object in the JSON transformationElements Array
        JSONObject firstItemObject = (JSONObject) transformationElements.get(0);
        // put the documentId stored in the first id object
        firstItemObject.put("documentId", documentId);
        // get the array annotations
        JSONObject annotations = contentFile.getJSONObject("annotations");
        assertTrue(!annotations.isEmpty(), "The transform document is without FDF annotations");
        JSONArray anno_array = annotations.getJSONArray("annotations");
        // put the id of the first document in the fieled documentId throughout
        // the
        // length of the annotaion array
        for (int i = 0; i < anno_array.length(); i++)
        {
            JSONObject id = (JSONObject) anno_array.getJSONObject(i).get("documentId");
            id.put("id", documentId);
        }
        // fill the variable string body with the contentfile as string
        body = contentFile.toString();

        // make the request of transformation for the two pdf
        Response response = Transformations.transformDocuments(body);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the returnd id of transformation
        transformationWithFDFAnnoId = jsonPath.get("transformationOrderId.id");
        System.out.println("transformation id with FDF annotations " + " : " + transformationWithAnnoId);

    }

    @Test(priority = 13)
    public void checkTranformationOrderWithFDFAnno() throws InterruptedException
    {

        // make a request on getTransformationOrdrer with the id received in
        // transformDocument
        Response response = Transformations.getTransformationOrdrer(transformationWithFDFAnnoId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the status of the body response
        String currentState = jsonPath.get("currentState");
        // get the id of the body response
        transformationWithFDFAnnoResultDocumentID = jsonPath.get("transformationResultDocumentID.id");
        System.out.println("transformation order state with annotations " + " : " + currentState);
        System.out.println(
                "trasformation order id with FDF annotations " + " : " + transformationWithFDFAnnoResultDocumentID);
        // Verify that the state is processed
        assertTrue(currentState.equals("PROCESSED"), "The current state is not PROCESSED");
        // verify that the id is genrated
        assertTrue(transformationWithFDFAnnoResultDocumentID.length() != 0,
                "the transformation result document ID is not generated");

    }

    @Test(priority = 14)
    public void checkTranformedFileWithFDFAnno() throws InterruptedException
    {

        Response response = Documents.getDocumentContent(transformationWithFDFAnnoResultDocumentID, "pdf");

        verifyStatusCode(response, 200);
        assertTrue(response.header("content-type").contains("application/pdf"));

    }

}
