package com.armedia.acm.services.notification.service.provider.model;

import java.util.List;

public class DocumentLinkedModel
{
    private final List<String> links;
    private final List<String> fileNames;
    private final String objectType;
    private final String objectNumber;

    public DocumentLinkedModel(List<String> links, List<String> fileNames, String objectType, String objectNumber)
    {
        this.links = links;
        this.fileNames = fileNames;
        this.objectType = objectType;
        this.objectNumber = objectNumber;
    }

    public List<String> getLinks() 
    {
        return links;
    }

    public List<String> getFileNames() 
    {
        return fileNames;
    }

    public String getObjectType() 
    {
        return objectType;
    }

    public String getObjectNumber() 
    {
        return objectNumber;
    }
}
