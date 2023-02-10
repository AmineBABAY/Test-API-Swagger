package com.arender.endpoint;

import java.util.concurrent.TimeUnit;

import com.arender.utlis.Initialization;

import io.restassured.RestAssured;
import io.restassured.config.ConnectionConfig;
import io.restassured.response.Response;

public class Conversions extends Initialization
{
    static
    {
        RestAssured.baseURI = url;
        ConnectionConfig connectionConfig = new ConnectionConfig(
                new ConnectionConfig.CloseIdleConnectionConfig(0, TimeUnit.NANOSECONDS));
        RestAssured.config().connectionConfig(connectionConfig);
    }

    public static Response convertDocumentToTargetFormat(String body)
    {
        return RestAssured.given().header("Content-type", "application/json").and().body(body).when()
                .post("/conversions");
    }

    public static Response getConversionOrder(String conversionOrderId)
    {

        return RestAssured.given().contentType("application/json").when().get("/conversions/" + conversionOrderId);
    }
}
