package com.armedia.acm.service.outlook.model;

import microsoft.exchange.webservices.data.property.complex.CompleteName;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookContactItem extends OutlookItem
{
    private String surname;
    private String displayName;
    private String companyName;
    private EmailAddress emailAddress1;
    private String primaryTelephone;
    private EmailAddress emailAddress2;
    private String completeName;

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getSurname()
    {
        return surname;
    }


    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setEmailAddress1(EmailAddress emailAddress1)
    {
        this.emailAddress1 = emailAddress1;
    }

    public EmailAddress getEmailAddress1()
    {
        return emailAddress1;
    }

    public void setPrimaryTelephone(String primaryTelephone)
    {
        this.primaryTelephone = primaryTelephone;
    }

    public String getPrimaryTelephone()
    {
        return primaryTelephone;
    }

    public void setEmailAddress2(EmailAddress emailAddress2)
    {
        this.emailAddress2 = emailAddress2;
    }

    public EmailAddress getEmailAddress2()
    {
        return emailAddress2;
    }

    public void setCompleteName(String completeName)
    {
        this.completeName = completeName;
    }

    public String getCompleteName()
    {
        return completeName;
    }

    @Override
    public String toString()
    {
        return "OutlookContactItem{" +
                "surname='" + surname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", emailAddress1=" + emailAddress1 +
                ", primaryTelephone='" + primaryTelephone + '\'' +
                ", emailAddress2=" + emailAddress2 +
                ", completeName='" + completeName + '\'' +
                "} " + super.toString();
    }
}
