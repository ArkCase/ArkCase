package com.armedia.acm.services.email.model;

public class EmailWithEmbeddedLinksResultDTO
{

    private String emailAddress;

    private boolean state;

    public EmailWithEmbeddedLinksResultDTO()
    {
    }

    public EmailWithEmbeddedLinksResultDTO(String emailAddress, boolean state)
    {
        this.emailAddress = emailAddress;
        this.state = state;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public boolean isState()
    {
        return state;
    }

    public void setState(boolean state)
    {
        this.state = state;
    }

}
