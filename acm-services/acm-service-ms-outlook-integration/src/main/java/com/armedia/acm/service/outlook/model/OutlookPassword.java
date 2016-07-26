package com.armedia.acm.service.outlook.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "acm_outlook_password")
public class OutlookPassword implements Serializable
{

    private static final long serialVersionUID = 2137276395926052320L;

    @Id
    @Column(name = "cm_user_id")
    private String userId;

    @Column(name = "cm_outlook_password")
    private String outlookPassword;

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getOutlookPassword()
    {
        return outlookPassword;
    }

    public void setOutlookPassword(String outlookPassword)
    {
        this.outlookPassword = outlookPassword;
    }
}
