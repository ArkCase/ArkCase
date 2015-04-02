package com.armedia.acm.plugins.ecm.model;

/**
 * Created by armdev on 3/11/15.
 */
public interface EcmFileConstants
{
    String MULE_ENDPOINT_CREATE_FOLDER = "vm://createFolder.in";

    String FOLDER_LIST_DEFAULT_SORT_PARAM = "name_lcs";

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

}
