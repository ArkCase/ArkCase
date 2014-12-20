package com.armedia.acm.services.search.model.facet;

import java.util.List;

/**
 * Created by marjan.stefanoski on 19.12.2014.
 */
public class FacetCaseDto implements IFacetDto{

    private String createDateStart;
    private String createDateEnd;
    private String modifyDateStart;
    private String modifyDateEnd;
    private String dueDateStart;
    private String dueDateEnd;
    private String incidentDateStart;
    private String incidentDateEnd;
    private List<String> priorityList;
    private List<String> caseStatusList;
    private List<String> caseTypeList;
    private List<String> createUserList;
    private List<String> modifyUserList;
    private List<String> assigneeFullNameList;

    public String getCreateDateStart() {
        return createDateStart;
    }

    public void setCreateDateStart(String createDateStart) {
        this.createDateStart = createDateStart;
    }

    public String getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(String createDateEnd) {
        this.createDateEnd = createDateEnd;
    }

    public String getModifyDateStart() {
        return modifyDateStart;
    }

    public void setModifyDateStart(String modifyDateStart) {
        this.modifyDateStart = modifyDateStart;
    }

    public String getModifyDateEnd() {
        return modifyDateEnd;
    }

    public void setModifyDateEnd(String modifyDateEnd) {
        this.modifyDateEnd = modifyDateEnd;
    }

    public String getDueDateStart() {
        return dueDateStart;
    }

    public void setDueDateStart(String dueDateStart) {
        this.dueDateStart = dueDateStart;
    }

    public String getDueDateEnd() {
        return dueDateEnd;
    }

    public void setDueDateEnd(String dueDateEnd) {
        this.dueDateEnd = dueDateEnd;
    }

    public String getIncidentDateStart() {
        return incidentDateStart;
    }

    public void setIncidentDateStart(String incidentDateStart) {
        this.incidentDateStart = incidentDateStart;
    }

    public String getIncidentDateEnd() {
        return incidentDateEnd;
    }

    public void setIncidentDateEnd(String incidentDateEnd) {
        this.incidentDateEnd = incidentDateEnd;
    }

    public List<String> getPriorityList() {
        return priorityList;
    }

    public void setPriorityList(List<String> priorityList) {
        this.priorityList = priorityList;
    }

    public List<String> getCaseStatusList() {
        return caseStatusList;
    }

    public void setCaseStatusList(List<String> caseStatusList) {
        this.caseStatusList = caseStatusList;
    }

    public List<String> getCaseTypeList() {
        return caseTypeList;
    }

    public void setCaseTypeList(List<String> caseTypeList) {
        this.caseTypeList = caseTypeList;
    }

    public List<String> getCreateUserList() {
        return createUserList;
    }

    public void setCreateUserList(List<String> createUserList) {
        this.createUserList = createUserList;
    }

    public List<String> getModifyUserList() {
        return modifyUserList;
    }

    public void setModifyUserList(List<String> modifyUserList) {
        this.modifyUserList = modifyUserList;
    }

    public List<String> getAssigneeFullNameList() {
        return assigneeFullNameList;
    }

    public void setAssigneeFullNameList(List<String> assigneeFullNameList) {
        this.assigneeFullNameList = assigneeFullNameList;
    }
}
