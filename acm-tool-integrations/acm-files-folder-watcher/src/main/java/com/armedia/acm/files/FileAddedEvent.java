package com.armedia.acm.files;

import org.apache.commons.vfs2.FileChangeEvent;

/**
 * Created by riste.tutureski on 10/8/2015.
 */
public class FileAddedEvent extends FileEvent
{
    /**
     * Create a new FileAddedEvent.
     *
     * @param source file change event
     */
    public FileAddedEvent(FileChangeEvent source)
    {
        super(source);
    }
}
