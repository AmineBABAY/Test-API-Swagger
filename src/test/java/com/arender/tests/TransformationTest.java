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

    private String uploadDocument(String f)
    {
        Response response = Documents.uploadDocument(f);
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        String id = jsonPath.get("id");
        assertTrue(id != null && !id.isEmpty(), "Your id is empty or null");
        return id;
    }

    private String transformDocumentBuilderWithoutAnnotations() throws InterruptedException
    {
        String documentId = uploadDocument("transf_doc1");
        String documentId2 = uploadDocument("transf_doc2");
        String body;
        // read the json file for transformation
        JSONObject contentFile = Config.readJsonFile("transformationBodyWithoutAnno");
        // get the transformationdetails array
        JSONArray transformationDetailsArray = contentFile.getJSONArray("transformationDetails");
        /// get the first transformationDetails object in the array
        JSONObject firstTransformationDetails = transformationDetailsArray.getJSONObject(0);
        // get the transformationElements array
        JSONArray transformationElements = firstTransformationDetails.getJSONArray("transformationElements");

        // get the first JSON Object in the JSON transformationElements Array
        JSONObject firstItemObject = (JSONObject) transformationElements.get(0);
        // put the documentId stored in the first id object
        firstItemObject.put("documentId", documentId);
        // get second JSON Object in the JSON transformationElements Array
        JSONObject secondItemObject = (JSONObject) transformationElements.get(1);
        // put the documentId1 stored in the second id object
        secondItemObject.put("documentId", documentId2);
        // fill the variable string body with the contentfile as string
        body = contentFile.toString();
        // make the request of transformation for the two pdf
        Response response = Transformations.transformDocuments(body);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the returnd id of transformation
        return jsonPath.get("transformationOrderId.id");

    }

    @Test()
    public void transformDocumentBuilderWithoutAnnotationsTest() throws InterruptedException
    {
        transformDocumentBuilderWithoutAnnotations();
    }

    public String checkTranformationOrder() throws InterruptedException
    {
        String transformationId = transformDocumentBuilderWithoutAnnotations();
        // make a request on getTransformationOrdrer with the id received in
        // transformDocument
        Response response = Transformations.getTransformationOrdrer(transformationId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        String transformationResultDocumentID = jsonPath.get("transformationResultDocumentID.id");
        // get the status of the body response
        String currentState = jsonPath.get("currentState");
        // Verify that the state is processed
        assertTrue(currentState.equals("PROCESSED"), "The current state is not PROCESSED");
        // verify that the id is genrated
        assertTrue(transformationResultDocumentID.length() != 0,
                "the transformation result document ID is not generated");
        return transformationResultDocumentID;

    }

    @Test()
    public void checkTranformationOrderTest() throws InterruptedException
    {
        checkTranformationOrder();
    }

    @Test()
    public void checkTranformedFile() throws InterruptedException
    {
        String transformationResultDocumentId = checkTranformationOrder();
        Response response = Documents.getDocumentContent(transformationResultDocumentId, "pdf");

        verifyStatusCode(response, 200);

        assertTrue(response.header("content-type").contains("application/pdf"));

    }

    public String transformDocumentBuilderWithAnnotations() throws InterruptedException
    {
        String documentId = uploadDocument("transf_doc1");
        String documentId2 = uploadDocument("transf_doc2");
        String body;
        // read the json file for transformation
        JSONObject contentFile = Config.readJsonFile("transformationBodyWithAnno");
        // get the transformationdetails array
        JSONArray transformationDetailsArray = contentFile.getJSONArray("transformationDetails");
        /// get the first transformationDetails object in the array
        JSONObject firstTransformationDetails = transformationDetailsArray.getJSONObject(0);
        // get the transformationElements array
        JSONArray transformationElements = firstTransformationDetails.getJSONArray("transformationElements");
        // get the first JSON Object in the JSON transformationElements Array
        JSONObject firstItemObject = (JSONObject) transformationElements.get(0);
        // put the documentId stored in the first id object
        firstItemObject.put("documentId", documentId);
        // get second JSON Object in the JSON transformationElements Array
        JSONObject secondItemObject = (JSONObject) transformationElements.get(1);
        // put the documentId1 stored in the second id object
        secondItemObject.put("documentId", documentId2);
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
        return jsonPath.get("transformationOrderId.id");

    }

    @Test()
    public void transformDocumentBuilderWithAnnotationsTest() throws InterruptedException
    {
        transformDocumentBuilderWithAnnotations();
    }

    public String checkTranformationOrderWithAnno() throws InterruptedException
    {
        String transformationWithAnnoId = transformDocumentBuilderWithAnnotations();
        // make a request on getTransformationOrdrer with the id received in
        // transformDocument
        Response response = Transformations.getTransformationOrdrer(transformationWithAnnoId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the status of the body response
        String currentState = jsonPath.get("currentState");
        String transformationWithAnnoResultDocumentID = jsonPath.get("transformationResultDocumentID.id");
        assertTrue(currentState.equals("PROCESSED"), "The current state is not PROCESSED");
        // verify that the id is genrated
        assertTrue(transformationWithAnnoResultDocumentID.length() != 0,
                "the transformation result document ID is not generated");
        return transformationWithAnnoResultDocumentID;

    }

    @Test()
    public void checkTranformationOrderWithAnnoTest() throws InterruptedException
    {
        checkTranformationOrderWithAnno();
    }

    @Test()
    public void checkTranformedFileWithAnno() throws InterruptedException
    {
        String transformationWithAnnoResultDocumentId = checkTranformationOrderWithAnno();
        Response response = Documents.getDocumentContent(transformationWithAnnoResultDocumentId, "pdf");

        verifyStatusCode(response, 200);
        assertTrue(response.header("content-type").contains("application/pdf"));

    }

    /**
     * the alternate DocumentContent method in RenditionRestClient allows
     * documentbuilder to work, download with annotations, and other features
     * that require alter
     **/

    @Test()
    public String alterDocumentContent() throws InterruptedException
    {
        String documentId = uploadDocument("fw4_pdf");
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
        String transformationOrderId = jsonPath.get("transformationOrderId.id");
        assertTrue(transformationOrderId.length() != 0, "the transformation order document ID is not generated");
        return transformationOrderId;

    }

    @Test()
    public void transformationOrderIdForAlterDocumentContentTest() throws InterruptedException
    {
        alterDocumentContent();
    }

    public String checkAlterDocTranformationOrder() throws InterruptedException
    {
        String transformationAlterDocumentId = alterDocumentContent();
        // make a request on getTransformationOrdrer with the id received in
        // transformDocument
        Response response = Transformations.getTransformationOrdrer(transformationAlterDocumentId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the status of the body response
        String currentState = jsonPath.get("currentState");
        String transformationAlterDocumentID = jsonPath.get("transformationResultDocumentID.id");
        // Verify that the state is processed
        assertTrue(currentState.equals("PROCESSED"), "The current state is not PROCESSED");
        // verify that the id is genrated
        assertTrue(transformationAlterDocumentID.length() != 0,
                "the transformation result document ID is not generated");
        return transformationAlterDocumentID;

    }

    @Test(dependsOnMethods = "transformationOrderIdForAlterDocumentContentTest")
    public void checkAlterDocTranformationOrderTest() throws InterruptedException
    {
        checkAlterDocTranformationOrder();
    }

    @Test()
    public void checkAlterDocTranformedFile() throws InterruptedException
    {
        String transformationAlterDocumentID = checkAlterDocTranformationOrder();
        Response response = Documents.getDocumentContent(transformationAlterDocumentID, "pdf");

        verifyStatusCode(response, 200);
        assertTrue(response.header("content-type").contains("application/pdf"));

    }

    public String transformDocumentWithFDFAnnotations() throws InterruptedException
    {
        String documentId = uploadDocument("pdf_");
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
        String transformationOrderId = jsonPath.get("transformationOrderId.id");
        assertTrue(transformationOrderId.length() != 0, "the transformation order document ID is not generated");
        return transformationOrderId;

    }

    @Test()
    public void transformationOrderIdForDocumentWithFDFAnnotationsTest() throws InterruptedException
    {
        transformDocumentWithFDFAnnotations();
    }

    public String checkTranformationOrderWithFDFAnno() throws InterruptedException
    {
        String transformationWithFDFAnnoId = transformDocumentWithFDFAnnotations();
        // make a request on getTransformationOrdrer with the id received in
        // transformDocument
        Response response = Transformations.getTransformationOrdrer(transformationWithFDFAnnoId);
        // verify status of request is ok
        verifyStatusCode(response, 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        // get the status of the body response
        String currentState = jsonPath.get("currentState");
        // get the id of the body response
        String transformationWithFDFAnnoResultDocumentID = jsonPath.get("transformationResultDocumentID.id");

        assertTrue(currentState.equals("PROCESSED"), "The current state is not PROCESSED");
        // verify that the id is genrated
        assertTrue(transformationWithFDFAnnoResultDocumentID.length() != 0,
                "the transformation result document ID is not generated");
        return transformationWithFDFAnnoResultDocumentID;
    }

    @Test(dependsOnMethods = "transformationOrderIdForDocumentWithFDFAnnotationsTest")
    public void checkTranformationOrderWithFDFAnnoTest() throws InterruptedException
    {
        checkTranformationOrderWithFDFAnno();
    }

    @Test()
    public void checkTranformedFileWithFDFAnno() throws InterruptedException
    {
        String transformationWithFDFAnnoResultDocumentID = checkTranformationOrderWithFDFAnno();
        Response response = Documents.getDocumentContent(transformationWithFDFAnnoResultDocumentID, "pdf");

        verifyStatusCode(response, 200);
        assertTrue(response.header("content-type").contains("application/pdf"));

    }

}
