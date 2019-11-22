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

import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FOLDER_TYPE;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.InputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 26, 2018
 *
 */
public class DefaultFolderAndFileConverter implements FolderConverter, FileConverter
{

    /**
     * Logger instance.
     */
    private Logger log = LogManager.getLogger(getClass());

    /**
     * Used to retrieve folder information from the system.
     */
    private AcmFolderService folderService;

    private Map<String, List<FileConverter>> convertersByType;

    private List<String> supportedTypes;
    
    private EcmFileDao ecmFileDao;

    public DefaultFolderAndFileConverter(List<FileConverter> converters)
    {
        convertersByType = new HashMap<>();
        setConverters(converters);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.convertfolder.FolderConverter#convertFolder(java.lang.Long)
     */
    @Override
    public void convertFolder(Long folderId, String username) throws ConversionException
    {
        log.debug("Converting folder with id: [{}].", folderId);
        AcmFolder folder = Optional.ofNullable(folderService.findById(folderId))
                .orElseThrow(() -> new ConversionException(String.format("Could not find folder with id [%s].", folderId)));

        try
        {
            List<AcmObject> folderChildren = folderService.getFolderChildren(folder.getId()).stream()
                    .filter(obj -> obj.getObjectType() != null)
                    .collect(Collectors.toList());

            for (AcmObject obj : folderChildren)
            {
                try
                {
                    String objectType = obj.getObjectType().toUpperCase();
                    // if child object is a folder, convert it's contents
                    if (OBJECT_FOLDER_TYPE.equals(objectType))
                    {
                        convertFolder(obj.getId(), username);
                    }
                    else
                    {
                        if(!EcmFile.class.cast(obj).getFileActiveVersionNameExtension().equals(".pdf"))
                        {
                            obj = checkDuplicateFileName(obj, folderId);
                        }
                        convert(EcmFile.class.cast(obj), username);
                    }
                }
                catch (ConversionException ce)
                {
                    continue;
                }
            }
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
        {
            log.warn("Failed to get children for folder with id: [{}].", folderId, e);
            throw new ConversionException(String.format("Failed to get children for folder with id: [%s].", folderId), e);
        }

    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.convertfolder.FileConverter#getSupportedTypesExtensions()
     */
    @Override
    public List<String> getSupportedTypesExtensions()
    {
        return supportedTypes;
    }

    /**
     * @param username
     * @param file
     * @throws ConversionException
     */
    @Override
    public void convert(EcmFile file, String username) throws ConversionException
    {
        convertFile(file, "", username, false);
    }

    @Override
    public File convert(EcmFile file) throws ConversionException
    {
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
        List<FileConverter> converters = convertersByType.get(file.getFileExtension().toLowerCase());
        if (converters == null)
        {
            return null;
        }

        ConversionException ex = new ConversionException(
                String.format("Error converting file [%s] of type [%s] with version [%s].", file.getFileName(),
                        file.getFileExtension(), file.getActiveVersionTag()));
        for (FileConverter converter : converters)
        {
            try
            {
                log.debug("Using converter of type [{}] to convert file [{}] of type [{}].", converter.getClass().getName(),
                        file.getFileName() + "." + file.getFileExtension(), file.getFileExtension());

                if(skipUploadAndReturnConvertedFile)
                {
                    return converter.convertAndReturnConvertedFile(file, version);
                }
                else
                {
                    converter.convert(file, username);
                }
            }
            catch (ConversionException ce)
            {
                ex.addSuppressed(ce);
            }
        }
        if (ex.getSuppressed().length > 0)
        {
            throw ex;
        }

        return null;
    }

    /**
     * @param converters
     *            the converters to set
     */
    private void setConverters(List<FileConverter> converters)
    {
        for (FileConverter converter : converters)
        {
            List<String> supportedFileExtensions = converter.getSupportedTypesExtensions().stream().map(ext -> ext.toLowerCase())
                    .collect(Collectors.toList());
            for (String fileExtension : supportedFileExtensions)
            {
                List<FileConverter> computedConverters = convertersByType.computeIfAbsent(fileExtension,
                        k -> new ArrayList<>());
                computedConverters.add(converter);
            }
        }
        supportedTypes = Collections.unmodifiableList(new ArrayList<>(convertersByType.keySet()));
    }

    private AcmObject checkDuplicateFileName(AcmObject file, Long folderId)
    {
        String fileName = EcmFile.class.cast(file).getFileName();
        List<EcmFile> pdfFiles = getEcmFileDao().findByFolderId(folderId).stream()
                .filter(obj -> obj.getFileActiveVersionNameExtension().equals(".pdf"))
                .filter(obj -> obj.getFileName().equals(fileName))
                .collect(Collectors.toList());
        if(pdfFiles.size() > 0)
        {
            ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestampName = formatter.format(date);
            EcmFile.class.cast(file).setFileName(fileName + "-" + timestampName);
        }
        return file;
    }
    /**
     * @param folderService
     *            the folderService to set
     */
    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public EcmFileDao getEcmFileDao() 
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao) 
    {
        this.ecmFileDao = ecmFileDao;
    }
}
