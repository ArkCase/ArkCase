package com.armedia.acm.plugins.task.model;

/**
 * Created by armdev on 2/11/15.
 */
public interface TaskConstants
{
    String OBJECT_TYPE = "TASK";
    String STATE_ACTIVE = "ACTIVE";
    String STATE_CLOSED = "CLOSED";

    // EDTRM-491: Use DELETE instead of DELETED, since the search filters for active objects filter out DELETE.
    // So this way, deleted tasks won't show up when we search for active tasks.
    String STATE_DELETED = "DELETE";
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
    String VARIABLE_NAME_REWORK_INSTRUCTIONS = "REWORK_INSTRUCTIONS";
    String VARIABLE_NAME_OUTCOME = "outcome";
    String VARIABLE_NAME_REQUEST_ID = "REQUEST_ID";
    String VARIABLE_NAME_REQUEST_TYPE = "REQUEST_TYPE";
    String VARIABLE_NAME_PDF_RENDITION_ID = "pdfRenditionId";
    String VARIABLE_NAME_XML_RENDITION_ID = "formXmlId";
    String VARIABLE_NAME_REVIEWERS = "reviewers";
    String VARIABLE_NAME_TASK_NAME = "taskName";
    String VARIABLE_NAME_DOC_AUTHOR = "documentAuthor";


    String IDENTITY_LINK_TYPE_CANDIDATE = "candidate";
}
