package com.armedia.acm.files.capture;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import org.springframework.beans.factory.annotation.Value;

public class CaptureConfig
{
    @Value("${capture.fileExtensions}")
    private String fileExtension;

    @Value("${capture.rootFolderToWatch}")
    private String rootFolderToWatch;

    @Value("${capture.working.folder}")
    private String workingFolder;

    @Value("${capture.completed.folder}")
    private String completedFolder;

    @Value("${capture.error.folder}")
    private String errorFolder;

    @Value("${capture.attachments.supported-object-types}")
    private String attachmentSupportedObjectTypes;

    @Value("${capture.sendForPdfConversionFolder}")
    private String sendForPdfConversionFolder;

    @Value("${capture.convertedPdfsFolder}")
    private String convertedPdfsFolder;

    @Value("${capture.fileTypesToBeConvertedToPDF}")
    private String fileTypesToBeConvertedToPdf;

    @Value("${capture.fileFormatsToBeConvertedToPDF}")
    private String fileFormatsToBeConvertedToPdf;

    @Value("${capture.fileTypesToMerge}")
    private String fileTypesToMerge;

    @Value("${capture.fileFormatsToMerge}")
    private String fileFormatsToMerge;

    @Value("${capture.oxm.file.path}")
    private String oxmFilePath;
    
    @Value("${capture.outgoingEmail.folderName}")
    private String outgoingEmailFolderName;

    public String getFileExtension()
    {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension)
    {
        this.fileExtension = fileExtension;
    }

    public String getRootFolderToWatch()
    {
        return rootFolderToWatch;
    }

    public void setRootFolderToWatch(String rootFolderToWatch)
    {
        this.rootFolderToWatch = rootFolderToWatch;
    }

    public String getWorkingFolder()
    {
        return workingFolder;
    }

    public void setWorkingFolder(String workingFolder)
    {
        this.workingFolder = workingFolder;
    }

    public String getCompletedFolder()
    {
        return completedFolder;
    }

    public void setCompletedFolder(String completedFolder)
    {
        this.completedFolder = completedFolder;
    }

    public String getErrorFolder()
    {
        return errorFolder;
    }

    public void setErrorFolder(String errorFolder)
    {
        this.errorFolder = errorFolder;
    }

    public String getAttachmentSupportedObjectTypes()
    {
        return attachmentSupportedObjectTypes;
    }

    public void setAttachmentSupportedObjectTypes(String attachmentSupportedObjectTypes)
    {
        this.attachmentSupportedObjectTypes = attachmentSupportedObjectTypes;
    }

    public String getSendForPdfConversionFolder()
    {
        return sendForPdfConversionFolder;
    }

    public void setSendForPdfConversionFolder(String sendForPdfConversionFolder)
    {
        this.sendForPdfConversionFolder = sendForPdfConversionFolder;
    }

    public String getConvertedPdfsFolder()
    {
        return convertedPdfsFolder;
    }

    public void setConvertedPdfsFolder(String convertedPdfsFolder)
    {
        this.convertedPdfsFolder = convertedPdfsFolder;
    }

    public String getFileTypesToBeConvertedToPdf()
    {
        return fileTypesToBeConvertedToPdf;
    }

    public void setFileTypesToBeConvertedToPdf(String fileTypesToBeConvertedToPdf)
    {
        this.fileTypesToBeConvertedToPdf = fileTypesToBeConvertedToPdf;
    }

    public String getFileFormatsToBeConvertedToPdf()
    {
        return fileFormatsToBeConvertedToPdf;
    }

    public void setFileFormatsToBeConvertedToPdf(String fileFormatsToBeConvertedToPdf)
    {
        this.fileFormatsToBeConvertedToPdf = fileFormatsToBeConvertedToPdf;
    }

    public String getFileTypesToMerge()
    {
        return fileTypesToMerge;
    }

    public void setFileTypesToMerge(String fileTypesToMerge)
    {
        this.fileTypesToMerge = fileTypesToMerge;
    }

    public String getFileFormatsToMerge()
    {
        return fileFormatsToMerge;
    }

    public void setFileFormatsToMerge(String fileFormatsToMerge)
    {
        this.fileFormatsToMerge = fileFormatsToMerge;
    }

    public String getOxmFilePath()
    {
        return oxmFilePath;
    }

    public void setOxmFilePath(String oxmFilePath)
    {
        this.oxmFilePath = oxmFilePath;
    }

    public String getOutgoingEmailFolderName() 
    {
        return outgoingEmailFolderName;
    }

    public void setOutgoingEmailFolderName(String outgoingEmailFolderName) 
    {
        this.outgoingEmailFolderName = outgoingEmailFolderName;
    }
}
