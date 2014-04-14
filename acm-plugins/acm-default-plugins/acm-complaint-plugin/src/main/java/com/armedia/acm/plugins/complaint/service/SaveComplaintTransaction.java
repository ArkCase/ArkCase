package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.model.Complaint;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Implement transactional responsibilities for the SaveComplaintController.
 *
 * JPA does all database writes at commit time.  Therefore, if the transaction demarcation was in the controller,
 * exceptions would not be raised until after the controller method returns; i.e. the exception message goes write
 * to the browser.  Also, separating transaction management (in this class) and exception handling (in the
 * controller) is a good idea in general.
 */
public class SaveComplaintTransaction
{
    private MuleClient muleClient;

    @Transactional
    public Complaint saveComplaint(
            Complaint complaint,
            Authentication authentication)
            throws MuleException
    {
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("methodToCall", "save");
        messageProps.put("acmUser", authentication);
        MuleMessage received = getMuleClient().send("vm://saveComplaint.in", complaint, messageProps);
        Complaint saved = received.getPayload(Complaint.class);
        MuleException e = received.getInboundProperty("saveException");

        if ( e != null )
        {
            throw e;
        }

        return saved;

    }



    public MuleClient getMuleClient()
    {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
    }
}
