package com.armedia.acm.files;

import org.apache.commons.vfs2.FileChangeEvent;

/**
 * Created by dmiller on 2/20/14.
 */
public class ConfigurationFileDeletedEvent extends AbstractConfigurationFileEvent
{
    public ConfigurationFileDeletedEvent(FileChangeEvent source)
    {
        super(source);
    }
}
