/**
 *
 */
package com.armedia.acm.plugins.consultation.model;

public interface ConsultationConstants
{
    String OBJECT_TYPE = "CONSULTATION";

    String ACTIVE_CONSULTATION_FORM_KEY = "active.consultation.form";

    String EVENT_TYPE_CREATED = "com.armedia.acm.consultation.created";

    String EVENT_TYPE_UPDATED = "com.armedia.acm.consultation.updated";

    String EVENT_TYPE_VIEWED = "com.armedia.acm.consultation.viewed";

    String OWNING_GROUP = "owning group";

    String ASSIGNEE = "assignee";

    String PARENT_OBJECT_TYPE = "PARENT_OBJECT_TYPE";

    String PARENT_OBJECT_ID = "PARENT_OBJECT_ID";

    String MIME_TYPE_PDF = "application/pdf";
    String NEW_FILE = "NEW_FILE";
    String FILE_ID = "FILE_ID";
    String FILE_VERSION = "FILE_VERSION";

    // TODO : Change with consultation doc
    String CONSULTATION_STYLESHEET = System.getProperty("user.home") + "/.arkcase/acm/pdf-stylesheets/casefile-document.xsl";
    String CONSULTATION_DOCUMENT = "CONSULTATION";
    String CONSULTATION_NAME_FORMAT = "Consultation.pdf";

    String NEXT_QUEUE_ACTION_COMPLETE = "Complete";
    String NEXT_QUEUE_ACTION_NEXT = "Next";
}
