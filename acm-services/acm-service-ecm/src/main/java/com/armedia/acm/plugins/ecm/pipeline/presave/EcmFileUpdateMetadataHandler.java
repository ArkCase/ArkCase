package com.armedia.acm.plugins.ecm.pipeline.presave;

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

import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.PageCountService;
import com.armedia.acm.plugins.ecm.service.impl.EcmTikaFile;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by sasko.tanaskoski
 */
public class EcmFileUpdateMetadataHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private EcmFileDao ecmFileDao;
    private AcmFolderDao folderDao;
    private PageCountService pageCountService;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("metadata pre save handler called");

        // Writes metadata for updated document into the database
        if (!pipelineContext.getIsAppend())
        {
            if (entity == null)
            {
                throw new PipelineProcessException("ecmFile is null");
            }

            Document cmisDocument = pipelineContext.getCmisDocument();
            if (cmisDocument == null)
            {
                throw new PipelineProcessException("cmisDocument is null");
            }

            entity.setActiveVersionTag(cmisDocument.getVersionLabel());
            entity.setVersionSeriesId(cmisDocument.getPropertyValue("alfcmis:nodeRef"));

            // Sets the versioning of the file
            EcmFileVersion version = new EcmFileVersion();
            version.setCmisObjectId(cmisDocument.getPropertyValue("alfcmis:nodeRef") + ";" + cmisDocument.getVersionLabel());
            version.setVersionTag(cmisDocument.getVersionLabel());
            version.setVersionMimeType(entity.getFileActiveVersionMimeType());
            version.setVersionFileNameExtension(entity.getFileActiveVersionNameExtension());
            long fileSizeBytes = pipelineContext.getMergedFile() != null &&
                    pipelineContext.getMergedFile().length() > 0 ? pipelineContext.getMergedFile().length()
                            : pipelineContext.getFileContents() != null ? pipelineContext.getFileContents().length() : 0;
            version.setFileSizeBytes(fileSizeBytes);

            version.setSearchablePDF(pipelineContext.isSearchablePDF());

            // file metadata
            if (pipelineContext.getDetectedFileMetadata() != null)
            {
                EcmTikaFile etf = pipelineContext.getDetectedFileMetadata();
                etf.stampVersionInfo(version);
            }

            entity.getVersions().add(version);

            try
            {
                int pageCount = getPageCountService().getNumberOfPages(entity.getFileActiveVersionMimeType(),
                        pipelineContext.getFileContents());
                if (pageCount > -1)
                {
                    entity.setPageCount(pageCount);
                }
            }
            catch (IOException e)
            {
                throw new PipelineProcessException(e);
            }

            // Saves updated file metadata into ArkCase database
            EcmFile saved = getEcmFileDao().save(entity);
            pipelineContext.setEcmFile(saved);
        }
        log.debug("metadata pre save handler ended");
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        // rollback not needed, JPA will rollback the database changes.
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public PageCountService getPageCountService()
    {
        return pageCountService;
    }

    public void setPageCountService(PageCountService pageCountService)
    {
        this.pageCountService = pageCountService;
    }
}
