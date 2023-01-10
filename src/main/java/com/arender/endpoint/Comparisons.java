package com.arender.endpoint;


import com.arender.utlis.Initialization;


import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Comparisons extends Initialization
{
    public static Response compareDocuments(String body)
    {
        RestAssured.baseURI = url;
        return RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/comparisons");
    }
    public static Response getComparisonsOrder(String comparisonsOrderId)
    {   RestAssured.baseURI = url;
        return RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/comparisons/"+comparisonsOrderId);
    }
}
