package com.armedia.acm.plugins.ecm.utils;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

import java.util.List;

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

    public static String getActiveVersionCmisId(EcmFile ecmFile) {
        List<EcmFileVersion> fileVersionList = ecmFile.getVersions();
        String cmisId = null;
        for(EcmFileVersion fileVersion: fileVersionList){
            if(fileVersion.getVersionTag().equals(ecmFile.getActiveVersionTag())){
                cmisId = fileVersion.getCmisObjectId();
                break;
            }
        }
        return cmisId;
    }
}
