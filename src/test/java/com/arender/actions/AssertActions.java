package com.arender.actions;

import static org.testng.Assert.assertEquals;

import com.arender.utlis.Initialization;

import io.restassured.response.Response;

public class AssertActions extends Initialization
{

    public void verifyStatusCode(Response response, int code)
    {
        assertEquals(response.getStatusCode() == code, true,
                "\nvalue of status code is : " + response.getStatusCode() + "\nExpected : " + code);
    }

    public void verifyResponseBody(String actual, String expected, String description)
    {
        assertEquals(actual, expected, description);

    }

    public void verifyResponseBody(float actual, float expected, String description)
    {
        assertEquals(actual, expected, description);

    }

    public void verifyResponseBody(int actual, int expected, String description)
    {
        assertEquals(actual, expected, description);

    }

    public void verifyResponseBody(double actual, double expected, String description)
    {
        assertEquals(actual, expected, description);

    }

    public void verifyResponseBody(boolean actual, boolean expected, String description)
    {
        assertEquals(actual, expected, description);

    }

}