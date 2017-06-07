package com.armedia.acm.plugins.ecm.model;

/**
 * Created by riste.tutureski on 6/7/2017.
 */
public class Range
{
    private long start;
    private long end;
    private long length;
    private long total;

    /**
     * Construct a byte range.
     * @param start Start of the byte range.
     * @param end End of the byte range.
     * @param total Total length of the byte source.
     */
    public Range(long start, long end, long total)
    {
        this.start = start;
        this.end = end;
        this.length = end - start + 1;
        this.total = total;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start)
    {
        this.start = start;
    }

    public long getEnd()
    {
        return end;
    }

    public void setEnd(long end)
    {
        this.end = end;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length)
    {
        this.length = length;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }
}
