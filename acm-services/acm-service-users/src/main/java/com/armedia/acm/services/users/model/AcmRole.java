package com.armedia.acm.services.users.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "acm_role")
public class AcmRole
{
    @Id
    @Column(name = "cm_role_name")
    private String roleName;

    @Column(name = "cm_role_type")
    @Enumerated(EnumType.STRING)
    private AcmRoleType roleType;

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    public AcmRoleType getRoleType()
    {
        return roleType;
    }

    public void setRoleType(AcmRoleType roleType)
    {
        this.roleType = roleType;
    }
}
