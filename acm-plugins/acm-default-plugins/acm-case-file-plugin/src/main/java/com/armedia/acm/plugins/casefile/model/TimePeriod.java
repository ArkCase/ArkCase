package com.armedia.acm.plugins.casefile.model;

/**
 * Created by marjan.stefanoski on 9/8/2014.
 */
public enum TimePeriod {

    ONE_YEAR(365,"one year"),
    SEVEN_DAYS(7,"seven days"),
    THIRTY_DAYS(30,"thirty days");

    TimePeriod(int numOfDays, String days) {
        this.numOfDays = numOfDays;
        this.nDays = days;
    }

    private int numOfDays;
    private String nDays;

    public int getNumOfDays() {
        return numOfDays;
    }

    public String getnDays() {
        return nDays;
    }

    public static TimePeriod getNumberOfDays(int days) {
        for (TimePeriod attribute : values()) {
            if (attribute.getNumOfDays() == days) {
                return attribute;
            }
        }
        return null;
    }



}
