package com.armedia.acm.plugins.task.model;

/**
 * Created by marjan.stefanoski on 8/29/2014.
 */
public enum NumberOfDays {
    ONE_DAY(1,"one day"),
    SEVEN_DAYS(7,"seven days"),
    THIRTY_DAYS(30,"thirty days"),
    ZERO(0,"zerro days");

    NumberOfDays(int numOfDays, String days) {
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

    public static NumberOfDays getNumberOfDays(int days) {
        for (NumberOfDays attribute : values()) {
            if (attribute.getNumOfDays() == days) {
                return attribute;
            }
        }
        return NumberOfDays.ZERO;
    }



}
