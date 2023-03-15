package com.arender.actions;

import java.io.File;
import java.time.LocalTime;
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

    private Response getBookmarksResponse;

    private ArrayList<Response> getImage100pxResponses;

    private ArrayList<Response> getImage800pxResponses;

    private ArrayList<Response> getTextPositionResponses;

    private Response evictResponse;

    private String name;

    public Tasks(File fileToUpload) throws Exception
    {
        getImage100pxResponses = new ArrayList<Response>();
        getImage800pxResponses = new ArrayList<Response>();
        getTextPositionResponses = new ArrayList<Response>();
        this.name = fileToUpload.getName();
        String idDoc = "";
        JsonPath layoutResponse = null;
        try
        {
            uploadResponse = Documents.uploadDocument(fileToUpload, "doc ");
            LOGGER.info("Upload " + name + " Code status : " + uploadResponse.statusCode());
            idDoc = JsonPath.from(uploadResponse.asString()).get("id");
        }
        catch (Exception e)
        {
            LOGGER.info("task exception upload : " + e.getMessage() + " Time : " + LocalTime.now() + " file : "
                    + this.name);
        }
        try
        {
            getLayoutResponse = Documents.getDocumentLayout(idDoc);
            LOGGER.info("getLayout " + name + " Code status : " + getLayoutResponse.statusCode());
            layoutResponse = JsonPath.from(getLayoutResponse.asString());
        }
        catch (Exception e)
        {
            LOGGER.info("task exception getLayout : " + e.getMessage() + " Time : " + LocalTime.now() + " file : "
                    + this.name);
        }
        try
        {
            getBookmarksResponse = Documents.getBookmarks(idDoc);
            LOGGER.info("getBookmarks " + name + " Code status : " + getBookmarksResponse.statusCode());
        }
        catch (Exception e)
        {
            LOGGER.info("task exception getBookmarks : " + e.getMessage() + " Time : " + LocalTime.now() + " file : "
                    + this.name);
        }

        // get all image with different
        int numberOfPages = layoutResponse.getList("pageDimensionsList").size();
        for (int i = 0; i < numberOfPages; i++)
        {
            try
            {
                Response getImage = Documents.getPageImage(idDoc, i, "IM_100_0");
                LOGGER.info("getImage100px " + name + " Code status : " + getImage.statusCode());
                getImage100pxResponses.add(getImage);
            }
            catch (Exception e)
            {
                LOGGER.info("task exception getPageImage 100 px: " + e.getMessage() + " Time : " + LocalTime.now()
                        + " file : " + this.name);
            }
            try
            {
                Response getImageFullScreen = Documents.getPageImage(idDoc, i, "IM_800_0");
                LOGGER.info("getImage800px " + name + " Code status : " + getImageFullScreen.statusCode());
                getImage800pxResponses.add(getImageFullScreen);
            }
            catch (Exception e)
            {
                LOGGER.info("task exception getPageImage 800 px: " + e.getMessage() + " Time : " + LocalTime.now()
                        + " file : " + this.name);
            }
            try
            {
                Response getTextPosition = Documents.getTextPosition(idDoc, i);
                LOGGER.info("getTextPosition " + name + " Code status : " + getTextPosition.statusCode());
                getTextPositionResponses.add(getTextPosition);
            }
            catch (Exception e)
            {
                LOGGER.info("task exception getTextPosition : " + e.getMessage() + " Time : " + LocalTime.now()
                        + " file : " + this.name);
            }
        }

        try
        {
            evictResponse = Documents.evictDocument(idDoc);
            LOGGER.info("evictResponse " + name + " Code status : " + evictResponse.statusCode());
        }
        catch (Exception e)
        {
            LOGGER.info("task exception : evictDocument" + e.getMessage() + " Time : " + LocalTime.now() + " file : "
                    + this.name);
        }
    }

    public Response getGetBookmarksResponse()
    {
        return getBookmarksResponse;
    }

    public void setGetBookmarksResponse(Response getBookmarksResponse)
    {
        this.getBookmarksResponse = getBookmarksResponse;
    }

    public ArrayList<Response> getGetTextPositionResponses()
    {
        return getTextPositionResponses;
    }

    public void setGetTextPositionResponses(ArrayList<Response> getTextPositionResponses)
    {
        this.getTextPositionResponses = getTextPositionResponses;
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
