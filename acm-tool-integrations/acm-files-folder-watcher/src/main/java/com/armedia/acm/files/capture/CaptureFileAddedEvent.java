package com.armedia.acm.files.capture;

import org.apache.commons.vfs2.FileChangeEvent;

public class CaptureFileAddedEvent extends AbstractCaptureFileEvent
{
    public CaptureFileAddedEvent(FileChangeEvent source)
    {
        super(source);
    }
}

