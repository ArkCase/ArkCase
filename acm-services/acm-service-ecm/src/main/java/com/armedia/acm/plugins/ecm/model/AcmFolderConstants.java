package com.armedia.acm.plugins.ecm.model;

/**
 * Created by marjan.stefanoski on 03.04.2015.
 */
public interface AcmFolderConstants {

    String MULE_ENDPOINT_RENAME_FOLDER = "vm://renameFolder.in";
    String MULE_ENDPOINT_ADD_NEW_FOLDER = "vm://addNewFolder.in";

    String OBJECT_FOLDER_TYPE = "FOLDER";
    String USER_ACTION_RENAME_FOLDER = "RENAME FOLDER";
    String USER_ACTION_ADD_NEW_FOLDER = "ADD NEW FOLDER";

    String PARENT_FOLDER_ID="parentFolderId";
    String ACM_FOLER_ID="acmFolderId";
    String NEW_FOLDER_NAME="newFolderName";

    String OBJECT_TYPE = "FOLDER";

    String ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY = "addNewFolderException";

}
