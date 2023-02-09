package com.arender.bodyrequest;

public class ConversionsBodyRequest
{

    private String documentId;

    private String format;

    public ConversionsBodyRequest(String documentId, String format)
    {
        this.documentId = documentId;
        this.format = format;
    }

    public String getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(String documentId)
    {
        this.documentId = documentId;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

}
