package com.armedia.acm.plugins.ecm.utils;

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

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mule.api.MuleMessage;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joseph.mcgrady on 9/17/2015.
 */
public class EcmFileMuleUtils
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private MuleContextManager muleContextManager;

    private EcmFileCamelUtils ecmFileCamelUtils;

    private CmisConfigUtils cmisConfigUtils;

    /**
     * Downloads the contents of the specified document from the repository
     *
     * @param cmisRepositoryId
     *            - cmis repository id of the document to download
     * @param cmisDocumentId
     *            - cmis id of the document to download
     * @return InputStream for the document contents
     */
    @Deprecated
    public InputStream downloadFile(String cmisRepositoryId, String cmisDocumentId)
    {
        return getEcmFileCamelUtils().downloadFile(cmisRepositoryId, cmisDocumentId);
    }

    /**
     * Adds a new file to the repository using the addFile camel flow.
     *
     * @param newEcmFile
     *            - contains metadata for the file whose contents will be added to the repository
     * @param cmisFolderId
     *            - cmis id of the folder in which the new file will be added
     * @param fileInputStream
     *            - binary content data for the new file version
     * @throws AcmUserActionFailedException
     *             if the camel call to save the file to the repository fails
     * @returns Cmis Document object for the new repository document
     */
    @Deprecated
    public Document addFile(EcmFile newEcmFile, String cmisFolderId, InputStream fileInputStream) throws AcmUserActionFailedException
    {
        log.debug("invoking Camel add file route");

        try
        {
            return getEcmFileCamelUtils().addFile(newEcmFile, cmisFolderId, fileInputStream);
        }
        catch (AcmUserActionFailedException e)
        {
            log.error("Could not create document {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_UPLOAD_FILE, EcmFileConstants.OBJECT_FILE_TYPE, null,
                    "Could not create document", e);
        }
    }

    /**
     * Updates the contents of an existing repository item using the Camel updateFile flow.
     *
     * @param newEcmFile
     *            - metadata for the new file which will replace the old version
     * @param originalFile
     *            - metadata for the old file whose contents will be replaced
     * @param fileInputStream
     *            - the binary data content which will be written to the repository
     * @throws ArkCaseFileRepositoryException
     *             if the Camel call to replace the file contents in the repository fails
     * @returns Cmis Document object for the updated repository document
     */
    @Deprecated
    public Document updateFile(EcmFile newEcmFile, EcmFile originalFile, InputStream fileInputStream) throws ArkCaseFileRepositoryException
    {
        return ecmFileCamelUtils.updateFile(newEcmFile, originalFile, fileInputStream);
    }

    /**
     * Removes a file from the Alfresco content repository
     *
     * @deprecated
     *             This method is no longer acceptable
     *             Use {@link EcmFileCamelUtils#deleteFile(EcmFile, String)} instead.
     *
     * @param ecmFile
     *            - metadata for the file to delete
     * @param cmisFileId
     *            - cmis id associated with the document which will be removed
     * @throws Exception
     *             if the mule call to delete the document from the repository fails
     */
    @Deprecated
    public void deleteFile(EcmFile ecmFile, String cmisFileId) throws Exception
    {
        // Invokes the Camel route to delete the file contents from the repository
        log.debug("Invoking delete Camel route to perform roll back on file upload for cmis id: [{}] using [{}]", cmisFileId,
                ArkCaseCMISActions.DELETE_DOCUMENT.getQueueName());
        getEcmFileCamelUtils().deleteFile(ecmFile, cmisFileId);
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public CmisConfigUtils getCmisConfigUtils()
    {
        return cmisConfigUtils;
    }

    public void setCmisConfigUtils(CmisConfigUtils cmisConfigUtils)
    {
        this.cmisConfigUtils = cmisConfigUtils;
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
