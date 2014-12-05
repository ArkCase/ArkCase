package com.armedia.acm.services.search.model;

/**
 * Created by marjan.stefanoski on 9/8/2014.
 */
public enum TimePeriodForSearch {

    ALL("all"),
    LAST_WEEK("lastWeek"),
    LAST_MONTH("lastMonth"),
    LAST_YEAR("lastYear"),
    NONE("none");

    private String period;

    private TimePeriodForSearch(String period) {
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }

    public static TimePeriodForSearch getTimePeriod(String text) {
        for (TimePeriodForSearch attribute : values()) {
            if (attribute.period.equals(text)) {
                return attribute;
            }
        }
        return TimePeriodForSearch.NONE;
    }
}
