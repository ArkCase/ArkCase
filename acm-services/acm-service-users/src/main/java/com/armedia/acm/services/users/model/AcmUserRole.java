package com.armedia.acm.services.users.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "ACM_USER_ROLE")
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
    private String userRoleState;

    @PrePersist
    public void preInsert()
    {
        setUserRoleState("VALID");
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

    public String getUserRoleState()
    {
        return userRoleState;
    }

    public void setUserRoleState(String userRoleState)
    {
        this.userRoleState = userRoleState;
    }
}
