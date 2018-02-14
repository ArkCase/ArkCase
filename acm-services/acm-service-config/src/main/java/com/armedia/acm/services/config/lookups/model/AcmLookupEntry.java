package com.armedia.acm.services.config.lookups.model;

public abstract class AcmLookupEntry
{

    private boolean readonly;

    public boolean isReadonly()
    {
        return readonly;
    }

    public void setReadonly(boolean readonly)
    {
        this.readonly = readonly;
    }

}
