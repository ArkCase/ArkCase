package com.armedia.acm.files;

import org.springframework.context.ApplicationListener;

/**
 * Created by dmiller on 2/20/14.
 */
public class ConfigurationFileEventListener implements ApplicationListener<AbstractConfigurationFileEvent>
{
    private int addedCount;
    private int removedCount;
    private int changedCount;

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent abstractConfigurationFileEvent)
    {
        if ( abstractConfigurationFileEvent instanceof ConfigurationFileAddedEvent )
        {
            addedCount++;
        }
        else if ( abstractConfigurationFileEvent instanceof ConfigurationFileChangedEvent )
        {
            changedCount++;
        }
        else if ( abstractConfigurationFileEvent instanceof ConfigurationFileDeletedEvent )
        {
            removedCount++;
        }
    }

    public int getAddedCount()
    {
        return addedCount;
    }

    public int getRemovedCount()
    {
        return removedCount;
    }

    public int getChangedCount()
    {
        return changedCount;
    }
}
