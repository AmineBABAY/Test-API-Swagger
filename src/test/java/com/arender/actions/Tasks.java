package com.arender.actions;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Tasks extends AssertActions
{
    private final static Logger LOGGER = Logger.getLogger(Tasks.class);

    private Response uploadResponse;

    private Response getLayoutResponse;

    private ArrayList<Response> getImage100pxResponses;

    private ArrayList<Response> getImage800pxResponses;

    private Response evictResponse;

    private String name;

    public Tasks(File fileToUpload) throws Exception
    {

        getImage100pxResponses = new ArrayList<Response>();
        getImage800pxResponses = new ArrayList<Response>();
        this.name = Thread.currentThread().getName().substring(7) + " of :"
                + Thread.currentThread().getName().substring(0, 6) + "file :" + fileToUpload.getName();
        // upload
        try
        {
            uploadResponse = Documents.uploadDocument(fileToUpload, "doc ");

            // get Layout
            String idDoc = JsonPath.from(uploadResponse.asString()).get("id");
            getLayoutResponse = Documents.getDocumentLayout(idDoc);
            JsonPath layoutResponse = JsonPath.from(getLayoutResponse.asString());

            // get all image with different
            for (int i = 0; i < layoutResponse.getList("pageDimensionsList").size(); i++)
            {
                Response getImage = Documents.getPageImage(idDoc, i, "IM_100_0");
                getImage100pxResponses.add(getImage);
                Response getImageFullScreen = Documents.getPageImage(idDoc, i, "IM_800_0");
                getImage800pxResponses.add(getImageFullScreen);
            }
            evictResponse = Documents.evictDocument(idDoc);
        }
        catch (Exception e)
        {
            LOGGER.info("task exception : " + e.getMessage());
        }
    }

    public ArrayList<Response> getGetImage100pxResponses()
    {
        return getImage100pxResponses;
    }

    public void setGetImage100pxResponses(ArrayList<Response> getImage100pxResponses)
    {
        this.getImage100pxResponses = getImage100pxResponses;
    }

    public ArrayList<Response> getGetImage800pxResponses()
    {
        return getImage800pxResponses;
    }

    public void setGetImage800pxResponses(ArrayList<Response> getImage800pxResponses)
    {
        this.getImage800pxResponses = getImage800pxResponses;
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
