package gov.foia.service.dataupdate;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import com.armedia.acm.services.dataupdate.service.AcmDataUpdateService;
import com.armedia.acm.services.dataupdate.service.SolrReindexService;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import gov.foia.model.FOIAPerson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ana Serafimoska <ana.serafimoska@armedia.com> on 6/3/2021
 */
public class CreateFOIAPersonFromExistingUsersExecutor implements AcmDataUpdateExecutor
{
    private transient Logger log = LogManager.getLogger(getClass());
    private SolrReindexService solrReindexService;
    private UserDao userDao;
    private PersonDao personDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public String getUpdateId()
    {
        return "create-foia-persons-from-existing-users-v1";
    }

    @Override
    public void execute()
    {
        auditPropertyEntityAdapter.setUserId(AcmDataUpdateService.DATA_UPDATE_MODIFIER);

        List<AcmUser> acmUsers = getUserDao().findAll();
        for (AcmUser acmUser : acmUsers)
        {
            if (!acmUser.getUserId().equals("OCR_SERVICE") && !acmUser.getUserId().equals("TRANSCRIBE_SERVICE"))
            {
                FOIAPerson existingPerson = (FOIAPerson) getPersonDao().findByLdapUserId(acmUser.getUserId());
                if (existingPerson != null)
                {
                    addOrUpdatePerson(acmUser, existingPerson);
                }
                else
                {
                    FOIAPerson person = new FOIAPerson();
                    addOrUpdatePerson(acmUser, person);
                }
            }
        }
    }

    private void addOrUpdatePerson(AcmUser acmUser, FOIAPerson person)
    {
        person.setLdapUserId(acmUser.getUserId());
        person.setGivenName(acmUser.getFirstName() != null ? acmUser.getFirstName() : "Unknown");
        person.setFamilyName(acmUser.getLastName() != null ? acmUser.getLastName() : "Unknown");
        person.setTitle("-");

        List<ContactMethod> contactMethods = new ArrayList<>();
        ContactMethod contactMethodEmail = new ContactMethod();
        contactMethodEmail.setType("email");
        contactMethodEmail.setSubType("Business");
        contactMethodEmail.setValue(acmUser.getMail());
        contactMethods.add(contactMethodEmail);

        person.setContactMethods(contactMethods);
        person.setDefaultEmail(contactMethodEmail);

        getPersonDao().save(person);

    }

    public SolrReindexService getSolrReindexService()
    {
        return solrReindexService;
    }

    public void setSolrReindexService(SolrReindexService solrReindexService)
    {
        this.solrReindexService = solrReindexService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
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
