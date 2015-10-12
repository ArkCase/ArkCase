package com.armedia.acm.files;

import org.apache.commons.vfs2.FileChangeEvent;

/**
 * Created by riste.tutureski on 10/8/2015.
 */
public class FileChangedEvent extends FileEvent
{
    /**
     * Create a new FileChangedEvent.
     *
     * @param source file change event
     */
    public FileChangedEvent(FileChangeEvent source)
    {
        super(source);
    }
}
