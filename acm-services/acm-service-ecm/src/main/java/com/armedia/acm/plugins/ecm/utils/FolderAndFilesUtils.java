package com.armedia.acm.plugins.ecm.utils;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by marjan.stefanoski on 09.04.2015.
 */
public class FolderAndFilesUtils {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
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
        String cmisId = null;

        //follow this way for now till we figure out
        //why stream code below is not working
        for(EcmFileVersion version : versions){
            if(version.getVersionTag().equals(ecmFile.getActiveVersionTag())){
                cmisId = version.getCmisObjectId();
            }
        }
        if(cmisId == null){
            cmisId = ecmFile.getVersionSeriesId();
        }
        /*cmisId = versions.stream().filter(fv -> (fv.getVersionTag()).equals(ecmFile.getActiveVersionTag())).
                map(EcmFileVersion::getCmisObjectId).findFirst().orElse(ecmFile.getVersionSeriesId());*/

        return cmisId;
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

    public String createUniqueFolderName(String name){
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
        String dateString = dateFormat.format(new Date());
        return name +"_"+ dateString;
    }
    
    public Long convertToLong(String folderId)
    {
    	try
    	{
    		return Long.parseLong(folderId);
    	}
    	catch (Exception e)
    	{
    		LOG.error("Cannot convert String representation of folderId=" + folderId + " to Long", e);
    	}
    	
    	return null;
    }

    /**
     * Returns a file which matches the supplied file type from the list
     * @param fileList - List of ecmFiles which will be searched for the desired type
     * @param fileType - type to search for in the ecm file list
     * @return ecmFile which has the given type, or null if not found
     */
    public EcmFile findMatchFileType(List<EcmFile> fileList, String fileType)
    {
        EcmFile matchFile = null;
        for (EcmFile ecmFile : fileList) {
            LOG.debug(ecmFile.getFileName() + ": [" + ecmFile.getFileType() + "]");
            if (ecmFile.getFileType().equals(fileType)) {
                matchFile = ecmFile;
            }
        }
        return matchFile;
    }
}