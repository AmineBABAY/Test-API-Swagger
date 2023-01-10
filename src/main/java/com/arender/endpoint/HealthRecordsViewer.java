package com.arender.endpoint;

import com.arender.utlis.Initialization;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class HealthRecordsViewer extends Initialization
{
    public static Response readiness() {
        RestAssured.baseURI = url;
        return RestAssured.given().when().get("/health/readiness");
    }
}
