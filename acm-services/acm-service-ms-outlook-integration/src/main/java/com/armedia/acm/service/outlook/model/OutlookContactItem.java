package com.armedia.acm.service.outlook.model;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookContactItem extends OutlookItem
{
    private String surname;
    private String displayName;
    private String companyName;
    private String emailAddress1;
    private String primaryTelephone;
    private String emailAddress2;
    private String completeName;

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getEmailAddress1()
    {
        return emailAddress1;
    }

    public void setEmailAddress1(String emailAddress1)
    {
        this.emailAddress1 = emailAddress1;
    }

    public String getPrimaryTelephone()
    {
        return primaryTelephone;
    }

    public void setPrimaryTelephone(String primaryTelephone)
    {
        this.primaryTelephone = primaryTelephone;
    }

    public String getEmailAddress2()
    {
        return emailAddress2;
    }

    public void setEmailAddress2(String emailAddress2)
    {
        this.emailAddress2 = emailAddress2;
    }

    public String getCompleteName()
    {
        return completeName;
    }

    public void setCompleteName(String completeName)
    {
        this.completeName = completeName;
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
