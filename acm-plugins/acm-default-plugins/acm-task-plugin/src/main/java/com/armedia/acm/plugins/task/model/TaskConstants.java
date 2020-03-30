package com.armedia.acm.plugins.task.model;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

/**
 * Created by armdev on 2/11/15.
 */
public interface TaskConstants
{
    String OBJECT_TYPE = "TASK";
    String SYSTEM_OBJECT_TYPE = "BUSINESS_PROCESS";
    String STATE_ACTIVE = "ACTIVE";
    String STATE_CLOSED = "CLOSED";
    String STATE_TERMINATED = "TERMINATED";
    String STATE_UNCLAIMED = "UNCLAIMED";
    // EDTRM-491: Use DELETE instead of DELETED, since the search filters for active objects filter out DELETE.
    // So this way, deleted tasks won't show up when we search for active tasks.
    String STATE_DELETE = "DELETE";
    String STATE_DELETED = "DELETED";
    Integer DEFAULT_PRIORITY = 50;
    String DEFAULT_PRIORITY_WORD = "Medium";

    String VARIABLE_NAME_OBJECT_TYPE = "OBJECT_TYPE";
    String VARIABLE_NAME_OBJECT_ID = "OBJECT_ID";
    String VARIABLE_NAME_OBJECT_NAME = "OBJECT_NAME";
    String VARIABLE_NAME_START_DATE = "START_DATE";
    String VARIABLE_NAME_PERCENT_COMPLETE = "PERCENT_COMPLETE";
    String VARIABLE_NAME_DETAILS = "DETAILS";
    String VARIABLE_NAME_PARENT_OBJECT_ID = "PARENT_OBJECT_ID";
    String VARIABLE_NAME_PARENT_OBJECT_TYPE = "PARENT_OBJECT_TYPE";
    String VARIABLE_NAME_PARENT_OBJECT_NAME = "PARENT_OBJECT_NAME";
    String VARIABLE_NAME_PARENT_OBJECT_TITLE = "PARENT_OBJECT_TITLE";
    String VARIABLE_NAME_REWORK_INSTRUCTIONS = "REWORK_INSTRUCTIONS";
    String VARIABLE_NAME_OUTCOME = "outcome";
    String VARIABLE_NAME_REQUEST_ID = "REQUEST_ID";
    String VARIABLE_NAME_REQUEST_TYPE = "REQUEST_TYPE";
    String VARIABLE_NAME_PDF_RENDITION_ID = "pdfRenditionId";
    String VARIABLE_NAME_XML_RENDITION_ID = "formXmlId";
    String VARIABLE_NAME_REVIEWERS = "reviewers";
    String VARIABLE_NAME_TASK_NAME = "taskName";
    String VARIABLE_NAME_DOC_AUTHOR = "documentAuthor";
    String VARIABLE_NAME_PENDING_STATUS = "PENDING_STATUS";

    String IDENTITY_LINK_TYPE_CANDIDATE = "candidate";
    String VARIABLE_NAME_NEXT_ASSIGNEE = "NEXT_ASSIGNEE";

    String VARIABLE_NAME_LEGACY_SYSTEM_ID = "LEGACY_SYSTEM_ID";

    String VARIABLE_NAME_IS_BUCKSLIP_WORKFLOW = "isBuckslipWorkflow";

    String VARIABLE_NAME_BUCKSLIP_FUTURE_APPROVERS = "futureApprovers";
    String VARIABLE_NAME_PAST_APPROVERS = "pastApprovers";
    String VARIABLE_NAME_PAST_TASKS = "pastTasks";

    String VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS = "futureTasks";
    String VARIABLE_NAME_NON_CONCUR_ENDS_APPROVALS = "nonConcurEndsApprovals";
    String INITIATE_TASK_NAME = "rtInitiate";
    String VARIABLE_NAME_TASK_DUE_DATE_EXPRESSION = "taskDueDateExpression";
    String VARIABLE_NAME_TASK_TYPE = "TASK_TYPE";

}
