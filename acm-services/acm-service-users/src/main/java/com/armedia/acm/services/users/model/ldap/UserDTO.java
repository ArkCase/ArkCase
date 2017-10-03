package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.AcmUser;

import java.util.List;

/**
 * Ldap User POST JSON request
 */
@PasswordValidation(message = "Password pattern rule validations")
public class UserDTO
{
    private AcmUser acmUser;
    private String password;
    private List<String> groupNames;

    public AcmUser getAcmUser()
    {
        return acmUser;
    }

    public void setAcmUser(AcmUser acmUser)
    {
        this.acmUser = acmUser;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public List<String> getGroupNames()
    {
        return groupNames;
    }

    public void setGroupNames(List<String> groupNames)
    {
        this.groupNames = groupNames;
    }
}
