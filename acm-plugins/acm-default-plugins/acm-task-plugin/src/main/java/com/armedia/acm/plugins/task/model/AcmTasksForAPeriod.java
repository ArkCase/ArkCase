package com.armedia.acm.plugins.task.model;

/**
 * Created by marjan.stefanoski on 8/29/2014.
 */
public enum AcmTasksForAPeriod {

    ALL("all"),
    PAST_DUE("pastDue"),
    DUE_TOMORROW("dueTomorrow"),
    DUE_IN_7_DAYS("dueInAWeek"),
    DUE_IN_30_DAYS("dueInAMonth"),
    NONE("none");

    private String period;

    private AcmTasksForAPeriod(String period) {
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }

    public static AcmTasksForAPeriod getTasksForPeriodByText(String text) {
        for (AcmTasksForAPeriod attribute : values()) {
            if (attribute.period.equals(text)) {
                return attribute;
            }
        }
        return AcmTasksForAPeriod.NONE;
    }
}

