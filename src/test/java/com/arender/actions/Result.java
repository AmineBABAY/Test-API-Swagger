package com.arender.actions;

import java.util.ArrayList;

public class Result
{
    private String name;

    private ArrayList<Long> uploadList = new ArrayList<>();

    private ArrayList<Long> getLayoutList = new ArrayList<>();

    private ArrayList<Long> getBookmarksList = new ArrayList<>();

    private ArrayList<Long> getImage100pxList = new ArrayList<>();

    private ArrayList<Long> getImage800pxList = new ArrayList<>();

    private ArrayList<Long> getTextPositionList = new ArrayList<>();

    private ArrayList<Long> evictList = new ArrayList<>();

    public Result(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList<Long> getUploadList()
    {
        return uploadList;
    }

    public void setUploadList(ArrayList<Long> uploadList)
    {
        this.uploadList = uploadList;
    }

    public ArrayList<Long> getGetLayoutList()
    {
        return getLayoutList;
    }

    public void setGetLayoutList(ArrayList<Long> getLayoutList)
    {
        this.getLayoutList = getLayoutList;
    }

    public ArrayList<Long> getGetBookmarksList()
    {
        return getBookmarksList;
    }

    public void setGetBookmarksList(ArrayList<Long> getBookmarksList)
    {
        this.getBookmarksList = getBookmarksList;
    }

    public ArrayList<Long> getGetImage100pxList()
    {
        return getImage100pxList;
    }

    public void setGetImage100pxList(ArrayList<Long> getImage100pxList)
    {
        this.getImage100pxList = getImage100pxList;
    }

    public ArrayList<Long> getGetImage800pxList()
    {
        return getImage800pxList;
    }

    public void setGetImage800pxList(ArrayList<Long> getImage800pxList)
    {
        this.getImage800pxList = getImage800pxList;
    }

    public ArrayList<Long> getGetTextPositionList()
    {
        return getTextPositionList;
    }

    public void setGetTextPositionList(ArrayList<Long> getTextPositionList)
    {
        this.getTextPositionList = getTextPositionList;
    }

    public ArrayList<Long> getEvictList()
    {
        return evictList;
    }

    public void setEvictList(ArrayList<Long> evictList)
    {
        this.evictList = evictList;
    }
}
