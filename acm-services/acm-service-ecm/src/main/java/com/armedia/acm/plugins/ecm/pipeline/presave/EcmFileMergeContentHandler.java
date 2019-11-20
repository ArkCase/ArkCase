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

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.utils.EcmFileCamelUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by joseph.mcgrady on 9/28/2015.
 */
public class EcmFileMergeContentHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private EcmFileCamelUtils ecmFileCamelUtils;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        if (entity == null)
        {
            throw new PipelineProcessException("ecmFile is null");
        }

        if (pipelineContext.getIsAppend())
        {
            try
            {
                // Updates the Alfresco content repository with the new merged version of the file
                InputStream mergedFileInputStream = new ByteArrayInputStream(pipelineContext.getMergedFileByteArray());
                Document updatedDocument = ecmFileCamelUtils.updateFile(entity, pipelineContext.getEcmFile(), mergedFileInputStream);
                pipelineContext.setCmisDocument(updatedDocument);
            }
            catch (Exception e)
            {
                log.error("Camel pre save handler failed: {}", e.getMessage(), e);
                throw new PipelineProcessException(e);
            }
        }
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {

    }

    public EcmFileCamelUtils getEcmFileCamelUtils()
    {
        return ecmFileCamelUtils;
    }

    public void setEcmFileCamelUtils(EcmFileCamelUtils ecmFileCamelUtils)
    {
        this.ecmFileCamelUtils = ecmFileCamelUtils;
    }
}
