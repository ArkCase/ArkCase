package com.armedia.acm.services.transcribe.model;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/08/2018
 */
public class TranscribeBusinessProcessModel
{
    private String type;
    private boolean start;
    private String name;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public boolean isStart()
    {
        return start;
    }

    public void setStart(boolean start)
    {
        this.start = start;
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
