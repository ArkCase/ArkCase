package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ana Serafimoska <ana.serafimoska@armedia.com> on 5/20/2021
 */
public class CreatePersonFromExistingUsersExecutor implements AcmDataUpdateExecutor
{
    private transient Logger log = LogManager.getLogger(getClass());
    private SolrReindexService solrReindexService;
    private UserDao userDao;
    private PersonDao personDao;

    @Override
    public String getUpdateId()
    {
        return "create-persons-from-existing-users-v1";
    }

    @Override
    public void execute()
    {

        List<AcmUser> acmUsers = getUserDao().findAll();
        Person person = new Person();
        for (AcmUser acmUser : acmUsers)
        {
            if (!acmUser.getUserId().equals("OCR_SERVICE") && !acmUser.getUserId().equals("TRANSCRIBE_SERVICE"))
            {
                person.setLdapUserId(acmUser.getUserId());
                person.setGivenName(acmUser.getFirstName());
                person.setFamilyName(acmUser.getLastName());

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
        }
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
}
