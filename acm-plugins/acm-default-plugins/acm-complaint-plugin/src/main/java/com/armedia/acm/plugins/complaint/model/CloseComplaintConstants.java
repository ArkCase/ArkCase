package com.armedia.acm.plugins.complaint.model;

public interface CloseComplaintConstants
{

    String MIME_TYPE_PDF = "application/pdf";
    String NEW_FILE = "NEW_FILE";
    String FILE_ID = "FILE_ID";
    String FILE_VERSION = "FILE_VERSION";

    String CLOSE_COMPLAINT_STYLESHEET = System.getProperty("user.home") + "/.arkcase/acm/pdf-stylesheets/close-complaint-document.xsl";
    String CLOSE_COMPLAINT_DOCUMENT = "CLOSE_COMPLAINT";
    String CLOSE_COMPLAINT_FILENAMEFORMAT = "Close Complaint.pdf";
    String CLOSE_COMPLAINT_REQUEST = "CLOSE_COMPLAINT_REQUEST";
}
