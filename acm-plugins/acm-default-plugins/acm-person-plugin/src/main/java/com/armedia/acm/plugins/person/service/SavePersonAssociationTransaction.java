package com.armedia.acm.plugins.person.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import java.util.HashMap;
import java.util.Map;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public class SavePersonAssociationTransaction
{
    private MuleClient muleClient;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Transactional
    public PersonAssociation savePersonAsssociation(
            PersonAssociation personAssociation,
            Authentication authentication)
            throws MuleException
    {
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("acmUser", authentication);
        messageProps.put("auditAdapter", getAuditPropertyEntityAdapter());
        MuleMessage received = getMuleClient().send("vm://savePersonAssociation.in", personAssociation, messageProps);
        PersonAssociation saved = received.getPayload(PersonAssociation.class);
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

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
