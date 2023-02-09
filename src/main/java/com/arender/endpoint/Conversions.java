package com.arender.endpoint;

import com.arender.utlis.Initialization;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Conversions extends Initialization
{
    public static Gson gson = new Gson();

    public static Response convertDocumentToTargetFormat(String body)
    {
        RestAssured.baseURI = url;
        return RestAssured.given().header("Content-type", "application/json").and().body(body).when()
                .post("/conversions");
    }

    public static Response getConversionOrder(String conversionOrderId)
    {
        RestAssured.baseURI = url;
        return RestAssured.given().contentType("application/json").when().get("/conversions/" + conversionOrderId);
    }
}
