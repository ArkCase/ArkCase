package com.armedia.acm.service.identifier.model;

import java.io.Serializable;

/**
 * this POJO holds information of Arkcase installation id
 */
public class AcmArkcaseIdentity implements Serializable
{
    /**
     * holds id information of current instance
     */
    private String instanceID;
    /**
     * holds id information of arkcase installation
     */
    private String globalID;

    public String getInstanceID()
    {
        return instanceID;
    }

    public void setInstanceID(String instanceID)
    {
        this.instanceID = instanceID;
    }

    public String getGlobalID()
    {
        return globalID;
    }

    public void setGlobalID(String globalID)
    {
        this.globalID = globalID;
    }
}
