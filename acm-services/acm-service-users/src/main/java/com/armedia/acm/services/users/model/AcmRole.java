package com.armedia.acm.services.users.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "ACM_ROLE")
public class AcmRole implements AcmLdapEntity
{
    @Id
    @Column(name = "CM_ROLE_NAME")
    private String roleName;

    @Column(name = "CM_ROLE_TYPE")
    private String roleType;

    @Transient
    private String distinguishedName;

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    public String getRoleType()
    {
        return roleType;
    }

    public void setRoleType(String roleType)
    {
        this.roleType = roleType;
    }

    @Override
    public String getDistinguishedName()
    {
        return distinguishedName;
    }

    @Override
    public void setDistinguishedName(String distinguishedName)
    {
        this.distinguishedName = distinguishedName;
    }

    @Override
    public boolean isGroup()
    {
        return true;
    }


}
