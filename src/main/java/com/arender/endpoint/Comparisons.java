package com.arender.endpoint;

import java.util.concurrent.TimeUnit;

import com.arender.utlis.Initialization;

import io.restassured.RestAssured;
import io.restassured.config.ConnectionConfig;
import io.restassured.response.Response;

public class Comparisons extends Initialization
{
    static
    {
        RestAssured.baseURI = url;
        ConnectionConfig connectionConfig = new ConnectionConfig(
                new ConnectionConfig.CloseIdleConnectionConfig(0, TimeUnit.NANOSECONDS));
        RestAssured.config().connectionConfig(connectionConfig);
    }

    public static Response compareDocuments(String body)
    {

        return RestAssured.given().contentType("application/json").body(body).when().post("/comparisons");
    }

    public static Response getComparisonsOrder(String comparisonsOrderId)
    {

        return RestAssured.given().queryParam("timeoutMs", 60000).contentType("application/json").when()
                .get("/comparisons/" + comparisonsOrderId);
    }
}
