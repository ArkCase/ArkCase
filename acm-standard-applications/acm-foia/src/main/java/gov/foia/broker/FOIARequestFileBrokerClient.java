package gov.foia.broker;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.web.api.MDCConstants;
import com.armedia.broker.AcmFileBrokerClient;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private transient Logger LOG = LoggerFactory.getLogger(getClass());

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

    /**
     * Send request release file to outbound queue
     *
     * @param request
     * @throws AcmObjectNotFoundException
     * @throws AcmUserActionFailedException
     * @throws AcmFolderException
     * @throws IOException
     * @throws JMSException
     */
    private void sendReleaseFile(CaseFile request)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {

        // since this code is run via a batch job, there is no authenticated user, so we need to specify the user
        // to be used for CMIS connections. Similar to the requiremnt to 'getAuditPropertyEntityAdapter().setUserId',
        // only this user has to be a real Alfresco user.
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        AcmFolder responseFolder = responseFolderCompressorService.getResponseFolderService().getResponseFolder(request);
        String filePath = responseFolderCompressorService.getCompressor().getCompressedFolderFilePath(responseFolder);
        File file = new File(filePath);
        if (!file.exists())
        {
            LOG.error("Required FOIA request release file was not found on file system " + file);
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
