package com.armedia.acm.plugins.ecm.model;

public enum FileUploadStage
{
    DIVIDE_FILE_INTO_CHUNKS(1),
    UPLOAD_CHUNKS_TO_FILESYSTEM(2),
    UPLOAD_TO_ALFRESCO(3);

    private final int value;

    FileUploadStage(final int newValue)
    {
        value = newValue;
    }

    public int getValue()
    {
        return value;
    }

}
