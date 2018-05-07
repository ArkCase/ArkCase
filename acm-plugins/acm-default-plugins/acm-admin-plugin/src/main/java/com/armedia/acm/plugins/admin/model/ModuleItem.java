package com.armedia.acm.plugins.admin.model;

public class ModuleItem
{
    private String name;
    private String id;
    private String privilege;
    private String key;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPrivilege()
    {
        return privilege;
    }

    public void setPrivilege(String privilege)
    {
        this.privilege = privilege;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }
}
