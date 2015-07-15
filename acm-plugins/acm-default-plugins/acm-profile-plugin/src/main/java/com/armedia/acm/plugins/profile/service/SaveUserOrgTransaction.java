package com.armedia.acm.plugins.profile.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.model.UserOrg;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 06.11.2014.
 */
public class SaveUserOrgTransaction {

    private MuleContextManager muleContextManager;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private UserOrgDao userOrgDao;

    @Transactional
    public UserOrg saveUserOrg(UserOrg userOrgInfo, Authentication authentication) throws MuleException {

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("acmUser", authentication);
        messageProps.put("auditAdapter", getAuditPropertyEntityAdapter());
        messageProps.put("userOrgDao", getUserOrgDao());

        MuleMessage received = getMuleContextManager().send("vm://saveUserOrg.in", userOrgInfo, messageProps);

        UserOrg saved = received.getPayload(UserOrg.class);
        MuleException e = received.getInboundProperty("saveException");

        if ( e != null ) {
            throw e;
        }
        return saved;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter() {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter) {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public UserOrgDao getUserOrgDao()
    {
        return userOrgDao;
    }

    public void setUserOrgDao(UserOrgDao userOrgDao)
    {
        this.userOrgDao = userOrgDao;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }
}
