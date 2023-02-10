package com.arender.endpoint;

import java.util.concurrent.TimeUnit;

import com.arender.utlis.Initialization;

import io.restassured.RestAssured;
import io.restassured.config.ConnectionConfig;
import io.restassured.response.Response;

public class Transformations extends Initialization
{
    static
    {
        RestAssured.baseURI = url;
        ConnectionConfig connectionConfig = new ConnectionConfig(
                new ConnectionConfig.CloseIdleConnectionConfig(0, TimeUnit.NANOSECONDS));
        RestAssured.config().connectionConfig(connectionConfig);
    }

    public static Response transformDocuments(String body)
    {
        return RestAssured.given().header("Content-type", "application/json").and().body(body).when()
                .post("/transformations");
    }

    public static Response getTransformationOrdrer(String transformationOrderId)
    {
        return RestAssured.given().queryParam("timeoutMs", 60000).contentType("application/json").when()
                .get("/transformations/" + transformationOrderId);
    }
}
