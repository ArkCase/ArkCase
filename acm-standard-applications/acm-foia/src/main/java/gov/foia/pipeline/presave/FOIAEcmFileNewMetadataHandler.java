package gov.foia.pipeline.presave;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.pipeline.presave.EcmFileNewMetadataHandler;
import com.armedia.acm.plugins.ecm.service.impl.EcmTikaFile;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import gov.foia.model.FOIAEcmFileVersion;
import gov.foia.model.FOIAFile;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class FOIAEcmFileNewMetadataHandler extends EcmFileNewMetadataHandler
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private ArkCaseBeanUtils arkCaseBeanUtils;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {

        log.debug("metadata pre save handler called");

        // Writes metadata for new document uploads into the database
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

            entity.setVersionSeriesId(cmisDocument.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID));
            entity.setActiveVersionTag(cmisDocument.getVersionLabel());

            // Sets the versioning of the file
            FOIAEcmFileVersion version = new FOIAEcmFileVersion();
            version.setCmisObjectId(
                    cmisDocument.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID) + ";" + cmisDocument.getVersionLabel());
            version.setVersionTag(cmisDocument.getVersionLabel());
            version.setVersionMimeType(entity.getFileActiveVersionMimeType());
            version.setVersionFileNameExtension(entity.getFileActiveVersionNameExtension());
            long fileSizeBytes = pipelineContext.getMergedFile() != null &&
                    pipelineContext.getMergedFile().length() > 0 ? pipelineContext.getMergedFile().length()
                            : pipelineContext.getFileContents() != null ? pipelineContext.getFileContents().length() : 0;
            version.setFileSizeBytes(fileSizeBytes);
            version.setReviewStatus("");
            version.setRedactionStatus("");
            version.setFileHash(pipelineContext.getFileHash());
            version.setSearchablePDF(pipelineContext.isSearchablePDF());
            log.debug("SearchablePDF = [{}]", pipelineContext.isSearchablePDF());

            // file metadata
            if (pipelineContext.getDetectedFileMetadata() != null)
            {
                EcmTikaFile etf = pipelineContext.getDetectedFileMetadata();
                etf.stampVersionInfo(version);
                version.setValidFile(etf.isValidFile());
            }

            entity.getVersions().add(version);

            // Determines the folder and container in which the file should be saved
            AcmFolder folder = getFolderDao().findByCmisFolderId(pipelineContext.getCmisFolderId());
            entity.setFolder(folder);
            entity.setContainer(pipelineContext.getContainer());

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

            // Saves new file metadata into ArkCase database
            FOIAFile fileMetadata = new FOIAFile();
            try
            {
                getArkCaseBeanUtils().copyProperties(fileMetadata, entity);
                fileMetadata.setPublicFlag(false);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                log.error("Could not copy properties from EcmFile to FoiaFile");
            }
            FOIAFile saved = (FOIAFile) getEcmFileDao().save(fileMetadata);
            pipelineContext.setEcmFile(saved);
        }
        log.debug("metadata pre save handler ended");
    }

    public ArkCaseBeanUtils getArkCaseBeanUtils()
    {
        return arkCaseBeanUtils;
    }

    public void setArkCaseBeanUtils(ArkCaseBeanUtils arkCaseBeanUtils)
    {
        this.arkCaseBeanUtils = arkCaseBeanUtils;
    }
}
