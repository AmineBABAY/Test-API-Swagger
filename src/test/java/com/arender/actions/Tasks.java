package com.arender.actions;

import java.util.ArrayList;

import com.arender.endpoint.Documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Tasks extends AssertActions {
    private ArrayList<Response> tabResponses;
    private String name;
    private int numberOfSuccessRequest=0;

    public Tasks(String fileToUpload) throws Exception
    {  
        tabResponses= new ArrayList<Response>();
        this.name=Thread.currentThread().getName().substring(7) +" of :"+ Thread.currentThread().getName().substring(0,6);
        for(int i=1;i<=2;i++)
        {
 
        Response upload=Documents.uploadDocument(fileToUpload);
        tabResponses.add(upload);
        if(upload.getStatusCode()==200)
        {
            this.numberOfSuccessRequest++;
        }
        String idDoc=JsonPath.from(upload.asString()).get("id");
        Response getLayout=Documents.getDocumentLayout(idDoc);
        if(getLayout.getStatusCode()==200)
        {
            this.numberOfSuccessRequest++;
        }
        tabResponses.add(getLayout);
        Response getImage = Documents.getPageImage(idDoc, 0);
        if(getImage.getStatusCode()==200)
        {
            this.numberOfSuccessRequest++;
        }
        tabResponses.add(getImage);
        Response evictDocument= Documents.evictDocument(idDoc);
        if(evictDocument.getStatusCode()==200)
        {
            this.numberOfSuccessRequest++;
        }
        tabResponses.add(evictDocument);
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
