package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.form.config.xml.PersonItem;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.addressable.model.xml.GeneralPostalAddress;
import com.armedia.acm.plugins.addressable.model.xml.InitiatorContactMethod;
import com.armedia.acm.plugins.addressable.model.xml.InitiatorPostalAddress;
import com.armedia.acm.plugins.addressable.model.xml.PeopleContactMethod;
import com.armedia.acm.plugins.addressable.model.xml.PeoplePostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.ComplaintForm;
import com.armedia.acm.plugins.complaint.model.complaint.Contact;
import com.armedia.acm.plugins.complaint.model.complaint.MainInformation;
import com.armedia.acm.plugins.complaint.model.complaint.xml.InitiatorContact;
import com.armedia.acm.plugins.complaint.model.complaint.xml.InitiatorMainInformation;
import com.armedia.acm.plugins.complaint.model.complaint.xml.PeopleContact;
import com.armedia.acm.plugins.complaint.model.complaint.xml.PeopleMainInformation;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.xml.InitiatorOrganization;
import com.armedia.acm.plugins.person.model.xml.InitiatorPersonAlias;
import com.armedia.acm.plugins.person.model.xml.PeopleOrganization;
import com.armedia.acm.plugins.person.model.xml.PeoplePersonAlias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ComplaintFactory extends FrevvoFormFactory
{
    private static final String ANONYMOUS = "Anonymous";
    private transient final Logger log = LoggerFactory.getLogger(getClass());
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
        retval.setParticipants(asAcmParticipants(formComplaint.getParticipants(), formComplaint.getOwningGroup(), FrevvoFormName.COMPLAINT));

        Calendar cal = Calendar.getInstance();
        cal.setTime(formComplaint.getDate());
        cal.add(Calendar.DATE, 3);

        Date dueDate = cal.getTime();
        retval.setDueDate(dueDate);
        retval.setComplaintType(formComplaint.getCategory());
        retval.setTag(formComplaint.getComplaintTag());
        retval.setFrequency(formComplaint.getFrequency());

        if (formComplaint.getInitiatorId() != null)
        {
            PersonAssociation pa = new PersonAssociation();
            Person initiator = getPersonDao().find(formComplaint.getInitiatorId());

            if(initiator != null)
            {
                pa.setPerson(initiator);
                pa.setPersonType(formComplaint.getInitiatorType());
                retval.setOriginator(pa);
                paArray.add(pa);
            }
        }

        if (formComplaint.getPeople() != null && formComplaint.getPeople().size() > 0)
        {
            for(PersonItem personItem : formComplaint.getPeople())
            {
                Person person = getPersonDao().find(personItem.getId());
                PersonAssociation personAssociation = (personItem.getPersonAssociationId() == null) ? new PersonAssociation() : getPersonAssociationDao().find(
                        personItem.getPersonAssociationId());

                if(person == null)
                    continue;

                personAssociation.setPerson(person);
                personAssociation.setPersonType(personItem.getPersonType());
                paArray.add(personAssociation);
            }

        }
        if(paArray!= null && paArray.size() > 0)
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

            if (complaint.getLocation() != null)
            {
                complaintForm.setLocation(new GeneralPostalAddress(complaint.getLocation()));
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
                } else
                {
                    AcmContainer container = getFileService().getOrCreateContainer(complaint.getObjectType(), complaint.getId());
                    complaintForm.setCmisFolderId(container.getFolder().getCmisFolderId());
                }
            } catch (AcmCreateObjectFailedException | AcmUserActionFailedException e)
            {
                log.error("Unknown CMIS folder for this complaint! " + e.getMessage(), e);
            }


            // Populate participants
            complaintForm.setParticipants(asFrevvoParticipants(complaint.getParticipants()));
            // Populate owning group
            complaintForm.setOwningGroup(asFrevvoGroupParticipant(complaint.getParticipants()));
        } catch (Exception e)
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
     * @param personDao the personDao to set
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
