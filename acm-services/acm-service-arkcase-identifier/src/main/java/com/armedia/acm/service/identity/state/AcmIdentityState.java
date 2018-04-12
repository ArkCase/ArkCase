package com.armedia.acm.service.identity.state;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;

public class AcmIdentityState extends StateOfModule
{
    /**
     * local instance id
     */
    private String instanceID;
    /**
     * global shared id
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
