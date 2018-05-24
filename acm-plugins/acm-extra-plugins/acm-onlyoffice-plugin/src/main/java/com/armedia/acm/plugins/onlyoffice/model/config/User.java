package com.armedia.acm.plugins.onlyoffice.model.config;

public class User
{
    /**
     * the first name of the user. Deprecated since version 4.2, please use name instead
     */
    @Deprecated
    private String firstname;
    /**
     * the identification of the user
     */
    private String id;
    /**
     * the last name of the user. Deprecated since version 4.2, please use name instead
     */
    @Deprecated
    private String lastname;
    /**
     * the full name of the user. Used since version 4.2
     */
    private String name;

    public String getFirstname()
    {
        return firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getLastname()
    {
        return lastname;
    }

    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
