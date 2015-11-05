package com.armedia.acm.services.dataaccess.model;

import java.util.List;

/**
 * Access Control List.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 05.11.2015.
 */
public class AccessControlList
{
    List<AccessControlEntry> accessControlEntryList;

    public List<AccessControlEntry> getAccessControlEntryList()
    {
        return accessControlEntryList;
    }

    public void setAccessControlEntryList(List<AccessControlEntry> accessControlEntryList)
    {
        this.accessControlEntryList = accessControlEntryList;
    }
}
