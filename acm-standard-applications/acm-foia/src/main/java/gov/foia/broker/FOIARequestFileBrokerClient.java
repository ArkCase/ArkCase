package gov.foia.broker;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.web.api.MDCConstants;
import com.armedia.broker.AcmFileBrokerClient;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;

import javax.jms.JMSException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import gov.foia.service.ResponseFolderCompressorService;

/**
 * Sends FOIA request files to outbound queue
 *
 * @author dame.gjorgjievski
 */
public class FOIARequestFileBrokerClient extends AcmFileBrokerClient
{

    private transient Logger LOG = LogManager.getLogger(getClass());

    private ResponseFolderCompressorService responseFolderCompressorService;
    private CaseFileDao caseFileDao;

    public FOIARequestFileBrokerClient(ActiveMQConnectionFactory connectionFactory, String outboundQueue, String inboundQueue)
    {
        super(connectionFactory, outboundQueue, inboundQueue);
    }

    /**
     * Send request release file to outbound queue
     *
     * @param requestId
     * @throws AcmObjectNotFoundException
     * @throws AcmUserActionFailedException
     * @throws AcmFolderException
     */
    public void sendReleaseFile(Long requestId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {
        CaseFile request = caseFileDao.find(requestId);
        sendReleaseFile(request);
    }

    public void sendReleaseFile(Long requestId, String filePath)
    {
        CaseFile request = caseFileDao.find(requestId);
        sendReleaseFile(request, filePath);
    }

    /**
     * Send request release file to outbound queue
     *
     * @param request
     * @throws AcmObjectNotFoundException
     * @throws AcmUserActionFailedException
     * @throws AcmFolderException
     */
    private void sendReleaseFile(CaseFile request)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {
        AcmFolder responseFolder = responseFolderCompressorService.getResponseFolderService().getResponseFolder(request);
        String filePath = responseFolderCompressorService.getCompressor().getCompressedFolderFilePath(responseFolder);

        sendReleaseFile(request, filePath);
    }

    /**
     * Send request release file to outbound queue
     *
     * @param request
     * @param filePath
     * @throws IOException
     * @throws JMSException
     */
    private void sendReleaseFile(CaseFile request, String filePath)
    {
        // since this code is run via a batch job, there is no authenticated user, so we need to specify the user
        // to be used for CMIS connections. Similar to the requiremnt to 'getAuditPropertyEntityAdapter().setUserId',
        // only this user has to be a real Alfresco user.
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        File file = new File(filePath);
        if (!file.exists())
        {
            LOG.error("Required FOIA request release file was not found on file system " + file);
            return;
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put("requestId", request.getCaseNumber());
        properties.put("fileName", request.getCaseNumber() + ".zip");

        try
        {
            LOG.debug("Sending FOIA request release file " + file + " with properties " + properties);
            sendFile(file, properties);
        }
        catch (JMSException | IOException e)
        {
            LOG.error("Failed to send FOIA request release file", e);
        }
    }

    public void setResponseFolderCompressorService(ResponseFolderCompressorService responseFolderCompressorService)
    {
        this.responseFolderCompressorService = responseFolderCompressorService;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

}
