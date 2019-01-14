package com.armedia.acm.plugins.ecm.model;

import java.io.SequenceInputStream;

public class SequenceInputStreamHolder
{
    private SequenceInputStream stream;
    private long size;

    public SequenceInputStream getStream() {
        return stream;
    }

    public void setStream(SequenceInputStream stream) {
        this.stream = stream;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
