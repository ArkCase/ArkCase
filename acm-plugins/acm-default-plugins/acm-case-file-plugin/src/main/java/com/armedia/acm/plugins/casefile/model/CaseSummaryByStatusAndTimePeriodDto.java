package com.armedia.acm.plugins.casefile.model;

import java.util.List;

/**
 * Created by marjan.stefanoski on 10/9/2014.
 */
public class CaseSummaryByStatusAndTimePeriodDto {
    private String timePeriod;
    private List<CaseByStatusDto> caseByStatusDtoList;

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public List<CaseByStatusDto> getCaseByStatusDtoList() {
        return caseByStatusDtoList;
    }

    public void setCaseByStatusDtoList(List<CaseByStatusDto> caseByStatusDtoList) {
        this.caseByStatusDtoList = caseByStatusDtoList;
    }
}
