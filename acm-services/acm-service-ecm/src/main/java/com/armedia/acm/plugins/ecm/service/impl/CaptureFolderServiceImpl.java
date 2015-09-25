package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.plugins.ecm.exception.EphesoftException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.CaptureFolderService;
import com.armedia.acm.plugins.ecm.utils.GenericUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by joseph.mcgrady on 9/14/2015.
 */
public class CaptureFolderServiceImpl implements CaptureFolderService {
    private String captureFolderToWatch;
    private String captureExtensions;
    private String captureCopyTypes;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Copies non-pdf files to the Ephesoft shared hot folder for recognition,
     * provided that they are supported file types in Ephesoft
     * @param ephesoftFile - contains the metadata for the file to send to Ephesoft
     * @param fileInputStream - binary data comprising the non-pdf file
     */
    public void copyToCaptureHotFolder(EcmFile ephesoftFile, InputStream fileInputStream) throws EphesoftException, IOException {
        FileOutputStream captureFileOutputStream = null;
        try {
            // Creates a filename in the format that Ephesoft expects for this drop file
            String fileExtension = FilenameUtils.getExtension(ephesoftFile.getFileName());
            String fileName = buildEphesoftFileName(ephesoftFile, fileExtension);

            // If the file is not a supported format then it will not be copied
            List<String> captureSupportedTypes = GenericUtils.parseStringList(captureExtensions);

            // Determines if the ecmFile's type (authorization, abstract, etc.) allows it to be copied
            boolean isFileCopyable = GenericUtils.isFileTypeInList(ephesoftFile.getFileType(), captureCopyTypes);

            // Copies supported file types to the Ephesoft hot folder
            if (isFileCopyable && isFormatSupported(fileExtension, captureSupportedTypes)) {
                captureFileOutputStream = new FileOutputStream(new File(buildFullEphesoftDropPath(fileName)));
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
    private String buildEphesoftFileName(EcmFile ephesoftFile, String fileExtension) throws EphesoftException {
        if (ephesoftFile == null) {
            throw new EphesoftException("ephesoftFile is null");
        }
        if (fileExtension == null || fileExtension.trim().length() == 0) {
            throw new EphesoftException("fileExtension is null or empty");
        }

        // To build the ephesoft format filename we need the file id, container id and container type
        Long fileId = ephesoftFile.getFileId();
        Long containerObjectId = ephesoftFile.getContainer().getContainerObjectId();
        String containerObjectType = ephesoftFile.getContainer().getContainerObjectType();
        if (fileId == null) {
            throw new EphesoftException("fileId is null");
        }
        if (containerObjectId == null) {
            throw new EphesoftException("containerObjectId is null");
        }
        if (containerObjectType == null || containerObjectType.length() == 0) {
            throw new EphesoftException("containerObjectType is null or empty");
        }

        return containerObjectId + "_" + containerObjectType + "_" + fileId + "." + fileExtension;
    }

    /**
     * Combines the Ephesoft folder path (from configuration) with
     * the filename of the document.
     * @param fileName - name of the document
     * @return full path for the file to be dropped into the Ephesoft hot folder
     */
    private String buildFullEphesoftDropPath(String fileName) {
        String fullPath = "";
        if (captureFolderToWatch != null && fileName != null) {
            if (captureFolderToWatch.endsWith("/")) {
                fullPath = captureFolderToWatch + fileName;
            } else {
                fullPath = captureFolderToWatch + "/" + fileName;
            }
        }
        return fullPath;
    }

    /**
     * Determines if Ephesoft supports the given file format by searching the
     * list of supported types
     * @param fileType - file extension of the file (e.x. .png, .tiff)
     * @param supportedList - list of the valid file type extensions supported by Ephesoft
     * @return true if the type is supported, false otherwise
     */
    private static boolean isFormatSupported(String fileType, List<String> supportedList) {
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
    public String getCaptureCopyTypes() {
        return captureCopyTypes;
    }
    public void setCaptureCopyTypes(String captureCopyTypes) {
        this.captureCopyTypes = captureCopyTypes;
    }
}