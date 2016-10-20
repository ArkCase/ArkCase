package com.armedia.acm.plugins.alfrescorma.model;

/**
 * Created by armdev on 3/27/15.
 */
public interface AlfrescoRmaPluginConstants
{
    String CATEGORY_FOLDER_PROPERTY_KEY_PREFIX = "rma_categoryFolder_";

    String PROPERTY_ORIGINATOR_ORG = "rma_default_originator_org";

    String DEFAULT_ORIGINATOR_ORG = "Armedia LLC";

    String RECORD_MULE_ENDPOINT = "jms://rmaRecord.in";
    String FOLDER_MULE_ENDPOINT = "jms://rmaFolder.in";

    String CASE_CLOSED_EVENT = "com.armedia.acm.casefile.closed";

    String CASE_CLOSE_INTEGRATION_KEY = "alfresco_rma_declare_records_on_case_close";
    String COMPLAINT_CLOSE_INTEGRATION_KEY = "alfresco_rma_declare_records_on_complaint_close";
    String COMPLAINT_FOLDER_INTEGRATION_KEY = "alfresco_rma_create_record_folder_on_complaint_create";
    String FILE_INTEGRATION_KEY = "alfresco_rma_declare_record_folder_on_file_upload";
    String FILE_DECLARE_REQUEST_INTEGRATION_KEY = "alfresco_rma_declare_file_record_on_declare_request";
    String FOLDER_DECLARE_REQUEST_INTEGRATION_KEY = "alfresco_rma_declare_folder_record_on_declare_request";

}
