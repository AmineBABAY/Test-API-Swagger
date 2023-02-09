package com.arender.actions;

import java.io.File;
import java.util.ArrayList;

import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Tasks extends AssertActions
{
    private ArrayList<Response> tabResponses;

    private ArrayList<String> nameOfResponses;;

    private String name;

    private int numberOfSuccessRequest = 0;

    public Tasks(File fileToUpload) throws Exception
    {
        tabResponses = new ArrayList<Response>();
        nameOfResponses = new ArrayList<String>();
        this.name = Thread.currentThread().getName().substring(7) + " of :"
                + Thread.currentThread().getName().substring(0, 6) + "file :" + fileToUpload.getName();
        // upload
        Response upload = Documents.uploadDocument(fileToUpload, "doc ");
        tabResponses.add(upload);
        nameOfResponses.add("upload document");
        if (upload.getStatusCode() == 200)
        {
            this.numberOfSuccessRequest++;
        }
        // get Layout
        String idDoc = JsonPath.from(upload.asString()).get("id");
        Response getLayout = Documents.getDocumentLayout(idDoc);
        JsonPath layoutResponse = JsonPath.from(getLayout.asString());

        nameOfResponses.add("get document layout");
        if (getLayout.getStatusCode() == 200)
        {
            this.numberOfSuccessRequest++;
        }
        tabResponses.add(getLayout);
        // get all image with different
        for (int i = 0; i < layoutResponse.getList("pageDimensionsList").size(); i++)
        {
            Response getImage = Documents.getPageImage(idDoc, i, "IM_100_0");
            nameOfResponses.add("get page image resolution: 100px");
            if (getImage.getStatusCode() == 200)
            {
                this.numberOfSuccessRequest++;
            }
            tabResponses.add(getImage);
            Response getImageFullScreen = Documents.getPageImage(idDoc, i, "IM_800_0");
            nameOfResponses.add("Page" + "get page image resolution: 800px");
            if (getImageFullScreen.getStatusCode() == 200)
            {
                this.numberOfSuccessRequest++;
            }
            tabResponses.add(getImageFullScreen);
        }
        Response evictDocument = Documents.evictDocument(idDoc);
        nameOfResponses.add("evict document");
        if (evictDocument.getStatusCode() == 200)
        {
            this.numberOfSuccessRequest++;
        }
        tabResponses.add(evictDocument);
    }

    public ArrayList<String> getNameOfResponses()
    {
        return nameOfResponses;
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

    public ArrayList<Response> getTabResponses()
    {
        return tabResponses;
    }
}
