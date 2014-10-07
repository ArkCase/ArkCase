package com.armedia.acm.plugins.casefile.model;

/**
 * Created by marjan.stefanoski on 9/8/2014.
 */
public enum CasesByStatusAndTimePeriod {

    ALL("all"),
    LAST_WEEK("lastWeek"),
    LAST_MONTH("lastMonth"),
    LAST_YEAR("lastYear"),
    NONE("none");

    private String period;

    private CasesByStatusAndTimePeriod(String period) {
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }

    public static CasesByStatusAndTimePeriod getTimePeriod(String text) {
        for (CasesByStatusAndTimePeriod attribute : values()) {
            if (attribute.period.equals(text)) {
                return attribute;
            }
        }
        return CasesByStatusAndTimePeriod.NONE;
    }
}
