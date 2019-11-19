package com.armedia.acm.configuration.util;

public enum MergeFlags
{
    REMOVE("^"),
    MERGE("~");

    private String symbol;

    MergeFlags(String symbol)
    {
        this.symbol = symbol;
    }

    public String getSymbol()
    {
        return symbol;
    }

}
