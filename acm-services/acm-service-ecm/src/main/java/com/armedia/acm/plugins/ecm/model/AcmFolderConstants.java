package com.armedia.acm.plugins.ecm.model;

/**
 * Created by marjan.stefanoski on 03.04.2015.
 */
public interface AcmFolderConstants {

    String MULE_ENDPOINT_RENAME_FOLDER = "vm://renameFolder.in";
    String MULE_ENDPOINT_ADD_NEW_FOLDER = "vm://addNewFolder.in";
    String MULE_ENDPOINT_DELETE_EMPTY_FOLDER = "vm://deleteFolder.in";
    String MULE_ENDPOINT_DELETE_FOLDER_TREE = "vm://deleteFolderTree.in";
    String MULE_ENDPOINT_LIST_FOLDER = "vm://listFolder.in";
    String MULE_ENDPOINT_MOVE_FOLDER = "vm://moveFolder.in";
    String MULE_ENDPOINT_GET_FOLDER = "vm://getFolder.in";
    String MULE_ENDPOINT_CREATE_FOLDER_BY_PATH = "vm://createFolderByPath.in";

    String OBJECT_FOLDER_TYPE = "FOLDER";

    String CONTAINER_OBJECT_TYPE = "objectType";
    String CONTAINER_OBJECT_TITLE = "objectTitle";

    String USER_ACTION_RENAME_FOLDER = "RENAME";
    String USER_ACTION_ADD_NEW_FOLDER = "ADD NEW";
    String USER_ACTION_CREATE_FOLDER_BY_PATH = " CREATE";
    String USER_ACTION_LIST_FOLDER = "LIST";
    String USER_ACTION_DELETE_FOLDER = "DELETE";
    String USER_ACTION_MOVE_FOLDER = "MOVE";
    String USER_ACTION_COPY_FOLDER = "COPY";
    String USER_ACTION_GET_FOLDER = "GET";

    String EVENT_TYPE_FOLDER_MOVED = "com.armedia.acm.folder.moved";
    String EVENT_TYPE_FOLDER_COPIED = "com.armedia.acm.folder.copied";

    String PARENT_FOLDER_ID="parentFolderId";
    String ACM_FOLDER_ID ="acmFolderId";
    String NEW_FOLDER_NAME="newFolderName";
    String DESTINATION_FOLDER_ID = "dstFolderId";
    String FOLDER_PATH = "folderPath";

    String ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY = "addNewFolderException";
    String DELETE_FOLDER_EXCEPTION_INBOUND_PROPERTY = "deleteFolderException";
    String DELETE_FOLDER_TREE_EXCEPTION_INBOUND_PROPERTY = "deleteFolderTreeException";
    String LIST_FOLDER_EXCEPTION_INBOUND_PROPERTY = "listFolderException";
    String MOVE_FOLDER_EXCEPTION_INBOUND_PROPERTY = "moveFolderException";
    String IS_FOLDER_NOT_EMPTY_INBOUND_PROPERTY = "isFolderNotEmpty";
    String IS_FOLDER_EMPTY_INBOUND_PROPERTY = "isFolderEmpty";
    String COPY_FOLDER_EXCEPTION_INBOUND_PROPERTY = "copyFolderException";
    String GET_FOLDER_EXCEPTION_INBOUND_PROPERTY = "getFolderException";
    String CREATE_FOLDER_BY_PATH_EXCEPTION_INBOUND_PROPERTY = "createFolderByPathException";

    int ZERO = 0;

    String SUCCESS_FOLDER_DELETE_MSG = "Folder deleted successfully";

    String IP_ADDRESS_ATTRIBUTE = "acm_ip_address";

    String CMIS_OBJECT_TYPE_ID_FOLDER = "cmis:folder";
    String CMIS_OBJECT_TYPE_ID_FILE = "cmis:document";

    
    String FOLDER_STRUCTURE_KEY_NAME = "name";
    String FOLDER_STRUCTURE_KEY_CHILDREN = "children";
    String FOLDER_STRUCTURE_KEY_ATTACHMENT = "attachment";

    String PROPERTY_KEY_DEFAULT_FOLDER_BASE_PATH = "ecm.defaultBasePath";
    String PROPERTY_PREFIX_FOLDER_PATH_BY_TYPE = "ecm.defaultPath.";

    // Multiple CMIS configurations constants
    String CONFIGURATION_REFERENCE = "configRef";
}
