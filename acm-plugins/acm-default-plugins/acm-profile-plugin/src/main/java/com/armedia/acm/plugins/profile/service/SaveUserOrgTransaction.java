package com.armedia.acm.plugins.profile.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.profile.model.UserOrg;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 06.11.2014.
 */
public class SaveUserOrgTransaction {

    private MuleClient muleClient;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Transactional
    public UserOrg saveUserOrg(UserOrg userOrgInfo, Authentication authentication) throws MuleException {

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("acmUser", authentication);
        messageProps.put("auditAdapter", getAuditPropertyEntityAdapter());
        MuleMessage received = getMuleClient().send("vm://saveUserOrg.in", userOrgInfo, messageProps);
        UserOrg saved = received.getPayload(UserOrg.class);
        MuleException e = received.getInboundProperty("saveException");

        if ( e != null ) {
            throw e;
        }
        return saved;
    }

    public MuleClient getMuleClient() {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient) {
        this.muleClient = muleClient;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter() {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter) {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
