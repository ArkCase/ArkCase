package com.armedia.acm.plugins.complaint.model;

/**
 * Created by marjan.stefanoski on 9/8/2014.
 */
public enum TimePeriod {

    ONE_YEAR(365,"one year"),
    SEVEN_DAYS(7,"seven days"),
    TRHEE_MONTHS(90,"three months"),
    SIX_MONTHS(182,"six months"),
    THIRTY_DAYS(30,"thirty days"),
    ZERO(0,"zerro days");

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
        return TimePeriod.ZERO;
    }



}
