package com.armedia.acm.services.notification.service.provider.model;

import java.util.List;

public class DocumentLinkedModel
{
    private final List<String> tokens;
    private final List<String> fileNames;

    public DocumentLinkedModel(List<String> tokens, List<String> fileNames)
    {
        this.tokens = tokens;
        this.fileNames = fileNames;
    }

    public List<String> getTokens() 
    {
        return tokens;
    }

    public List<String> getFileNames() 
    {
        return fileNames;
    }
}
