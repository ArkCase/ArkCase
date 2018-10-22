/**
 *
 */
package gov.foia.model;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 17, 2016
 */
public interface FOIAConstants
{

    public String NEW_REQUEST_TYPE = "New Request";
    public String APPEAL_REQUEST_TYPE = "Appeal";

    public String REQ = "REQ";
    public String ACK = "ACK";
    public String REQ_DELETE = "REQ_DELETE";
    public String DENIAL = "DENIAL";
    public String REQ_EXTENSION = "REQ_EXTENSION";

    public String MIME_TYPE_PDF = "application/pdf";
    public String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public String NEW_FILE = "NEW_FILE";
    public String FILE_ID = "FILE_ID";

    public String EMAIL_HEADER_SUBJECT = "FOIA Extension Notification";
    public String EMAIL_HEADER_ATTACHMENT = "Hello,";
    public String EMAIL_BODY_ATTACHMENT = "Please find attached the documents sent to you from ArkCase";
    public String EMAIL_FOOTER_ATTACHMENT = "Powered by ArkCase, Enterprise Case Management platform, http://www.arkcase.com";

    public String EMAIL_RELEASE_SUBJECT = "FOIA Request Complete";
    public String EMAIL_RELEASE_BODY = "Your %s with number %s has been released and the document(s) is ready for download on the portal. Please go to the check status page at this <a href=\"%s\">link</a>.";
    public String PORTAL_REQUEST_STATUS_RELATIVE_URL = "/../foia/portal/requestStatus?requestNumber=%s";
    public String NEW_REQUEST_TITLE = "NEW REQUEST";

    public static final String FOIA_PIPELINE_EXTENSION_PROPERTY_KEY = "foia_request_extension";
}
