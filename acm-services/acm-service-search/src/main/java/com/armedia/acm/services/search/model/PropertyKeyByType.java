package com.armedia.acm.services.search.model;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 07.01.2015.
 */

public enum PropertyKeyByType
{

    COMPLAINT_CREATE_DATA_FILTER("acm.complaint.createDateFilter", "COMPLAINT"),
    COMPLAINT_MODIFY_DATA_FILTER(
            "acm.complaint.modifyDateFilter",
            "COMPLAINT"),
    COMPLAINT_DUE_DATA_FILTER(
            "acm.complaint.dueDateFilter",
            "COMPLAINT"),
    COMPLAINT_INCIDENT_DATA_FILTER(
            "acm.complaint.incidentDateFilter",
            "COMPLAINT"),
    COMPLAINT_PRIORITY_FILTER("acm.complaint.priorityFilter", "COMPLAINT"),
    COMPLAINT_STATUS_FILTER(
            "acm.complaint.complaintStatusFilter",
            "COMPLAINT"),
    COMPLAINT_INCIDENT_TYPE_FILTER(
            "acm.complaint.incidentTypeFilter",
            "COMPLAINT"),
    COMPLAINT_CREATE_USER_FILTER(
            "acm.complaint.createUserFilter",
            "COMPLAINT"),
    COMPLAINT_MODIFY_USER_FILTER(
            "acm.complaint.modifyUserFilter",
            "COMPLAINT"),
    COMPLAINT_ASSIGNEE_FULL_NAME_FILTER(
            "acm.complaint.assigneeFullNameFilter",
            "COMPLAINT"),

    CASE_FILE_CREATE_DATA_FILTER("acm.case.createDateFilter", "CASE_FILE"),
    CASE_FILE_MODIFY_DATA_FILTER(
            "acm.case.modifyDateFilter",
            "CASE_FILE"),
    CASE_FILE_DUE_DATA_FILTER("acm.case.dueDateFilter", "CASE_FILE"),
    CASE_FILE_INCIDENT_DATA_FILTER(
            "acm.caset.incidentDateFilter",
            "CASE_FILE"),
    CASE_FILE_PRIORITY_FILTER(
            "acm.case.priorityFilter",
            "CASE_FILE"),
    CASE_FILE_STATUS_FILTER("acm.case.caseStatusFilter", "CASE_FILE"),
    CASE_FILE_INCIDENT_TYPE_FILTER(
            "acm.case.caseTypeFilter",
            "CASE_FILE"),
    CASE_FILE_CREATE_USER_FILTER(
            "acm.case.createUserFilter",
            "CASE_FILE"),
    CASE_FILE_MODIFY_USER_FILTER(
            "acm.case.modifyUserFilter",
            "CASE_FILE"),
    CASE_FILE_ASSIGNEE_FULL_NAME_FILTER(
            "acm.case.assigneeFullNameFilter",
            "CASE_FILE"),

    DOCUMENT_CREATE_DATA_FILTER("acm.document.createDateFilter", "DOCUMENT"),
    DOCUMENT_MODIFY_DATA_FILTER(
            "acm.document.modifyDateFilter",
            "DOCUMENT"),
    DOCUMENT_CREATE_USER_FILTER("acm.document.createUserFilter", "DOCUMENT"),
    DOCUMENT_MODIFY_USER_FILTER(
            "acm.document.modifyUserFilter",
            "DOCUMENT"),
    DOCUMENT_FILE_STATUS_FILTER(
            "acm.document.fileStatusFilter",
            "DOCUMENT"),
    DOCUMENT_PARENT_TYPE_FILTER("acm.document.parentTypeFilter", "DOCUMENT"),

    NONE("NONE", "NONE");

    private String type;
    private String propertyKey;

    PropertyKeyByType(String propertyKey, String type)
    {
        this.propertyKey = propertyKey;
        this.type = type;
    }

    public static List<PropertyKeyByType> getPropertyKeyByType(String type)
    {
        List<PropertyKeyByType> returnList = new ArrayList<>();
        for (PropertyKeyByType attribute : values())
        {
            if (attribute.type.equals(type))
            {
                returnList.add(attribute);
            }
        }
        if (returnList.size() > 0)
        {
            return returnList;
        }
        else
        {
            returnList.add(PropertyKeyByType.NONE);
            return returnList;
        }
    }

    public static List<PropertyKeyByType> getAllPopertyKeyByTypeValues()
    {
        List<PropertyKeyByType> returnList = new ArrayList<>();
        for (PropertyKeyByType attribute : values())
        {
            if ("NONE".equals(attribute.getType()))
            {
                continue;
            }
            returnList.add(attribute);
        }
        return returnList;
    }

    public String getType()
    {
        return type;
    }

    public String getPropertyKey()
    {
        return propertyKey;
    }
}
