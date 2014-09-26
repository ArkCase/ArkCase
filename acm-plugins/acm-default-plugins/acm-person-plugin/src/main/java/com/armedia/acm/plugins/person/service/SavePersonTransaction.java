package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.person.model.Person;
import java.util.HashMap;
import java.util.Map;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public class SavePersonTransaction
{
    private MuleClient muleClient;

    @Transactional
    public Person savePerson(
            Person person,
            Authentication authentication)
            throws MuleException
    {
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("acmUser", authentication);
        MuleMessage received = getMuleClient().send("vm://savePerson.in", person, messageProps);
        Person saved = received.getPayload(Person.class);
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
