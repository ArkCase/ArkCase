package com.armedia.acm.snowbound.model;

/**
 * Created by joseph.mcgrady on 9/2/2015.
 */
public class AcmDocument {
    private byte[] data;
    private String name;

    public AcmDocument(byte[] data, String name) {
        this.data = data;
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}