package com.armedia.acm.plugins.ecm.model;

/**
 * Created by marjan.stefanoski on 03.04.2015.
 */
public interface AcmFolderConstants {

    String MULE_ENDPOINT_RENAME_FOLDER = "vm://renameFolder.in";
    String MULE_ENDPOINT_ADD_NEW_FOLDER = "vm://addNewFolder.in";
    String MULE_ENDPOINT_DELETE_EMPTY_FOLDER = "vm://deleteFolder.in";

    String OBJECT_FOLDER_TYPE = "FOLDER";
    String USER_ACTION_RENAME_FOLDER = "RENAME";
    String USER_ACTION_ADD_NEW_FOLDER = "ADD NEW";
    String USER_ACTION_DELETE_NEW_FOLDER = "DELETE";

    String PARENT_FOLDER_ID="parentFolderId";
    String ACM_FOLDER_ID ="acmFolderId";
    String NEW_FOLDER_NAME="newFolderName";

    String ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY = "addNewFolderException";
    String IS_FOLDER_NOT_EMPTY_INBOUND_PROPERTY = "isFolderNotEmpty";

    String SUCCESS_FOLDER_DELETE_MSG = "Folder deleted successfully: ";

    String IP_ADDRESS_ATTRIBUTE = "acm_ip_address";
}
