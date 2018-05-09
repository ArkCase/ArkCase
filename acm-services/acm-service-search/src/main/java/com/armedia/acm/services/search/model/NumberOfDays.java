package com.armedia.acm.services.search.model;

/**
 * Created by marjan.stefanoski on 9/8/2014.
 */
public enum NumberOfDays
{

    ONE_YEAR(365, "one year"), SEVEN_DAYS(7, "seven days"), THIRTY_DAYS(30, "thirty days");

    private int numOfDays;
    private String nDays;
    NumberOfDays(int numOfDays, String days)
    {
        this.numOfDays = numOfDays;
        this.nDays = days;
    }

    public static NumberOfDays getNumberOfDays(int days)
    {
        for (NumberOfDays attribute : values())
        {
            if (attribute.getNumOfDays() == days)
            {
                return attribute;
            }
        }
        return null;
    }

    public int getNumOfDays()
    {
        return numOfDays;
    }

    public String getnDays()
    {
        return nDays;
    }
}
