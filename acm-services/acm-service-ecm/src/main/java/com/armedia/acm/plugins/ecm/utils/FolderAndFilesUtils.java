package com.armedia.acm.plugins.ecm.utils;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    public  String buildSafeFolderName(String folderName) {
        if (folderName != null) {
            String regex = EcmFileConstants.INVALID_CHARACTERS_IN_FOLDER_NAME_REGEX;
            String replacement = EcmFileConstants.INVALID_CHARACTERS_IN_FOLDER_NAME_REPLACEMENT;

            folderName = folderName.replaceAll(regex, replacement);
        }
        return folderName;
    }

    public  String getActiveVersionCmisId( EcmFile ecmFile ) {
        List<EcmFileVersion> versions = ecmFile.getVersions();
        if ( versions == null ) {
            return ecmFile.getVersionSeriesId();
        }
        return versions.stream().filter(fv -> fv.getVersionTag().equals(ecmFile.getActiveVersionTag())).
                map(EcmFileVersion::getCmisObjectId).findFirst().orElse(ecmFile.getVersionSeriesId());
    }

    public String createUniqueIdentificator(String input)
    {
        if (input != null && input.length() > 0)
        {
            input = input.replace(" ", "_");

            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
            String dateString = dateFormat.format(new Date());

            String[] inputArray = input.split("\\.");

            if (inputArray != null && inputArray.length == 1)
            {
                input = input +  "_" + dateString;
            }
            else if (inputArray != null && inputArray.length > 1)
            {
                input = input.replace("." + inputArray[inputArray.length - 1], "_" + dateString + "." + inputArray[inputArray.length - 1]);
            }
        }

        return input;
    }
}
