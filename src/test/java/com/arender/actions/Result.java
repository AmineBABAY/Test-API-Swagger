package com.arender.actions;

import java.util.ArrayList;

public class Result
{
    private int numberOfUsers;

    private String name;

    private ArrayList<Long> uploadList = new ArrayList<>();

    private ArrayList<Long> getLayoutList = new ArrayList<>();

    private ArrayList<Long> getBookmarksList = new ArrayList<>();

    private ArrayList<Long> getImage100pxList = new ArrayList<>();

    private ArrayList<Long> getImage800pxList = new ArrayList<>();

    private ArrayList<Long> getTextPositionList = new ArrayList<>();

    private ArrayList<Long> evictList = new ArrayList<>();

    private int totalUploadOK = 0, totalGetLayoutOK = 0, totalGetBoomarksOK = 0, totalGetImage100pxOK = 0,
            totalGetImage800pxOK = 0, totalGetTextPositionOK = 0, totalEvictOK = 0;

    public int getNumberOfUsers()
    {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers)
    {
        this.numberOfUsers = numberOfUsers;
    }

    public int getTotalGetLayoutOK()
    {
        return totalGetLayoutOK;
    }

    public void setTotalGetLayoutOK(int totalGetLayoutOK)
    {
        this.totalGetLayoutOK = totalGetLayoutOK;
    }

    public void incrementTotalEvictOK()
    {
        this.totalEvictOK++;
    }

    public void incrementTotalGetTextPositionOK()
    {
        this.totalGetTextPositionOK++;
    }

    public void incrementTotalGetImage800pxOK()
    {
        this.totalGetImage800pxOK++;
    }

    public void incrementTotalGetImage100pxOK()
    {
        this.totalGetImage100pxOK++;
    }

    public void incrementTotalGetBoomarksOK()
    {
        this.totalGetBoomarksOK++;
    }

    public void incrementTotalgetLayoutOK()
    {
        this.totalGetLayoutOK++;
    }

    public int getTotalUploadOK()
    {
        return totalUploadOK;
    }

    public void setTotalUploadOK(int totalUploadOK)
    {
        this.totalUploadOK = totalUploadOK;
    }

    public int getTotalgetLayoutOK()
    {
        return totalGetLayoutOK;
    }

    public void setTotalgetLayoutOK(int totalgetLayoutOK)
    {
        this.totalGetLayoutOK = totalgetLayoutOK;
    }

    public int getTotalGetBoomarksOK()
    {
        return totalGetBoomarksOK;
    }

    public void setTotalGetBoomarksOK(int totalGetBoomarksOK)
    {
        this.totalGetBoomarksOK = totalGetBoomarksOK;
    }

    public int getTotalGetImage100pxOK()
    {
        return totalGetImage100pxOK;
    }

    public void setTotalGetImage100pxOK(int totalGetImage100pxOK)
    {
        this.totalGetImage100pxOK = totalGetImage100pxOK;
    }

    public int getTotalGetImage800pxOK()
    {
        return totalGetImage800pxOK;
    }

    public void setTotalGetImage800pxOK(int totalGetImage800pxOK)
    {
        this.totalGetImage800pxOK = totalGetImage800pxOK;
    }

    public int getTotalGetTextPositionOK()
    {
        return totalGetTextPositionOK;
    }

    public void setTotalGetTextPositionOK(int totalGetTextPositionOK)
    {
        this.totalGetTextPositionOK = totalGetTextPositionOK;
    }

    public int getTotalEvictOK()
    {
        return totalEvictOK;
    }

    public void setTotalEvictOK(int totalEvictOK)
    {
        this.totalEvictOK = totalEvictOK;
    }

    public void incrementTotalUploadOK()
    {
        this.totalUploadOK++;
    }

    public void setTotalUpladOK(int totalUploadOK)
    {
        this.totalUploadOK = totalUploadOK;
    }

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
