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


import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.form.config.xml.PersonItem;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.addressable.model.xml.GeneralPostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.ComplaintForm;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ComplaintFactory extends FrevvoFormFactory
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private PersonDao personDao;
    private PersonAssociationDao personAssociationDao;
    private EcmFileService fileService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    public Complaint asAcmComplaint(ComplaintForm formComplaint)
    {
        Complaint retval = new Complaint();
        List<PersonAssociation> paArray = new ArrayList<>();

        retval.setComplaintId(formComplaint.getComplaintId());
        retval.setComplaintNumber(formComplaint.getComplaintNumber());

        retval.setCreator(getAuditPropertyEntityAdapter().getUserId());
        retval.setModifier(getAuditPropertyEntityAdapter().getUserId());

        retval.setDetails(formComplaint.getComplaintDescription());
        retval.setIncidentDate(formComplaint.getDate());
        retval.setPriority(formComplaint.getPriority());
        retval.setComplaintTitle(formComplaint.getComplaintTitle());
        retval.setParticipants(
                asAcmParticipants(formComplaint.getParticipants(), formComplaint.getOwningGroup(), FrevvoFormName.COMPLAINT));

        Calendar cal = Calendar.getInstance();
        cal.setTime(formComplaint.getDate());
        cal.add(Calendar.DATE, 3);

        Date dueDate = cal.getTime();
        retval.setDueDate(dueDate);
        retval.setComplaintType(formComplaint.getCategory());
        retval.setTag(formComplaint.getComplaintTag());
        retval.setFrequency(formComplaint.getFrequency());

        if (formComplaint.getLocation() != null)
        {
            List<PostalAddress> addresses = new ArrayList<PostalAddress>();
            addresses.add(formComplaint.getLocation().returnBase());
            retval.setAddresses(addresses);
        }

        if (formComplaint.getInitiatorId() != null)

        {
            PersonAssociation pa = new PersonAssociation();
            Person initiator = getPersonDao().find(formComplaint.getInitiatorId());

            if (initiator != null)
            {
                pa.setPerson(initiator);
                pa.setPersonType(formComplaint.getInitiatorType());
                retval.setOriginator(pa);
                paArray.add(pa);
            }
        }

        if (formComplaint.getPeople() != null && formComplaint.getPeople().size() > 0)
        {
            for (PersonItem personItem : formComplaint.getPeople())
            {
                Person person = getPersonDao().find(personItem.getId());
                PersonAssociation personAssociation = (personItem.getPersonAssociationId() == null) ? new PersonAssociation()
                        : getPersonAssociationDao().find(personItem.getPersonAssociationId());

                if (person == null)
                    continue;

                personAssociation.setPerson(person);
                personAssociation.setPersonType(personItem.getPersonType());
                paArray.add(personAssociation);
            }

        }
        if (paArray != null && paArray.size() > 0)
        {
            retval.setPersonAssociations(paArray);
        }
        return retval;
    }

    public ComplaintForm asFrevvoComplaint(Complaint complaint, ComplaintForm complaintForm)
    {
        try
        {
            if (complaintForm == null)
            {
                complaintForm = new ComplaintForm();
            }

            complaintForm.setComplaintId(complaint.getComplaintId());
            complaintForm.setComplaintNumber(complaint.getComplaintNumber());
            complaintForm.setComplaintTitle(complaint.getComplaintTitle());
            complaintForm.setCategory(complaint.getComplaintType());
            complaintForm.setComplaintDescription(complaint.getDetails());
            complaintForm.setPriority(complaint.getPriority());
            complaintForm.setDate(complaint.getCreated());
            complaintForm.setComplaintTag(complaint.getTag());
            complaintForm.setFrequency(complaint.getFrequency());
            if (complaint.getDefaultAddress() != null)
            {
                complaintForm.setLocation(new GeneralPostalAddress(complaint.getDefaultAddress()));
            }

            if (complaint.getOriginator() != null && complaint.getOriginator().getPerson() != null)
            {
                complaintForm.setInitiatorId(complaint.getOriginator().getPerson().getId());
                complaintForm.setInitiatorFullName(complaint.getOriginator().getPerson().getFullName());
                complaintForm.setInitiatorType(complaint.getOriginator().getPersonType());
            }

            if (complaint.getPersonAssociations() != null && complaint.getPersonAssociations().size() > 0)
            {
                List<PersonItem> people = new ArrayList<>();
                for (PersonAssociation personAssociation : complaint.getPersonAssociations())
                {
                    if (personAssociation.getPerson() != null && !personAssociation.getPersonType().equals("Initiator"))
                    {
                        PersonItem personItem = new PersonItem();

                        personItem.setId(personAssociation.getPerson().getId());
                        personItem.setValue(personAssociation.getPerson().getFullName());
                        personItem.setPersonType(personAssociation.getPersonType());
                        personItem.setPersonAssociationId(personAssociation.getId());

                        people.add(personItem);
                    }
                }
                complaintForm.setPeople(people);
            }

            try
            {
                // see if the complaint has its container... sometimes it doesn't
                if (complaint.getContainer() != null)
                {
                    complaintForm.setCmisFolderId(complaint.getContainer().getFolder().getCmisFolderId());
                }
                else
                {
                    AcmContainer container = getFileService().getOrCreateContainer(complaint.getObjectType(), complaint.getId());
                    complaintForm.setCmisFolderId(container.getFolder().getCmisFolderId());
                }
            }
            catch (AcmCreateObjectFailedException | AcmUserActionFailedException e)
            {
                log.error("Unknown CMIS folder for this complaint! " + e.getMessage(), e);
            }

            // Populate participants
            complaintForm.setParticipants(asFrevvoParticipants(complaint.getParticipants()));
            // Populate owning group
            complaintForm.setOwningGroup(asFrevvoGroupParticipant(complaint.getParticipants()));
        }
        catch (Exception e)
        {
            log.error("Cannot convert Object to Frevvo form.", e);
        }

        return complaintForm;
    }

    /**
     * @return the personDao
     */
    public PersonDao getPersonDao()
    {
        return personDao;
    }

    /**
     * @param personDao
     *            the personDao to set
     */
    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
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
