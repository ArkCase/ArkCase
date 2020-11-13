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
public class ArkCaseCMISConstants
{
    public static final String CMIS_API_URL = "cmisApiURL";
    // This is endpoint for Apache cmis component
    public static final String ARKCASE_CMIS_COMPONENT = "arkcase-cmis://";
    public static final String CAMEL_CMIS_DEFAULT_REPO_ID = "alfresco";

    public static final String CMIS_OBJECT_ID = "cmisObjectId";
    public static final String CMIS_DOC_ID = "cmisDocumentId";
    public static final String CMIS_FOLDER_ID = "cmisFolderId";
    public static final String CMIS_REPOSITORY_ID = "cmisRepositoryId";

    public static final String VERSIONING_STATE = "versioningState";
    public static final String MIME_TYPE = "mimeType";
    public static final String CHECKIN_COMMENT = "checkinComment";
    public static final String INPUT_STREAM = "inputStream";
    public static final String NEW_FILE_NAME = "newFileName";
    public static final String NEW_FOLDER_NAME = "newFolderName";

    public static final String ACM_FOLDER_ID = "acmFolderId";
    public static final String DESTINATION_FOLDER_ID = "dstFolderId";
    public static final String PARENT_FOLDER_ID = "acmFolderId";
    public static final String SOURCE_FOLDER_ID = "srcFolderId";
}
