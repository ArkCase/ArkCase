package com.armedia.acm.services.email.service;

import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 27, 2017
 *
 */
public class AcmAddressBookItem
{

    private String name;

    private String emailAddress;

    private Map<String, String> additionalDetails;

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }

    /**
     * @param emailAddress
     *            the emailAddress to set
     */
    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    /**
     * @return the additionalDetails
     */
    public Map<String, String> getAdditionalDetails()
    {
        return additionalDetails;
    }

    /**
     * @param additionalDetails
     *            the additionalDetails to set
     */
    public void setAdditionalDetails(Map<String, String> additionalDetails)
    {
        this.additionalDetails = additionalDetails;
    }

}
