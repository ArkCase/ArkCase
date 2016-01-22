package com.armedia.acm.plugins.dashboard.model.widget;

/**
 * Created by marjan.stefanoski on 10/1/2014.
 */
public class WidgetRoleName
{

    public WidgetRoleName()
    {
        super();
    }

    public WidgetRoleName(String name)
    {
        this.name = name;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WidgetRoleName that = (WidgetRoleName) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return "WidgetRoleName{" +
                "name='" + name + '\'' +
                '}';
    }
}
