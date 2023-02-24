package com.arender.actions;

import java.io.File;
import java.util.ArrayList;

import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Tasks extends AssertActions
{
    private Response uploadResponse;

    private Response getLayoutResponse;

    private ArrayList<Response> tabGetImage100pxResponses;

    private ArrayList<Response> tabGetImage800pxResponses;

    private Response evictResponse;

    private String name;

    public Tasks(File fileToUpload) throws Exception
    {

        tabGetImage100pxResponses = new ArrayList<Response>();
        tabGetImage800pxResponses = new ArrayList<Response>();
        this.name = Thread.currentThread().getName().substring(7) + " of :"
                + Thread.currentThread().getName().substring(0, 6) + "file :" + fileToUpload.getName();
        // upload
        uploadResponse = Documents.uploadDocument(fileToUpload, "doc ");

        // get Layout
        String idDoc = JsonPath.from(uploadResponse.asString()).get("id");
        getLayoutResponse = Documents.getDocumentLayout(idDoc);
        JsonPath layoutResponse = JsonPath.from(getLayoutResponse.asString());

        // get all image with different
        for (int i = 0; i < layoutResponse.getList("pageDimensionsList").size(); i++)
        {
            Response getImage = Documents.getPageImage(idDoc, i, "IM_100_0");
            tabGetImage100pxResponses.add(getImage);
            Response getImageFullScreen = Documents.getPageImage(idDoc, i, "IM_800_0");
            tabGetImage800pxResponses.add(getImageFullScreen);
        }
        evictResponse = Documents.evictDocument(idDoc);
    }

    public ArrayList<Response> getTabGetImage100pxResponses()
    {
        return tabGetImage100pxResponses;
    }

    public void setTabGetImage100pxResponses(ArrayList<Response> tabGetImage100pxResponses)
    {
        this.tabGetImage100pxResponses = tabGetImage100pxResponses;
    }

    public ArrayList<Response> getTabGetImage800pxResponses()
    {
        return tabGetImage800pxResponses;
    }

    public void setTabGetImage800pxResponses(ArrayList<Response> tabGetImage800pxResponses)
    {
        this.tabGetImage800pxResponses = tabGetImage800pxResponses;
    }

    public Response getUploadResponse()
    {
        return uploadResponse;
    }

    public void setUploadResponse(Response uploadResponse)
    {
        this.uploadResponse = uploadResponse;
    }

    public Response getGetLayoutResponse()
    {
        return getLayoutResponse;
    }

    public void setGetLayoutResponse(Response getLayoutResponse)
    {
        this.getLayoutResponse = getLayoutResponse;
    }

    public Response getEvictResponse()
    {
        return evictResponse;
    }

    public void setEvictResponse(Response evicResponses)
    {
        this.evictResponse = evicResponses;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
