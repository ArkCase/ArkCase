package com.armedia.acm.spring;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Watch for file events in the ACM Spring dynamic config folder.  When Spring
 * files are removed, call the Spring context holder to remove the corresponding
 * child context.
 */
public class SpringFileWatcher implements FileListener
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private SpringContextHolder springContextHolder;

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception
    {
        log.debug("file added: " + fileChangeEvent.getFile().getName());
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception
    {
        log.debug("file deleted: " + fileChangeEvent.getFile().getName());
        getSpringContextHolder().removeContext(fileChangeEvent.getFile().getName().getBaseName());
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception
    {
        log.debug("file changed: " + fileChangeEvent.getFile().getName());
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }


}
