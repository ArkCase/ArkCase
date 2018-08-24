package com.armedia.acm.plugins.alfrescorma.model;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

    String CASE_STATUS_CHANGED_EVENT = "com.armedia.acm.casefile.status.changed";

    String CASE_CLOSE_INTEGRATION_KEY = "alfresco_rma_declare_records_on_case_close";
    String COMPLAINT_CLOSE_INTEGRATION_KEY = "alfresco_rma_declare_records_on_complaint_close";
    String COMPLAINT_FOLDER_INTEGRATION_KEY = "alfresco_rma_create_record_folder_on_complaint_create";
    String FILE_INTEGRATION_KEY = "alfresco_rma_declare_record_folder_on_file_upload";
    String FILE_DECLARE_REQUEST_INTEGRATION_KEY = "alfresco_rma_declare_file_record_on_declare_request";
    String FOLDER_DECLARE_REQUEST_INTEGRATION_KEY = "alfresco_rma_declare_folder_record_on_declare_request";

    String RMA_PLUGIN = "RMA";

}
