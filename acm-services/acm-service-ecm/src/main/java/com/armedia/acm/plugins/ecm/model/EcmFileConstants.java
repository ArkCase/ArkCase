package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
 * Created by armdev on 3/11/15.
 */
public interface EcmFileConstants
{
    String MULE_ENDPOINT_CREATE_FOLDER = "vm://createFolder.in";

    String MULE_ENDPOINT_COPY_FILE = "vm://copyFile.in";

    String MULE_ENDPOINT_MOVE_FILE = "vm://moveFile.in";

    String USER_ACTION_COPY_FILE = "COPY";

    String USER_ACTION_UPLOAD_FILE = "UPLOAD";

    String USER_ACTION_MOVE_FILE = "MOVE";

    String USER_ACTION_DELETE_FILE = "DELETE";

    String USER_ACTION_RENAME_FILE = "RENAME";

    String USER_ACTION_REPLACE_FILE = "REPLACE";

    String USER_ACTION_SET_FILE_ACTIVE_VERSION = "SET ACTIVE VERSION TO";

    String USER_ACTION_UPDATE_FILE = "UPDATE FILE";

    String USER_ACTION_DOWNLOAD_FILE = " DOWNLOAD FILE";

    String USER_ACTION_DOWNLOAD_FILE_AS_INPUTSTREAM = " DOWNLOAD FILE AS INPUTSTREAM";

    String FOLDER_LIST_DEFAULT_SORT_PARAM = "name_lcs";

    String SUCCESS_DELETE_MSG = "File deleted successfully: ";

    String SUCCESS_TEMPORARY_DELETE_MSG = "Temporary file deleted successfully: ";

    String FILE_NOT_FOUND_DB = "File is not found: ";

    String SUCCESS_CHANGE_STATUS_TO_RECORD_MSG = "DECLARED AS RECORD";

    String FIND_CONTAINER_QUERY_BY_FOLDER_ID = "SELECT e FROM AcmContainer e WHERE e.folder.id = :folderId";

    String FIND_CONTAINERS_QUERY_BY_OBJECT_TYPE = "SELECT e FROM AcmContainer e WHERE e.containerObjectType = :objectType";

    String FIND_CONTAINER_QUERY = "SELECT e FROM AcmContainer e WHERE e.containerObjectId = :objectId AND e.containerObjectType = :objectType";

    String FIND_CMIS_CONTAINER_QUERY = "SELECT e FROM AcmContainer e WHERE e.containerObjectId = :objectId AND e.containerObjectType = :objectType AND e.cmisRepositoryId = :cmisRepositoryId";

    String FIND_CONTAINERS_QUERY = "SELECT e FROM AcmContainer e WHERE e.containerObjectId IN :objectIds AND e.containerObjectType = :objectType";

    String CONTAINER_FOLDER_NAME = "ROOT";

    String CATEGORY_ALL = "all";

    /**
     * Regex for characters that are not allowed while creating folder name
     */
    String INVALID_CHARACTERS_IN_FOLDER_NAME_REGEX = "[/?<>\\\\:*|\"^]";

    String INVALID_CHARACTERS_IN_FOLDER_NAME_REPLACEMENT = "_";

    /**
     * These four properties we need for searching them in mimeType property for the file
     */
    String MIME_TYPE_XML = "text/xml";
    String MIME_TYPE_FREVVO_URL = "www.frevvo.com";
    String MIME_TYPE_PNG = "image/png";
    String MIME_TYPE_FREVVO_SIGNATURE_KEY = "frevvo-signature-image=true";

    String OBJECT_FILE_TYPE = "FILE";

    String OBJECT_FOLDER_TYPE = "FOLDER";

    String OBJECT_CONTAINER_TYPE = "CONTAINER";

    String EVENT_TYPE_TAG_UPDATE = "com.armedia.acm.tag.updated";
    String EVENT_TYPE_ACTIVE_VERSION_SET = "com.armedia.acm.file.version.set";
    String EVENT_TYPE_FILE_EMAILED = "com.armedia.acm.ecm.file.emailed";

    String CMIS_OBJECT_ID = "cmisObjectId";
    String SRC_FOLDER_ID = "srcFolderId";
    String DST_FOLDER_ID = "dstFolderId";
    String ECM_FILE_ID = "ecmFileId";
    String NEW_FILE_NAME = "newFileName";
    String FILE_NAME = "fileName";
    String INPUT_STREAM = "inputStream";
    String RECORD = "RECORD";
    String CMIS_DOCUMENT_ID = "cmisDocumentId";

    String IP_ADDRESS_ATTRIBUTE = "acm_ip_address";

    String FILE = "file";

    String COPY_FILE_EXCEPTION_INBOUND_PROPERTY = "copyFileException";

    String ACTIVE = "ACTIVE";

    String FIND_CONTAINER_BY_CALENDAR_FOLDER_QUERY = "SELECT c FROM AcmContainer c WHERE c.calendarFolderId = :folderId";
    String FILE_MIME_TYPE = "fileMimeType";

    // Multiple CMIS configurations constants
    String CONFIGURATION_REFERENCE = "configRef";
    String VERSIONING_STATE = "versioningState";
    String DEFAULT_CMIS_REPOSITORY_ID = "alfresco";
    String ALL_VERSIONS = "allVersions";
    String CMIS_REPOSITORY_ID = "cmisRepositoryId";

    String ECM_SYNC_NODE_TYPE_FOLDER = "folder";
    String ECM_SYNC_NODE_TYPE_DOCUMENT = "document";
}
