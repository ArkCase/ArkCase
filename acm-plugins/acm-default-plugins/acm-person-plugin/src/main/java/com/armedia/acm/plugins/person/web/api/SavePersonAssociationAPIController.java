package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationEventPublisher;
import com.armedia.acm.plugins.person.service.SavePersonAssociationTransaction;
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
@RequestMapping({ "/api/v1/plugin/personAssociation", "/api/latest/plugin/personAssociation" })
public class SavePersonAssociationAPIController {
    
    private Logger log = LoggerFactory.getLogger(getClass());

    private SavePersonAssociationTransaction personAssociationTransaction;
    private PersonAssociationEventPublisher personAssociationEventPublisher;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonAssociation addPersonAssociation(
            @RequestBody PersonAssociation in,
            Authentication auth
    ) throws AcmCreateObjectFailedException
    {
        if ( log.isTraceEnabled() )
        {
            log.trace("Got a personAssociation: " + in +"; person ID: '" + in.getId()+ "'");
            log.trace("personAssociation parentType: " + in.getParentType());
        }

        boolean isInsert = in.getId()== null;

        try
        {
            PersonAssociation saved = getPersonAssociationTransaction().savePersonAsssociation(in, auth);

          getPersonAssociationEventPublisher().publishPersonAssociationEvent(saved, auth, isInsert, true);
          
          return saved;

        } catch ( MuleException | TransactionException e)
        {
      
            getPersonAssociationEventPublisher().publishPersonAssociationEvent(in, auth, isInsert, false); 

            throw new AcmCreateObjectFailedException("personAssociation", e.getMessage(), e);
        }

    }

    public SavePersonAssociationTransaction getPersonAssociationTransaction() {
        return personAssociationTransaction;
    }

    public void setPersonAssociationTransaction(SavePersonAssociationTransaction personAssociationTransaction) {
        this.personAssociationTransaction = personAssociationTransaction;
    }

    public PersonAssociationEventPublisher getPersonAssociationEventPublisher() {
        return personAssociationEventPublisher;
    }

    public void setPersonAssociationEventPublisher(PersonAssociationEventPublisher personAssociationEventPublisher) {
        this.personAssociationEventPublisher = personAssociationEventPublisher;
    }


       

   

}
