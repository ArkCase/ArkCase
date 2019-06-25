package com.armedia.acm.plugins.ecm.service;

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

import com.armedia.acm.plugins.ecm.model.EcmFileUploaderConfig;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.impl.FileChunkServiceImpl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileFilter;
import java.time.LocalDate;

public class RemoveExpiredFilesService
{
    private final Logger LOG = LogManager.getLogger(getClass());
    private FileChunkServiceImpl fileChunkService;
    private EcmFileService ecmFileService;
    private EcmFileUploaderConfig ecmFileUploaderConfig;

    public void deleteExpiredFiles()
    {
        String dirPath = System.getProperty("java.io.tmpdir");
        String uniqueArkCaseHashFileIdentifier = ecmFileUploaderConfig.getUniqueHashFileIdentifier();
        File directory = new File(dirPath);
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);

        LOG.debug("Mark files that should be deleted.");
        FileFilter filter = file -> {
            if (!(file.lastModified() < weekAgo.toEpochDay()))
            {
                return false;
            }
            return file.getName().contains(uniqueArkCaseHashFileIdentifier);
        };

        File[] files = directory.listFiles(filter);
        LOG.debug("Found {} files to delete.", files.length);
        int deletedFiles = 0;
        for (File file : files)
        {
            if (file.delete())
            {
                deletedFiles++;
            }
            else
            {
                LOG.warn("The file {} could not be deleted.", file.getName());
            }
        }
        LOG.info("{} files have been deleted.", deletedFiles);
    }

    public FileChunkServiceImpl getFileChunkService()
    {
        return fileChunkService;
    }

    public void setFileChunkService(FileChunkServiceImpl fileChunkService)
    {
        this.fileChunkService = fileChunkService;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public EcmFileUploaderConfig getEcmFileUploaderConfig()
    {
        return ecmFileUploaderConfig;
    }

    public void setEcmFileUploaderConfig(EcmFileUploaderConfig ecmFileUploaderConfig)
    {
        this.ecmFileUploaderConfig = ecmFileUploaderConfig;
    }
}
