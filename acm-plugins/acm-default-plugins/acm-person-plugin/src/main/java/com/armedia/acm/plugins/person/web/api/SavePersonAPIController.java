package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.service.PersonEventPublisher;
import com.armedia.acm.plugins.person.service.SavePersonTransaction;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/plugin/person", "/api/latest/plugin/person" })
public class SavePersonAPIController {
    
    private Logger log = LoggerFactory.getLogger(getClass());

    private SavePersonTransaction personTransaction;
    private PersonEventPublisher eventPublisher;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Person AddPerson(
            @RequestBody Person in,
            Authentication auth
    ) throws AcmCreateObjectFailedException
    {
        if ( log.isTraceEnabled() )
        {
            log.trace("Got a Person: " + in +"; person ID: '" + in.getId()+ "'");
            log.trace("person title: " + in.getTitle());
        }

        boolean isInsert = in.getId()== null;

        try
        {
            Person saved = getPersonTransaction().savePerson(in, auth);

          getEventPublisher().publishPersonEvent(saved, auth, isInsert, true);
          
          return saved;

        } catch ( MuleException | TransactionException e)
        {
      
            getEventPublisher().publishPersonEvent(in, auth, isInsert, false);

            throw new AcmCreateObjectFailedException("person", e.getMessage(), e);
        }

    }

    public SavePersonTransaction getPersonTransaction() {
        return personTransaction;
    }

    public void setPersonTransaction(SavePersonTransaction personTransaction) {
        this.personTransaction = personTransaction;
    }

    public PersonEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(PersonEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

   

}
