package com.arender.endpoint;

import com.arender.utlis.Initialization;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Transformations extends Initialization
{
    public static Response transformDocuments(String body)
    {
        RestAssured.baseURI = url;
        return RestAssured.given().header("Content-type", "application/json").and().body(body).when()
                .post("/transformations");
    }

    public static Response getTransformationOrdrer(String transformationOrderId)
    {
        RestAssured.baseURI = url;
        return RestAssured.given().queryParam("timeoutMs", 60000).contentType("application/json").when()
                .get("/transformations/" + transformationOrderId);
    }
}
