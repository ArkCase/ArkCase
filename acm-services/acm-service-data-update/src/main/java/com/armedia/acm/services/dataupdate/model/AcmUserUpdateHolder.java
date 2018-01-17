package com.armedia.acm.services.dataupdate.model;

import com.armedia.acm.services.users.model.AcmUser;

import java.util.Objects;

public class AcmUserUpdateHolder
{
    private String oldId;

    private String newId;

    private AcmUser newUser;

    public AcmUserUpdateHolder(String oldId, String newId, AcmUser newUser)
    {
        this.oldId = oldId;
        this.newId = newId;
        this.newUser = newUser;
    }

    public String getOldId()
    {
        return oldId;
    }

    public String getNewId()
    {
        return newId;
    }

    public AcmUser getNewUser()
    {
        return newUser;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AcmUserUpdateHolder holder = (AcmUserUpdateHolder) o;
        return Objects.equals(oldId, holder.oldId) &&
                Objects.equals(newId, holder.newId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(oldId, newId);
    }
}
