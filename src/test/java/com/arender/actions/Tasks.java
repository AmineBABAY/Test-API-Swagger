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

    private Response evicResponses;

    private String name;

    private int numberOfSuccessRequest = 0;

    public Tasks(File fileToUpload) throws Exception
    {

        tabGetImage100pxResponses = new ArrayList<Response>();
        tabGetImage800pxResponses = new ArrayList<Response>();
        this.name = Thread.currentThread().getName().substring(7) + " of :"
                + Thread.currentThread().getName().substring(0, 6) + "file :" + fileToUpload.getName();
        // upload
        uploadResponse = Documents.uploadDocument(fileToUpload, "doc ");

        if (uploadResponse.getStatusCode() == 200)
        {
            this.numberOfSuccessRequest++;
        }
        // get Layout
        String idDoc = JsonPath.from(uploadResponse.asString()).get("id");
        getLayoutResponse = Documents.getDocumentLayout(idDoc);
        JsonPath layoutResponse = JsonPath.from(getLayoutResponse.asString());

        if (getLayoutResponse.getStatusCode() == 200)
        {
            this.numberOfSuccessRequest++;
        }
        // get all image with different
        for (int i = 0; i < layoutResponse.getList("pageDimensionsList").size(); i++)
        {
            Response getImage = Documents.getPageImage(idDoc, i, "IM_100_0");
            if (getImage.getStatusCode() == 200)
            {
                this.numberOfSuccessRequest++;
            }
            tabGetImage100pxResponses.add(getImage);
            Response getImageFullScreen = Documents.getPageImage(idDoc, i, "IM_800_0");
            if (getImageFullScreen.getStatusCode() == 200)
            {
                this.numberOfSuccessRequest++;
            }
            tabGetImage800pxResponses.add(getImageFullScreen);
        }
        evicResponses = Documents.evictDocument(idDoc);
        if (evicResponses.getStatusCode() == 200)
        {
            this.numberOfSuccessRequest++;
        }
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

    public Response getEvicResponses()
    {
        return evicResponses;
    }

    public void setEvicResponses(Response evicResponses)
    {
        this.evicResponses = evicResponses;
    }

    public int getNumberOfSuccessRequest()
    {
        return numberOfSuccessRequest;
    }

    public void setNumberOfSuccessRequest(int numberOfSuccessRequest)
    {
        this.numberOfSuccessRequest = numberOfSuccessRequest;
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
