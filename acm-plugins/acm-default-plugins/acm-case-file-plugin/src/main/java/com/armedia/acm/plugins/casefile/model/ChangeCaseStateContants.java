package com.armedia.acm.plugins.casefile.model;

public interface ChangeCaseStateContants
{
    String MIME_TYPE_PDF = "application/pdf";
    String NEW_FILE = "NEW_FILE";
    String FILE_ID = "FILE_ID";
    String FILE_VERSION = "FILE_VERSION";

    String CHANGE_CASE_STATUS_STYLESHEET = System.getProperty("user.home")
            + "/.arkcase/acm/pdf-stylesheets/change-case-file-state-document.xsl";
    String CHANGE_CASE_STATUS_DOCUMENT = "CHANGE_CASE_STATUS";
    String CHANGE_CASE_STATUS_FILENAMEFORMAT = "Change Case Status.pdf";
    String CHANGE_CASE_STATUS = "CHANGE_CASE_STATUS";
}
