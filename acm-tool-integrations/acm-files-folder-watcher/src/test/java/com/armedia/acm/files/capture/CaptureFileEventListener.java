package com.armedia.acm.files.capture;

import org.springframework.context.ApplicationListener;

public class CaptureFileEventListener implements ApplicationListener<AbstractCaptureFileEvent>
{
    private int addedCount;

    @Override
    public void onApplicationEvent(AbstractCaptureFileEvent abstractCaptureFileEvent)
    {
        if ( abstractCaptureFileEvent instanceof CaptureFileAddedEvent )
        {
            addedCount++;
        }
    }

    public int getAddedCount()
    {
        return addedCount;
    }
}
