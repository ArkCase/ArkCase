package com.armedia.acm.plugins.ecm.utils;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

import java.util.List;
import java.util.Optional;

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

    public static String getActiveVersionCmisId( EcmFile ecmFile ) {
        List<EcmFileVersion> versions = ecmFile.getVersions();
        if ( versions == null ) {
            return ecmFile.getVersionSeriesId();
        }
        return versions.stream().filter(fv -> fv.getVersionTag().equals(ecmFile.getActiveVersionTag())).
                map(EcmFileVersion::getCmisObjectId).findFirst().orElse(ecmFile.getVersionSeriesId());
    }
}
