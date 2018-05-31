package com.armedia.acm.plugins.onlyoffice.model.config;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Customer
{
    /**
     * postal address of the above company or person
     */
    private String address;
    /**
     * some information about the above company or person which will be displayed at the About page and visible to all
     * editor users
     */
    private String info;
    /**
     * the path to the image logo which will be displayed at the About page (there are no special recommendations for
     * this file, but it would be better if it were in .png format with transparent background). The image must have the
     * following size: 432x70
     */
    private String logo;
    /**
     * email address of the above company or person
     */
    private String mail;
    /**
     * the name of the company or person who gives access to the editors or the editor authors
     */
    private String name;
    /**
     * home website address of the above company or person
     */
    private String www;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public String getLogo()
    {
        return logo;
    }

    public void setLogo(String logo)
    {
        this.logo = logo;
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getWww()
    {
        return www;
    }

    public void setWww(String www)
    {
        this.www = www;
    }
}
