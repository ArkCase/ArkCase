package com.armedia.acm.plugins.person.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

public class SavePersonTransaction
{
    private MuleContextManager muleContextManager;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private PersonDao personDao;

    @Transactional
    public Person savePerson(
            Person person,
            Authentication authentication)
            throws MuleException
    {
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("acmUser", authentication);
        messageProps.put("auditAdapter", getAuditPropertyEntityAdapter());
        messageProps.put("acmPersonDao", getMuleContextManager());

        MuleMessage received = getMuleContextManager().send("vm://savePerson.in", person, messageProps);

        Person saved = received.getPayload(Person.class);
        MuleException e = received.getInboundProperty("saveException");

        if ( e != null )
        {
            throw e;
        }

        return saved;

    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }
}
