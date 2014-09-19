package com.armedia.acm.plugins.complaint.model;

/**
 * Created by marjan.stefanoski on 9/8/2014.
 */
public enum ComplaintsByTimePeriod {


    LAST_WEEK("lastWeek"),
    LAST_MONTH("lastMonth"),
    LAST_THREE_MONTH("lastThreeMonths"),
    LAST_SIX_MONTH("lastSixMonths"),
    LAST_YEAR("lastYear"),
    NONE("none");

    private String period;

    private ComplaintsByTimePeriod(String period) {
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }

    public static ComplaintsByTimePeriod getTimePeriod(String text) {
        for (ComplaintsByTimePeriod attribute : values()) {
            if (attribute.period.equals(text)) {
                return attribute;
            }
        }
        return ComplaintsByTimePeriod.NONE;
    }
}
