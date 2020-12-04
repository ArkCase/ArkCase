package com.armedia.acm.camelcontext.arkcase.cmis;

/*-
 * #%L
 * acm-camel-context-manager
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Sep, 2019
 */
public interface ArkCaseCMISConstants
{
    String CMIS_API_URL = "cmisApiURL";
    // This is endpoint for Apache cmis component
    String ARKCASE_CMIS_COMPONENT = "arkcase-cmis://";
    String DEFAULT_CMIS_REPOSITORY_ID = "alfresco";

    String CMIS_OBJECT_ID = "cmisObjectId";
    String CMIS_DOCUMENT_ID = "cmisDocumentId";
    String CMIS_FOLDER_ID = "cmisFolderId";
    String CMIS_REPOSITORY_ID = "cmisRepositoryId";
    String ALL_VERSIONS = "allVersions";

    String VERSIONING_STATE = "versioningState";
    String MIME_TYPE = "mimeType";
    String CHECKIN_COMMENT = "checkinComment";
    String INPUT_STREAM = "inputStream";
    String NEW_FILE_NAME = "newFileName";
    String NEW_FOLDER_NAME = "newFolderName";

    String ACM_FOLDER_ID = "acmFolderId";
    String DESTINATION_FOLDER_ID = "dstFolderId";
    String PARENT_FOLDER_ID = "parentFolderId";
    String SOURCE_FOLDER_ID = "srcFolderId";
}
