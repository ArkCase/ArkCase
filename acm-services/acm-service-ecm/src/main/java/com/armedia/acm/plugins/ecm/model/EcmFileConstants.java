package com.armedia.acm.plugins.ecm.model;

/**
 * Created by armdev on 3/11/15.
 */
public interface EcmFileConstants
{
    String MULE_ENDPOINT_CREATE_FOLDER = "vm://createFolder.in";
    String MULE_ENDPOINT_LIST_FOLDER_CONTENTS = "vm://listFolderContents.in";

    String FOLDER_LIST_DEFAULT_SORT_PARAM = "cmis:name";

    String FIND_CONTAINER_FOLDER_QUERY =
            "SELECT e FROM AcmContainerFolder e WHERE e.containerObjectId = :objectId AND e.containerObjectType = :objectType";

    String PROPERTY_KEY_DEFAULT_FOLDER_BASE_PATH = "ecm.defaultBasePath";
    String PROPERTY_PREFIX_FOLDER_PATH_BY_TYPE = "ecm.defaultPath.";

}
