package gov.privacy.pipeline.presave;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.PageCountService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Date;

import gov.privacy.model.SAREcmFileVersion;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SAREcmFileMergedMetadataHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private EcmFileDao ecmFileDao;
    private AcmFolderDao folderDao;
    private PageCountService pageCountService;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        // This handler only executes when the file was merged with a pre-existing document
        if (pipelineContext.getIsAppend())
        {

            // The new content is merged into an existing document, so the old document metadata is returned
            EcmFile oldFile = pipelineContext.getEcmFile();
            if (oldFile == null)
            {
                throw new PipelineProcessException("oldFile is null");
            }

            Document cmisDocument = pipelineContext.getCmisDocument();
            if (cmisDocument == null)
            {
                throw new PipelineProcessException("cmisDocument is null");
            }

            // Updates the versioning of the file
            oldFile.setVersionSeriesId(cmisDocument.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID));
            oldFile.setActiveVersionTag(cmisDocument.getVersionLabel());
            SAREcmFileVersion version = new SAREcmFileVersion();
            version.setCmisObjectId(
                    cmisDocument.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID) + ";" + cmisDocument.getVersionLabel());
            version.setVersionTag(cmisDocument.getVersionLabel());
            version.setVersionMimeType(oldFile.getFileActiveVersionMimeType());
            version.setVersionFileNameExtension(oldFile.getFileActiveVersionNameExtension());
            version.setReviewStatus("");
            version.setRedactionStatus("");
            oldFile.getVersions().add(version);
            oldFile.setModified(new Date());
            try
            {
                int pageCount = getPageCountService().getNumberOfPages(entity.getFileActiveVersionMimeType(),
                        pipelineContext.getMergedFile());
                if (pageCount > -1)
                {
                    oldFile.setPageCount(pageCount);
                }
            }
            catch (IOException e)
            {
                throw new PipelineProcessException(e);
            }

            // Updates the database with the version changes
            EcmFile savedFile = ecmFileDao.save(oldFile);

            // The pipeline will output the updated metadata for the merged file
            pipelineContext.setEcmFile(savedFile);
        }
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
