package com.armedia.acm.plugins.person.web.api;

/*-
 * #%L
 * ACM Default Plugin: Person
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.service.PersonEventPublisher;
import com.armedia.acm.plugins.person.service.SavePersonTransaction;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;

import org.mule.api.MuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
public class SavePersonAPIController
{

    private Logger log = LogManager.getLogger(getClass());

    private SavePersonTransaction personTransaction;
    private PersonEventPublisher eventPublisher;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public Person addPerson(@RequestBody Person in, Authentication auth) throws AcmCreateObjectFailedException
    {

        log.trace("Got a Person: {}; person ID: '{}'", in, in.getId());
        log.trace("person title: {}", in.getTitle());

        boolean isInsert = in.getId() == null;

        try
        {
            Person saved = getPersonTransaction().savePerson(in, auth);

            getEventPublisher().publishPersonUpsertEvents(saved, in, isInsert, true);

            return saved;

        }
        catch (MuleException | TransactionException e)
        {

            getEventPublisher().publishPersonUpsertEvents(in, in, isInsert, false);

            throw new AcmCreateObjectFailedException("person", e.getMessage(), e);
        }

    }

    public SavePersonTransaction getPersonTransaction()
    {
        return personTransaction;
    }

    public void setPersonTransaction(SavePersonTransaction personTransaction)
    {
        this.personTransaction = personTransaction;
    }

    public PersonEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(PersonEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

}
