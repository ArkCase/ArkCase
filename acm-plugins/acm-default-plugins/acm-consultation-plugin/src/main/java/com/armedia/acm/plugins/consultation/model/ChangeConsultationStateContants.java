package com.armedia.acm.plugins.consultation.model;

public interface ChangeConsultationStateContants
{
    String MIME_TYPE_PDF = "application/pdf";
    String NEW_FILE = "NEW_FILE";
    String FILE_ID = "FILE_ID";
    String FILE_VERSION = "FILE_VERSION";

    // TODO : Change with consultation document
    String CHANGE_CONSULTATION_STATUS_STYLESHEET = System.getProperty("user.home")
            + "/.arkcase/acm/pdf-stylesheets/change-case-file-state-document.xsl";
    String CHANGE_CONSULTATION_STATUS_DOCUMENT = "CHANGE_CONSULTATION";
    String CHANGE_CONSULTATION_STATUS_FILENAMEFORMAT = "Change Consultation Status.pdf";
    String CHANGE_CONSULTATION_STATUS = "CHANGE_CONSULTATION_STATUS";
}
