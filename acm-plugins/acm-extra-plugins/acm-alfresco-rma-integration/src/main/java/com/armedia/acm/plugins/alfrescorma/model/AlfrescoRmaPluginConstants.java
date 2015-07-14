package com.armedia.acm.plugins.alfrescorma.model;

/**
 * Created by armdev on 3/27/15.
 */
public interface AlfrescoRmaPluginConstants
{
    String RMA_MODULE_VERSION_KEY = "alfresco.rma.rma-module-version";

    String CATEGORY_FOLDER_PROPERTY_KEY_PREFIX = "rma.categoryFolder.";

    String PROPERTY_ORIGINATOR_ORG = "rma.default.originator.org";

    String DEFAULT_ORIGINATOR_ORG = "Armedia LLC";

    String RECORD_MULE_ENDPOINT = "jms://rmaRecord.in";
    String FOLDER_MULE_ENDPOINT = "jms://rmaFolder.in";

    String CASE_CLOSED_EVENT = "com.armedia.acm.casefile.event.closed";

    String CASE_CLOSE_INTEGRATION_KEY = "alfresco.rma.declare-records-on-case-close";
    String COMPLAINT_CLOSE_INTEGRATION_KEY = "alfresco.rma.declare-records-on-complaint-close";
    String COMPLAINT_FOLDER_INTEGRATION_KEY = "alfresco.rma.create-record-folder-on-complaint-create";
    String FILE_INTEGRATION_KEY = "alfresco.rma.declare-record-folder-on-file-upload";
}
