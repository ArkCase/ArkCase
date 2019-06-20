package com.armedia.acm.plugins.ecm.service.impl;
/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.files.capture.CaptureConfig;
import com.armedia.acm.plugins.ecm.exception.EphesoftException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.SendForPdfConversion;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by joseph.mcgrady on 9/14/2015.
 */
public class SendForPdfConversionImpl implements SendForPdfConversion
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private CaptureConfig captureConfig;

    /**
     * Copies non-pdf files to the Ephesoft shared hot folder for recognition,
     * provided that they are supported file types in Ephesoft
     * 
     * @param toBeConverted
     *            - contains the metadata for the file to send to Ephesoft
     * @param fileInputStream
     *            - binary data comprising the non-pdf file
     */
    @Override
    public void copyToCaptureHotFolder(EcmFile toBeConverted, InputStream fileInputStream) throws EphesoftException, IOException
    {
        FileOutputStream captureFileOutputStream = null;
        try
        {
            // Creates a filename in the format that Ephesoft expects for this drop file
            String fileExtension = toBeConverted.getFileExtension();
            String fileName = buildEphesoftFileName(toBeConverted, fileExtension);

            // Copies supported file types to the Ephesoft hot folder
            captureFileOutputStream = new FileOutputStream(new File(buildFullEphesoftDropPath(fileName)));
            IOUtils.copy(fileInputStream, captureFileOutputStream);
        }
        finally
        {
            if (captureFileOutputStream != null)
            {
                captureFileOutputStream.close();
            }
        }
    }

    /**
     * Creates the name of the Ephesoft capture document in the standard format
     * which has the containerObjectId, containerObjectType, and fileId separated by underscores
     * 
     * @param ephesoftFile
     *            - contains the metadata for the file to send to Ephesoft
     * @param fileExtension
     *            - file type extension (e.x. png, tiff, jpg)
     * @return filename formatted for Ephesoft containerId + "_" + containerType + "_" fileId + "." + extension
     * @throws Exception
     *             if one of the components of the name is not present
     */
    private String buildEphesoftFileName(EcmFile ephesoftFile, String fileExtension) throws EphesoftException
    {
        if (ephesoftFile == null)
        {
            throw new EphesoftException("ephesoftFile is null");
        }
        if (fileExtension == null || fileExtension.trim().length() == 0)
        {
            throw new EphesoftException("fileExtension is null or empty");
        }

        // To build the ephesoft format filename we need the file id, container id and container type
        Long fileId = ephesoftFile.getFileId();
        Long containerObjectId = ephesoftFile.getContainer().getContainerObjectId();
        String containerObjectType = ephesoftFile.getContainer().getContainerObjectType();
        if (fileId == null)
        {
            throw new EphesoftException("fileId is null");
        }
        if (containerObjectId == null)
        {
            throw new EphesoftException("containerObjectId is null");
        }
        if (containerObjectType == null || containerObjectType.length() == 0)
        {
            throw new EphesoftException("containerObjectType is null or empty");
        }

        return containerObjectId + "_" + containerObjectType + "_" + fileId + "." + fileExtension;
    }

    /**
     * Combines the Ephesoft folder path (from configuration) with
     * the filename of the document.
     * 
     * @param fileName
     *            - name of the document
     * @return full path for the file to be dropped into the Ephesoft hot folder
     */
    private String buildFullEphesoftDropPath(String fileName)
    {
        String fullPath = "";
        String folderToWatch = captureConfig.getRootFolderToWatch();
        if (folderToWatch != null && fileName != null)
        {
            if (folderToWatch.endsWith("/"))
            {
                fullPath = folderToWatch + fileName;
            }
            else
            {
                fullPath = folderToWatch + "/" + fileName;
            }
        }
        return fullPath;
    }

    public CaptureConfig getCaptureConfig()
    {
        return captureConfig;
    }

    public void setCaptureConfig(CaptureConfig captureConfig)
    {
        this.captureConfig = captureConfig;
    }
}
