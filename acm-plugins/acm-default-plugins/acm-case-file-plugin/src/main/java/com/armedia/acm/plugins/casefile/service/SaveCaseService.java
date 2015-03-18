package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 8/29/14.
 */
public class SaveCaseService
{
    private CaseFileDao caseFileDao;
    private SaveCaseFileBusinessRule saveRule;
    private CaseFileEventUtility caseFileEventUtility;
    private MuleClient muleClient;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    public CaseFile saveCase(CaseFile in, Authentication auth, String ipAddress) throws MuleException
    {
        boolean newCase = in.getId() == null;
        if ( newCase )
        {
            in.setCreator(auth.getName());

        }

        in.setModified(new Date());
        in.setModifier(auth.getName());

        CaseFile retval = getCaseFileDao().save(in);

        log.info("Saving case: retval is null? " + ( retval == null));

        retval = getSaveRule().applyRules(retval);

        // call Mule flow to create the Alfresco folder
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("acmUser", auth);
        messageProps.put("auditAdapter", getAuditPropertyEntityAdapter());
        MuleMessage received = getMuleClient().send("vm://saveCaseFile.in", retval, messageProps);
        CaseFile saved = received.getPayload(CaseFile.class);
        MuleException e = received.getInboundProperty("saveException");

        if ( e != null )
        {
            throw e;
        }

        if ( newCase )
        {
            getCaseFileEventUtility().raiseEvent(retval, "created", new Date(), ipAddress, auth.getName(), auth);
            getCaseFileEventUtility().raiseEvent(retval, retval.getStatus(), new Date(), ipAddress, auth.getName(), auth);
        }
        else
        {
        	getCaseFileEventUtility().raiseEvent(retval, "updated", new Date(), ipAddress, auth.getName(), auth);
        }
                
        return saved;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public SaveCaseFileBusinessRule getSaveRule()
    {
        return saveRule;
    }

    public void setSaveRule(SaveCaseFileBusinessRule saveRule)
    {
        this.saveRule = saveRule;
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
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
