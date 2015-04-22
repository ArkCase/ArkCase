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

    String FOLDER_LIST_DEFAULT_SORT_PARAM = "name_lcs";

    String SUCCESS_DELETE_MSG = "File deleted successfully: ";

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
	 * These two properties we need for searching them in mimeType property for the file
	 */
	String MIME_TYPE_XML = "text/xml";
	String MIME_TYPE_FREVVO_URL = "www.frevvo.com";

    String OBJECT_FILE_TYPE = "FILE";

    String EVENT_TYPE_TAG_UPDATE = "com.armedia.acm.tag.updated";


    String CMIS_OBJECT_ID = "cmisObjectId";
    String SRC_FOLDER_ID = "srcFolderId";
    String DST_FOLDER_ID = "dstFolderId";
    String ECM_FILE_ID = "ecmFileId";
    String NEW_FILE_NAME = "newFileName";
    String FILE_NAME = "fileName";
    String INPUT_STREAM = "inputStream";

    String IP_ADDRESS_ATTRIBUTE = "acm_ip_address";

}
