package com.armedia.acm.plugins.ecm.utils;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 09.04.2015.
 */
public class FolderAndFilesUtils
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Replace all not allowed characters in folder name with underscore
     *
     * @param folderName
     * @return
     */
    public String buildSafeFolderName(String folderName)
    {
        if (folderName != null)
        {
            String regex = EcmFileConstants.INVALID_CHARACTERS_IN_FOLDER_NAME_REGEX;
            String replacement = EcmFileConstants.INVALID_CHARACTERS_IN_FOLDER_NAME_REPLACEMENT;

            folderName = folderName.replaceAll(regex, replacement);
        }
        return folderName;
    }

    public String getActiveVersionCmisId(EcmFile ecmFile)
    {
        List<EcmFileVersion> versions = ecmFile.getVersions();
        if (versions == null)
        {
            return ecmFile.getVersionSeriesId();
        }
        String cmisId = null;

        // follow this way for now till we figure out
        // why stream code below is not working
        for (EcmFileVersion version : versions)
        {
            if (version.getVersionTag().equals(ecmFile.getActiveVersionTag()))
            {
                cmisId = version.getCmisObjectId();
            }
        }
        if (cmisId == null)
        {
            cmisId = ecmFile.getVersionSeriesId();
        }
        /*
         * cmisId = versions.stream().filter(fv -> (fv.getVersionTag()).equals(ecmFile.getActiveVersionTag())).
         * map(EcmFileVersion::getCmisObjectId).findFirst().orElse(ecmFile.getVersionSeriesId());
         */

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
                input = input + "_" + dateString;
            } else if (inputArray != null && inputArray.length > 1)
            {
                input = input.replace("." + inputArray[inputArray.length - 1], "_" + dateString + "." + inputArray[inputArray.length - 1]);
            }
        }

        return input;
    }

    public String createUniqueFolderName(String name)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
        String dateString = dateFormat.format(new Date());
        return name + "_" + dateString;
    }

    public Long convertToLong(String folderId)
    {
        try
        {
            return Long.parseLong(folderId);
        } catch (Exception e)
        {
            LOG.error("Cannot convert String representation of folderId=" + folderId + " to Long", e);
        }

        return null;
    }

    /**
     * Returns a PDF file which matches the supplied ArkCase model file type from the list and which is a PDF document
     * since only PDF files can be merged
     * 
     * @param fileList
     *            - List of ecmFiles which will be searched for the desired type
     * @param fileType
     *            - type to search for in the ecm file list
     * @return ecmFile which has the given ArkCase type and is a PDF, or null if not found
     */
    public EcmFile findMatchingPDFFileType(List<EcmFile> fileList, String fileType)
    {
        EcmFile matchFile = null;
        for (EcmFile ecmFile : fileList)
        {
            if (ecmFile.getFileType().equalsIgnoreCase(fileType) && ecmFile.getFileActiveVersionMimeType().equals("application/pdf"))
            {
                matchFile = ecmFile;
            }
        }
        return matchFile;
    }

    public String getBaseFileName(String fileName)
    {
        if (fileName.lastIndexOf(".") > 0)
        {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else
        {
            return fileName;
        }
    }

    public String getFileNameExtension(String fileName)
    {
        if (fileName.lastIndexOf(".") > 0)
        {
            return fileName.substring(fileName.lastIndexOf("."));
        } else
        {
            return "";
        }
    }

    public String detectFileContentType(InputStream inputStream, String fileName) throws IOException, MimeTypeException
    {
        TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
        return getMediaType(tikaConfig, inputStream, fileName).toString();
    }

    public String detectFileExtension(InputStream inputStream, String fileName) throws IOException, MimeTypeException
    {
        TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
        MediaType mediaType = getMediaType(tikaConfig, inputStream, fileName);
        MimeType mimeType = tikaConfig.getMimeRepository().forName(mediaType.toString());
        return mimeType.getExtension();
    }

    public String[] detectFileContentTypeAndExtension(InputStream inputStream, String fileName) throws IOException, MimeTypeException
    {
        String[] contentTypeAndExtension = new String[2];
        TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
        MediaType mediaType = getMediaType(tikaConfig, inputStream, fileName);
        MimeType mimeType = tikaConfig.getMimeRepository().forName(mediaType.toString());
        contentTypeAndExtension[0] = mediaType.toString();
        contentTypeAndExtension[1] = mimeType.getExtension();
        return contentTypeAndExtension;
    }

    private MediaType getMediaType(TikaConfig tikaConfig, InputStream inputStream, String fileName) throws IOException
    {
        Detector detector = tikaConfig.getDetector();
        TikaInputStream stream = TikaInputStream.get(inputStream);

        Metadata metadata = new Metadata();
        metadata.add(Metadata.RESOURCE_NAME_KEY, fileName);
        MediaType mediaType = detector.detect(stream, metadata);
        return mediaType;
    }

}