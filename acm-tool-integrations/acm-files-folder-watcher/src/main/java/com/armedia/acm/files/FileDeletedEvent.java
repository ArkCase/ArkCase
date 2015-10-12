package com.armedia.acm.files;

import org.apache.commons.vfs2.FileChangeEvent;

/**
 * Created by riste.tutureski on 10/8/2015.
 */
public class FileDeletedEvent extends FileEvent
{
    /**
     * Create a new FileDeletedEvent.
     *
     * @param source file change event
     */
    public FileDeletedEvent(FileChangeEvent source)
    {
        super(source);
    }
}
