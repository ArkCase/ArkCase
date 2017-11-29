package com.armedia.acm.services.users.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "acm_user_role")
@IdClass(AcmUserRolePrimaryKey.class)
public class AcmUserRole
{
    @Id
    @Column(name = "cm_role_name")
    private String roleName;

    @Id
    @Column(name = "cm_user_id")
    private String userId;

    @Column(name = "cm_user_role_state")
    @Enumerated(EnumType.STRING)
    private AcmUserRoleState userRoleState;

    @PrePersist
    public void preInsert()
    {
        setUserRoleState(AcmUserRoleState.VALID);
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public AcmUserRoleState getUserRoleState()
    {
        return userRoleState;
    }

    public void setUserRoleState(AcmUserRoleState userRoleState)
    {
        this.userRoleState = userRoleState;
    }

}
