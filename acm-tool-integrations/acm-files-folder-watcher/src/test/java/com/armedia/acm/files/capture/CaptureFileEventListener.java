package com.armedia.acm.files.capture;

import com.armedia.acm.files.FileAddedEvent;
import com.armedia.acm.files.FileEvent;
import org.springframework.context.ApplicationListener;

public class CaptureFileEventListener implements ApplicationListener<FileEvent>
{
    private int addedCount;

    @Override
    public void onApplicationEvent(FileEvent fileEvent)
    {
        if ( fileEvent instanceof FileAddedEvent)
        {
            addedCount++;
        }
    }

    public int getAddedCount()
    {
        return addedCount;
    }
}
