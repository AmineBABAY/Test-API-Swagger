package com.arender.actions;

import java.util.ArrayList;

import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Tasks extends AssertActions {
    private ArrayList<Response> tabResponses;
    private String name;
    private int numberOfSuccessRequest=0;

    public Tasks(String file)
    {  
        tabResponses= new ArrayList<Response>();
        Thread.currentThread().setName("Im the user : " +Thread.currentThread().getName());
        this.name=Thread.currentThread().getName();
        for(int i=1;i<=3;i++)
        {
        Response upload=Documents.uploadDocument(file);
        tabResponses.add(upload);
        if(upload.getStatusCode()==200)
        {
            this.numberOfSuccessRequest++;
        }
        Response getLayout=Documents.getDocumentLayout(JsonPath.from(upload.asString()).get("id"));
        if(getLayout.getStatusCode()==200)
        {
            this.numberOfSuccessRequest++;
        }
        tabResponses.add(getLayout);
        Response getImage = Documents.getPageImage(JsonPath.from(upload.asString()).get("id"), 0);
        if(getImage.getStatusCode()==200)
        {
            this.numberOfSuccessRequest++;
        }
        tabResponses.add(getImage);
        

        }
        
        
        
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
