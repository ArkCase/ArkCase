package gov.foia.broker;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.web.api.MDCConstants;
import com.armedia.broker.AcmObjectBrokerClient;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

import gov.foia.model.PortalFOIARequest;
import gov.foia.service.PortalCreateRequestService;

/**
 * FOIA Request message consumer intercepts FOIA request objects sent from external portal
 *
 * @author dame.gjorgjievski
 */
public class FOIARequestBrokerClient extends AcmObjectBrokerClient<PortalFOIARequest>
{

    private static final Logger LOG = LoggerFactory.getLogger(FOIARequestBrokerClient.class);
    private PortalCreateRequestService createRequestService;
    private boolean externalEnable;
    private String externalUserId;

    public FOIARequestBrokerClient(ActiveMQConnectionFactory connectionFactory, String outboundQueue, String inboundQueue)
    {
        super(connectionFactory, outboundQueue, inboundQueue, PortalFOIARequest.class);
        setUpHandler();
        start();
    }

    /**
     * SetUp object handler
     */
    private void setUpHandler()
    {

        setHandler(entity -> {
            LOG.debug("Received external FOIA request ", entity.toString());

            // since this code is run via a batch job, there is no authenticated user, so we need to specify the user
            // to be used for CMIS connections. Similar to the requirement to
            // 'getAuditPropertyEntityAdapter().setUserId',
            // only this user has to be a real Alfresco user.
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

            boolean result = false;
            try
            {

                if (isExternalEnable())
                {
                    entity.setUserId(getExternalUserId());
                    getCreateRequestService().createFOIARequest(entity);
                    result = true;
                }
                else
                {
                    LOG.error("Not allowed external access");
                }
            }
            catch (PipelineProcessException | AcmUserActionFailedException | AcmCreateObjectFailedException e)
            {
                LOG.error("Error occurred while saving external FOIA request ", e);
            }
            return result;
        });
    }

    public PortalCreateRequestService getCreateRequestService()
    {
        return createRequestService;
    }

    public void setCreateRequestService(PortalCreateRequestService createRequestService)
    {
        this.createRequestService = createRequestService;
    }

    /**
     * @return the externalEnable
     */
    public boolean isExternalEnable()
    {
        return externalEnable;
    }

    /**
     * @param externalEnable
     *            the externalEnable to set
     */
    public void setExternalEnable(boolean externalEnable)
    {
        this.externalEnable = externalEnable;
    }

    /**
     * @return the externalUserId
     */
    public String getExternalUserId()
    {
        return externalUserId;
    }

    /**
     * @param externalUserId
     *            the externalUserId to set
     */
    public void setExternalUserId(String externalUserId)
    {
        this.externalUserId = externalUserId;
    }

}