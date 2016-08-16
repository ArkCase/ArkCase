package com.armedia.acm.plugins.ecm.model;

/**
 * Created by armdev on 3/11/15.
 */
public interface EcmFileConstants
{
    String MULE_ENDPOINT_CREATE_FOLDER = "vm://createFolder.in";

    String MULE_ENDPOINT_COPY_FILE = "vm://copyFile.in";

    String MULE_ENDPOINT_RENAME_FILE = "vm://renameFile.in";

    String MULE_ENDPOINT_REPLACE_FILE = "vm://updateFile.in";

    String MULE_ENDPOINT_DELETE_FILE = "vm://deleteFile.in";

    String MULE_ENDPOINT_MOVE_FILE = "vm://moveFile.in";

    String USER_ACTION_COPY_FILE = "COPY";

    String USER_ACTION_UPLOAD_FILE = "UPLOAD";

    String USER_ACTION_MOVE_FILE = "MOVE";

    String USER_ACTION_DELETE_FILE = "DELETE";

    String USER_ACTION_RENAME_FILE = "RENAME";

    String USER_ACTION_REPLACE_FILE = "REPLACE";

    String USER_ACTION_SET_FILE_ACTIVE_VERSION = "SET ACTIVE VERSION TO";

    String USER_ACTION_DOWNLOAD_FILE_AS_INPUTSTREAM = " DOWNLOAD FILE AS INPUTSTREAM";


    String FOLDER_LIST_DEFAULT_SORT_PARAM = "name_lcs";

    String SUCCESS_DELETE_MSG = "File deleted successfully: ";

    String SUCCESS_CHANGE_STATUS_TO_RECORD_MSG = "DECLARED AS RECORD";


    String FIND_CONTAINER_QUERY =
            "SELECT e FROM AcmContainer e WHERE e.containerObjectId = :objectId AND e.containerObjectType = :objectType";

    String PROPERTY_KEY_DEFAULT_FOLDER_BASE_PATH = "ecm.defaultBasePath";
    String PROPERTY_PREFIX_FOLDER_PATH_BY_TYPE = "ecm.defaultPath.";

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

    String IP_ADDRESS_ATTRIBUTE = "acm_ip_address";

    String FILE = "file";

    String COPY_FILE_EXCEPTION_INBOUND_PROPERTY = "copyFileException";

    String ACTIVE = "ACTIVE";

    String FIND_CONTAINER_BY_CALENDAR_FOLDER_QUERY = "SELECT c FROM AcmContainer c WHERE c.calendarFolderId = :folderId";
    String FILE_MIME_TYPE = "fileMimeType";
}
