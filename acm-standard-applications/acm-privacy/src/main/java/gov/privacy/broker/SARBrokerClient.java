package gov.privacy.broker;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.web.api.MDCConstants;
import com.armedia.broker.AcmObjectBrokerClient;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

import gov.privacy.model.PortalSubjectAccessRequest;
import gov.privacy.service.PortalCreateRequestService;

/**
 * Subject Access Request message consumer intercepts Subject Access Request objects sent from external portal
 *
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 */
public class SARBrokerClient extends AcmObjectBrokerClient<PortalSubjectAccessRequest>
{

    private static final Logger LOG = LogManager.getLogger(SARBrokerClient.class);
    private PortalCreateRequestService createRequestService;
    @Value("${external.integration.enable}")
    private boolean externalEnable;
    @Value("${external.integration.userId}")
    private String externalUserId;

    public SARBrokerClient(ActiveMQConnectionFactory connectionFactory, String outboundQueue, String inboundQueue)
    {
        super(connectionFactory, outboundQueue, inboundQueue);
        setUpHandler();
        start();
    }

    /**
     * SetUp object handler
     */
    private void setUpHandler()
    {

        setHandler(entity -> {
            LOG.debug("Received external Subject Access Request ", entity.toString());

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
                    if (entity.getUserId() == null)
                    {
                        entity.setUserId(getExternalUserId());
                    }
                    getCreateRequestService().createSAR(entity);
                    result = true;
                }
                else
                {
                    LOG.error("Not allowed external access");
                }
            }
            catch (PipelineProcessException | AcmUserActionFailedException | AcmCreateObjectFailedException e)
            {
                LOG.error("Error occurred while saving external Subject Access Request ", e);
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
