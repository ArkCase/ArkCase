package com.armedia.acm.plugins.casefile.dao;

import java.util.Properties;

public class QueuePropertyFileDao extends AcmQueueDao
{


    //private final Logger log = LoggerFactory.getLogger(getClass());

    private Properties queueNamesProperties;

    public Properties getQueueNamesProperties()
    {
        return queueNamesProperties;
    }

    public void setQueueNamesProperties(Properties queueNamesProperties)
    {
        this.queueNamesProperties = queueNamesProperties;
    }


}
