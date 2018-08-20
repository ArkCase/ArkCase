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
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

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
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Used to retrieve folder information from the system.
     */
    private AcmFolderService folderService;

    private Map<String, List<FileConverter>> convertersByType;

    private List<String> supportedTypes;

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
    public void convertFolder(Long folderId, Authentication auth) throws ConversionException
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
                        convertFolder(obj.getId(), auth);
                    }
                    else
                    {
                        convert(EcmFile.class.cast(obj), auth);
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
     * @param auth
     * @param id
     * @throws ConversionException
     */
    @Override
    public void convert(EcmFile file, Authentication auth) throws ConversionException
    {
        List<FileConverter> converters = convertersByType.get(file.getFileExtension().toLowerCase());
        if (converters == null)
        {
            return;
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
                converter.convert(file, auth);
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

    /**
     * @param folderService
     *            the folderService to set
     */
    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

}
