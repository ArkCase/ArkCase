package com.armedia.acm.service.outlook.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 * Created by armdev on 4/16/15.
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class OutlookDTO
{
    private String outlookPassword;

    public String getOutlookPassword()
    {
        return outlookPassword;
    }

    public void setOutlookPassword(String outlookPassword)
    {
        this.outlookPassword = outlookPassword;
    }
}
