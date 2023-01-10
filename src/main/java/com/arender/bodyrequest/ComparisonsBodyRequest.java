package com.arender.bodyrequest;

public class ComparisonsBodyRequest {
	
	private int fuzz;
	private String highlightColor;
	private String leftDocumentId;
	private String lowlightColor;
	private String rightDocumentId;
	
    public ComparisonsBodyRequest(int fuzz, String highlightColor, String leftDocumentId, String lowlightColor,
            String rightDocumentId)
    {
        this.fuzz = fuzz;
        this.highlightColor = highlightColor;
        this.leftDocumentId = leftDocumentId;
        this.lowlightColor = lowlightColor;
        this.rightDocumentId = rightDocumentId;
    }

    public int getFuzz()
    {
        return fuzz;
    }

    public void setFuzz(int fuzz)
    {
        this.fuzz = fuzz;
    }

    public String getHighlightColor()
    {
        return highlightColor;
    }

    public void setHighlightColor(String highlightColor)
    {
        this.highlightColor = highlightColor;
    }

    public String getLeftDocumentId()
    {
        return leftDocumentId;
    }

    public void setLeftDocumentId(String leftDocumentId)
    {
        this.leftDocumentId = leftDocumentId;
    }

    public String getLowlightColor()
    {
        return lowlightColor;
    }

    public void setLowlightColor(String lowlightColor)
    {
        this.lowlightColor = lowlightColor;
    }

    public String getRightDocumentId()
    {
        return rightDocumentId;
    }

    public void setRightDocumentId(String rightDocumentId)
    {
        this.rightDocumentId = rightDocumentId;
    }
    
	
	

	
}
