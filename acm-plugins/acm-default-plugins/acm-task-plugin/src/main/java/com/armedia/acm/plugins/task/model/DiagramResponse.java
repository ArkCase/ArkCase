package com.armedia.acm.plugins.task.model;

import java.io.Serializable;

/**
 * Created by riste.tutureski on 5/12/2017.
 */
public class DiagramResponse implements Serializable
{
    private byte[] data;

    public DiagramResponse()
    {

    }

    public DiagramResponse(byte[] data)
    {
        this.data = data;
    }

    public byte[] getData()
    {
        return data;
    }

    public void setData(byte[] data)
    {
        this.data = data;
    }
}
