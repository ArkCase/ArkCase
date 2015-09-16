package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.CaptureFolderService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph.mcgrady on 9/14/2015.
 */
public class CaptureFolderServiceImpl implements CaptureFolderService {
    private String captureFolderToWatch;
    private String captureExtensions;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Copies non-pdf files to the Ephesoft shared hot folder for recognition,
     * provided that they are supported file types in Ephesoft
     * @param ephesoftFile - contains the metadata for the file to send to Ephesoft
     * @param fileInputStream - binary data comprising the non-pdf file
     */
    public void copyToCaptureHotFolder(EcmFile ephesoftFile, InputStream fileInputStream) throws Exception {
        FileOutputStream captureFileOutputStream = null;
        try {
            // Creates a filename in the format that Ephesoft expects for this drop file
            String fileExtension = FilenameUtils.getExtension(ephesoftFile.getFileName());
            String fileName = buildEphesoftFileName(ephesoftFile, fileExtension);

            // If the file is not a supported type then it will not be copied
            List<String> captureSupportedTypes = parseStringList(captureExtensions);

            // Copies supported file types to the Ephesoft hot folder
            if (isTypeSupported(fileExtension, captureSupportedTypes)) {
                captureFileOutputStream = new FileOutputStream(new File(buildFullEphesoftDropPath(fileName, captureFolderToWatch)));
                fileInputStream.reset(); // the stream was already read by the addFile mule flow in the previous stage, so it needs to be reset here
                IOUtils.copy(fileInputStream, captureFileOutputStream);
            }
        } finally {
            if (captureFileOutputStream != null) {
                captureFileOutputStream.close();
            }
        }
    }

    /**
     * Creates the name of the Ephesoft capture document in the standard format
     * which has the containerObjectId, containerObjectType, and fileId separated by underscores
     * @param ephesoftFile - contains the metadata for the file to send to Ephesoft
     * @param fileExtension - file type extension (e.x. png, tiff, jpg)
     * @return filename formatted for Ephesoft containerId + "_" + containerType + "_" fileId + "." + extension
     * @throws Exception if one of the components of the name is not present
     */
    private static String buildEphesoftFileName(EcmFile ephesoftFile, String fileExtension) throws Exception {
        if (ephesoftFile == null) {
            throw new Exception("ephesoftFile is null");
        }
        if (fileExtension == null || fileExtension.trim().length() == 0) {
            throw new Exception("fileExtension is null or empty");
        }

        // To build the ephesoft format name we need the file id, container id, and the original filename
        String fileName = ephesoftFile.getFileName();
        Long fileId = ephesoftFile.getFileId();
        Long containerObjectId = ephesoftFile.getContainer().getContainerObjectId();
        String containerObjectType = ephesoftFile.getContainer().getContainerObjectType();
        if (fileName == null) {
            throw new Exception("fileName is null");
        }
        if (fileId == null) {
            throw new Exception("fileId is null");
        }
        if (containerObjectId == null) {
            throw new Exception("containerObjectId is null");
        }
        if (containerObjectType == null || containerObjectType.length() == 0) {
            throw new Exception("containerObjectType is null or empty");
        }

        return containerObjectId + "_" + containerObjectType + "_" + fileId + "." + fileExtension;
    }

    /**
     * Combines the Ephesoft folder path (from configuration) with
     * the filename of the document.
     * @param fileName - name of the document
     * @param captureFolder - name of the Ephesoft capture hot folder
     * @return full path for the file to be dropped into the Ephesoft hot folder
     */
    private static String buildFullEphesoftDropPath(String fileName, String captureFolder) {
        String fullPath = "";
        if (captureFolder != null && fileName != null) {
            if (captureFolder.endsWith("/")) {
                fullPath = captureFolder + fileName;
            } else {
                fullPath = captureFolder + "/" + fileName;
            }
        }
        return fullPath;
    }

    /**
     * Determines if Ephesoft supports the given file type by searching the
     * list of supported types
     * @param fileType - file extension of the file (e.x. .png, .tiff)
     * @param supportedList - list of the valid file type extensions supported by Ephesoft
     * @return true if the type is supported, false otherwise
     */
    private static boolean isTypeSupported(String fileType, List<String> supportedList) {
        boolean isSupported = false;
        if (supportedList != null && fileType != null) {
            for (String supportedType : supportedList) {
                if (fileType.equalsIgnoreCase(supportedType)) {
                    isSupported = true;
                    break;
                }
            }
        }
        return isSupported;
    }

    /**
     * Takes a string containing a comma separated list of string values and
     * generates a list of trimmed String objects for each individual entry
     * @param commaSeparatedList - comma separated list of string values (e.x. "1,abc,6c")
     * @return array of Strings parsed from the list with trailing/leading whitespace removed from each entry
     */
    private static List<String> parseStringList(String commaSeparatedList) {
        List<String> stringList = new ArrayList<String>();
        if (commaSeparatedList != null && commaSeparatedList.length() > 0) {
            String[] listItems = commaSeparatedList.split(",");
            for (String listItem : listItems) {
                stringList.add(listItem.trim());
            }
        }
        return stringList;
    }

    public String getCaptureFolderToWatch() {
        return captureFolderToWatch;
    }
    public void setCaptureFolderToWatch(String captureFolderToWatch) {
        this.captureFolderToWatch = captureFolderToWatch;
    }
    public String getCaptureExtensions() {
        return captureExtensions;
    }
    public void setCaptureExtensions(String captureExtensions) {
        this.captureExtensions = captureExtensions;
    }
}