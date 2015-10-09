package com.armedia.acm.files.capture;

import org.apache.commons.vfs2.FileChangeEvent;

public class ConvertedFileAddedEvent extends AbstractConvertFileEvent
{
    public ConvertedFileAddedEvent(FileChangeEvent source)
    {
        super(source);
    }
}

