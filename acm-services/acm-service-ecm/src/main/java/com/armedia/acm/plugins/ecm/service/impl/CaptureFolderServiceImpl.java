package com.armedia.acm.plugins.ecm.service.impl;

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
     * @param fileName - name of the non-pdf file including the type extension (e.x. .tiff)
     * @param fileInputStream - binary data comprising the non-pdf file
     */
    public void copyToCaptureHotFolder(String fileName, InputStream fileInputStream) {
        FileOutputStream captureFileOutputStream = null;
        try {
            List<String> captureSupportedTypes = parseStringList(captureExtensions);
            String fileExtension = FilenameUtils.getExtension(fileName);

            // Copies supported file types to the Ephesoft hot folder
            if (isTypeSupported(fileExtension, captureSupportedTypes)) {
                captureFileOutputStream = new FileOutputStream(new File(captureFolderToWatch + "/" + fileName));
                IOUtils.copy(fileInputStream, captureFileOutputStream);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (captureFileOutputStream != null)
                    captureFileOutputStream.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
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