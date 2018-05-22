package com.armedia.acm.convertfolder;

/*-
 * #%L
 * ACM Service: Folder Converting Service
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.commons.io.FileUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 17, 2018
 *
 */
public abstract class PdfConverterBase implements FileConverter
{
    /**
     * PDF mime type.
     */
    private static final String APPLICATION_PDF = "application/pdf";

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Used to retrieve file information from the system.
     */
    private EcmFileService fileService;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.convertfolder.FileConverter#convert(com.armedia.acm.plugins.ecm.model.EcmFile,
     * org.springframework.security.core.Authentication)
     */
    @Override
    public void convert(EcmFile file, Authentication auth) throws ConversionException
    {
        String tempUploadFolderPath = FileUtils.getTempDirectoryPath();
        String fileName = file.getFileName() + "." + file.getFileExtension();
        log.debug("Converting file [{}].", fileName);
        File tempOriginFile = new File(tempUploadFolderPath + File.separator + fileName + "_" + Thread.currentThread().getName());

        try (InputStream fileByteStream = fileService.downloadAsInputStream(file.getId()))
        {
            FileUtils.copyInputStreamToFile(fileByteStream, tempOriginFile);
        }
        catch (IOException | MuleException | AcmUserActionFailedException e)
        {
            FileUtils.deleteQuietly(tempOriginFile);
            log.warn("Failed to retrieve file [{}] with id [{}] of type [{}].", fileName, file.getId(), file.getFileExtension(), e);
            throw new ConversionException(String.format("Failed to convert file [%s] with id [%s] of type [%s].", fileName, file.getId(),
                    file.getFileExtension()), e);
        }

        File tempPdfFile = new File(tempUploadFolderPath + File.separator + createFileName(file));

        performConversion(file, tempUploadFolderPath, tempOriginFile, tempPdfFile);

        try (FileInputStream fis = new FileInputStream(tempPdfFile))
        {
            EcmFile metadata = new EcmFile();
            metadata.setFileType(file.getFileType());
            metadata.setFileLang(file.getFileLang());
            metadata.setFileName(createFileName(file));
            metadata.setCmisRepositoryId(file.getCmisRepositoryId());
            metadata.setFileActiveVersionMimeType(APPLICATION_PDF);

            fileService.upload(auth, file.getParentObjectType(), file.getParentObjectId(), file.getFolder().getCmisFolderId(),
                    file.getFileName(), fis, metadata);
        }
        catch (IOException | AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            log.warn("Failed to upload the conversion of file [{}] with id [{}] of type [{}].", fileName, file.getId(),
                    file.getFileExtension(), e);
            throw new ConversionException(
                    String.format("Failed to upload the conversion of file [%s] with id [%s] of type [%s].", fileName, file.getId(),
                            file.getFileExtension()),
                    e);
        }
        finally
        {
            FileUtils.deleteQuietly(tempPdfFile);
        }

    }

    protected abstract void performConversion(EcmFile file, String tempUploadFolderPath, File tempOriginFile, File tempPdfFile)
            throws ConversionException;

    /**
     * @param fileService
     *            the fileService to set
     */
    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

}
