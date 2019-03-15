package com.armedia.acm.plugins.ecm.job;

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

import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.impl.FileChunkServiceImpl;
import com.armedia.acm.scheduler.AcmSchedulableBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.time.LocalDate;

public class RemoveExpiredFilesJob implements AcmSchedulableBean
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private FileChunkServiceImpl fileChunkService;
    private EcmFileService ecmFileService;
    private EcmFileConfig ecmFileConfig;

    public void deleteExpiredFiles()
    {
        String dirPath = System.getProperty("java.io.tmpdir");
        String uniqueArkCaseHashFileIdentifier = ecmFileConfig.getUniqueHashFileIdentifier();
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

    @Override
    public void executeTask()
    {
        deleteExpiredFiles();
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

    public EcmFileConfig getEcmFileConfig()
    {
        return ecmFileConfig;
    }

    public void setEcmFileConfig(EcmFileConfig ecmFileConfig)
    {
        this.ecmFileConfig = ecmFileConfig;
    }
}
