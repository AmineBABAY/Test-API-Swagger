package com.arender.endpoint;

import java.util.concurrent.TimeUnit;

import com.arender.utlis.Initialization;

import io.restassured.RestAssured;
import io.restassured.config.ConnectionConfig;
import io.restassured.response.Response;

public class HealthRecordsViewer extends Initialization
{
    static
    {
        RestAssured.baseURI = url;
        ConnectionConfig connectionConfig = new ConnectionConfig(
                new ConnectionConfig.CloseIdleConnectionConfig(0, TimeUnit.NANOSECONDS));
        RestAssured.config().connectionConfig(connectionConfig);
    }

    public static Response readiness()
    {

        return RestAssured.given().when().get("/health/readiness");
    }
}
