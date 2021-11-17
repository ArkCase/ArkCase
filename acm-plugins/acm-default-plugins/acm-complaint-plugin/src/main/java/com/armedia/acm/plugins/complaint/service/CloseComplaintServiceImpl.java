package com.armedia.acm.plugins.complaint.service;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.services.pipeline.PipelineManager;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CloseComplaintServiceImpl implements CloseComplaintService {

    private CloseComplaintRequestDao closeComplaintRequestDao;
    private PipelineManager<CloseComplaintRequest, CloseComplaintPipelineContext> pipelineManager;
    private PersonDao personDao;
    private OrganizationDao organizationDao;

    @Override
    @Transactional
    public void save(CloseComplaintRequest form, Authentication auth, String mode) throws Exception {
        CloseComplaintPipelineContext pipelineContext = new CloseComplaintPipelineContext();
        pipelineContext.setAuthentication(auth);
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        pipelineContext.setIpAddress(ipAddress);
        pipelineContext.addProperty("mode", mode);
        pipelineContext.addProperty("closeComplaintStatusFlow", form.isCloseComplaintStatusFlow());

        pipelineManager.executeOperation(form, pipelineContext, () -> {

            CloseComplaintRequest savedRequest = getCloseComplaintRequestDao().save(form);
            pipelineContext.setCloseComplaintRequest(savedRequest);
            return savedRequest;
        });
    }

    @Override
    public void createPersonOrganizationAssociation(CloseComplaintRequest form)

    {

        Person person = getPersonDao().find(form.getReferExternalPersonId());
        Organization organization = getOrganizationDao().find(form.getReferExternalOrganizationId());

        List<PersonOrganizationAssociation> organizationAssociationList = organization.getPersonAssociations();
        List<PersonOrganizationAssociation> personAssociationList = person.getOrganizationAssociations();

        PersonOrganizationAssociation personOrganizationAssociation = new PersonOrganizationAssociation();
        personOrganizationAssociation.setOrganization(organization);
        personOrganizationAssociation.setDefaultOrganization(false);
        personOrganizationAssociation.setPerson(person);
        personOrganizationAssociation.setPersonToOrganizationAssociationType("");
        personOrganizationAssociation.setOrganizationToPersonAssociationType("");
        if (person.getOrganizationAssociations().isEmpty()) {

            personOrganizationAssociation.setDefaultOrganization(true);
        }

        boolean isPersonOrganizationAssociationExists = false;

        for (PersonOrganizationAssociation orgAss : organizationAssociationList) {

            if (orgAss.getOrganization().getId() == organization.getId()) {
                isPersonOrganizationAssociationExists = true;
                break;
            }
        }
        if (!isPersonOrganizationAssociationExists) {
            organization.getPersonAssociations().add(personOrganizationAssociation);

        }
        isPersonOrganizationAssociationExists = false;
        for (PersonOrganizationAssociation orgAss : personAssociationList) {

            if (orgAss.getPerson().getId() == person.getId()) {
                isPersonOrganizationAssociationExists = true;
                break;
            }
        }
        if (!isPersonOrganizationAssociationExists) {
            person.getOrganizationAssociations().add(personOrganizationAssociation);
        }

        getPersonDao().save(person);
        getOrganizationDao().save(organization);
    }

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

    public PipelineManager<CloseComplaintRequest, CloseComplaintPipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<CloseComplaintRequest, CloseComplaintPipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }

    public PersonDao getPersonDao() {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    public OrganizationDao getOrganizationDao() {
        return organizationDao;
    }

    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }
}