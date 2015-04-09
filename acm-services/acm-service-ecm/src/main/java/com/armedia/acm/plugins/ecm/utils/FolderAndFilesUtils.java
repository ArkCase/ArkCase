package com.armedia.acm.plugins.ecm.utils;

import com.armedia.acm.plugins.ecm.model.EcmFileConstants;

/**
 * Created by marjan.stefanoski on 09.04.2015.
 */
public class FolderAndFilesUtils {

    /**
     * Replace all not allowed characters in folder name with underscore
     *
     * @param folderName
     * @return
     */
    public static String buildSafeFolderName(String folderName) {
        if (folderName != null) {
            String regex = EcmFileConstants.INVALID_CHARACTERS_IN_FOLDER_NAME_REGEX;
            String replacement = EcmFileConstants.INVALID_CHARACTERS_IN_FOLDER_NAME_REPLACEMENT;

            folderName = folderName.replaceAll(regex, replacement);
        }
        return folderName;
    }
}
