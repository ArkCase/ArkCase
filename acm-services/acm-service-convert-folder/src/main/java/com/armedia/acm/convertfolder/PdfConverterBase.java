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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

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
    private Logger log = LogManager.getLogger(getClass());

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
    public void convert(EcmFile file, String username) throws ConversionException
    {
        convertFile(file, "", username, false);
    }

    @Override
    public File convert(EcmFile file) throws ConversionException {
        return convertAndReturnConvertedFile(file);
    }

    @Override
    public File convertAndReturnConvertedFile(EcmFile file) throws ConversionException
    {
        return convertFile(file, "", "", true);
    }

    @Override
    public File convertAndReturnConvertedFile(EcmFile file, String version) throws ConversionException
    {
        return convertFile(file, version, "", true);
    }

    private File convertFile(EcmFile file, String version, String username, Boolean skipUploadAndReturnConvertedFile) throws ConversionException
    {
        String tempUploadFolderPath = FileUtils.getTempDirectoryPath();
        String fileName = file.getFileName() + "." + file.getFileExtension();
        String fileVersion = (version.isEmpty() || version == null) ? file.getActiveVersionTag() : version;
        log.debug("Converting file [{}].", fileName);
        File tempOriginFile = new File(tempUploadFolderPath + File.separator + fileName + "_" + Thread.currentThread().getName());

        try (InputStream fileByteStream = fileService.downloadAsInputStream(file.getId(), fileVersion))
        {
            FileUtils.copyInputStreamToFile(fileByteStream, tempOriginFile);
        }
        catch (IOException | AcmUserActionFailedException e)
        {
            FileUtils.deleteQuietly(tempOriginFile);
            log.warn("Failed to retrieve file [{}] with id [{}] of type [{}].", fileName, file.getId(), file.getFileExtension(), e);
            throw new ConversionException(String.format("Failed to convert file [%s] with id [%s] of type [%s].", fileName, file.getId(),
                    file.getFileExtension()), e);
        }

        File tempPdfFile = new File(tempUploadFolderPath + File.separator + createFileName(file));

        performConversion(file, tempOriginFile, tempPdfFile);

        if(skipUploadAndReturnConvertedFile)
        {
            return tempPdfFile;
        }

        try (FileInputStream fis = new FileInputStream(tempPdfFile))
        {
            EcmFile metadata = new EcmFile();
            metadata.setFileType(file.getFileType());
            metadata.setFileLang(file.getFileLang());
            metadata.setFileName(createFileName(file));
            metadata.setCmisRepositoryId(file.getCmisRepositoryId());
            metadata.setFileActiveVersionMimeType(APPLICATION_PDF);

            fileService.upload(new UsernamePasswordAuthenticationToken(username, username), file.getParentObjectType(),
                    file.getParentObjectId(), file.getFolder().getCmisFolderId(),
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

        return null;
    }

    /**
     * Performs the actual conversion of the input file into pdf format. The implementations have to ensure that they
     * will remove the <code>tempOriginFile</code> regardless of the outcome of the conversion. Also, in case of an
     * exception, they have to ensure that they will remove the <code>tempPdfFile</code>.
     *
     * @param file
     *            the file being converted.
     * @param tempOriginFile
     *            the temporary copy on the local storage of the contents of <code>file</code>. It is stored on the file
     *            system in order to avoid keeping a byte array in memory, which would result in huge consumption of
     *            memory.
     * @param tempPdfFile
     *            the temporary copy on the local storage of the result of the conversion.
     * @throws ConversionException
     */
    protected abstract void performConversion(EcmFile file, File tempOriginFile, File tempPdfFile)
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
