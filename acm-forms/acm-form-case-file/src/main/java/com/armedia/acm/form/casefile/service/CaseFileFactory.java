/**
 * 
 */
package com.armedia.acm.form.casefile.service;

import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.xml.InitiatorPerson;
import com.armedia.acm.plugins.person.model.xml.PeoplePerson;
import com.armedia.acm.service.history.dao.AcmHistoryDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileFactory extends FrevvoFormFactory
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public static final String PERSON_TYPE = "Subject";
    public static final String PERSON_IDENTIFICATION_EMPLOYEE_ID = "EMPLOYEE_ID";
    public static final String PERSON_IDENTIFICATION_SSN = "SSN";
    public static final String OBJECT_TYPE_POSTAL_ADDRESS = "POSTAL_ADDRESS";
    public static final String OBJECT_TYPE_ORGANIZATION = "ORGANIZATION";

    private ObjectAssociationDao objectAssociationDao;
    private EcmFileDao ecmFileDao;
    private AcmHistoryDao acmHistoryDao;
    private EcmFileService ecmFileService;

    public CaseFile asAcmCaseFile(CaseFileForm form, CaseFile caseFile)
    {
        if (caseFile == null)
        {
            caseFile = new CaseFile();
        }

        caseFile.setTitle(form.getCaseTitle());
        caseFile.setCaseType(form.getCaseType());
        caseFile.setDetails(form.getCaseDescription());
        caseFile.setParticipants(
                getParticipants(caseFile.getParticipants(), form.getParticipants(), form.getOwningGroup(), caseFile.getObjectType()));

        if (form.getInitiator() != null)
        {
            PersonAssociation personAssociation = caseFile.getPersonAssociations().stream()
                    .filter(initiatorPersonAssociation -> "Initiator".equalsIgnoreCase(initiatorPersonAssociation.getPersonType()))
                    .findFirst().get();
            if (personAssociation == null)
            {
                personAssociation = new PersonAssociation();
            }
            Person person = form.getInitiator();
            if (person.getId() != null)
            {
                // Update Person Association
                personAssociation.getPerson().setTitle(person.getTitle());
                personAssociation.getPerson().setGivenName(person.getGivenName());
                personAssociation.getPerson().setFamilyName(person.getFamilyName());
                personAssociation.setPersonType(((InitiatorPerson) person).getType());

                saveCommunicationDevice(personAssociation, person);
                saveOrganizationInformation(personAssociation, person);
                saveLocationInformation(personAssociation, person);

            } else
            {
                // Add Person Association
                personAssociation = new PersonAssociation();
                personAssociation.setPerson(person.returnBase());
                personAssociation.setPersonType(((InitiatorPerson) person).getType());
            }

            caseFile.setOriginator(personAssociation);
        }

        if (form.getPeople() != null)
        {
            List<PersonAssociation> paArray = caseFile.getPersonAssociations();
            if (paArray == null)
            {
                paArray = new ArrayList<>();
            }

            for (Person person : form.getPeople())
            {
                boolean personFound = false;
                if (person.getId() != null)
                {
                    for (PersonAssociation personAssociation : paArray)
                    {
                        if (!personAssociation.getPersonType().equals("Initiator")
                                && person.getId().equals(personAssociation.getPerson().getId()))
                        {
                            // Update Person Association
                            personAssociation.getPerson().setTitle(person.getTitle());
                            personAssociation.getPerson().setGivenName(person.getGivenName());
                            personAssociation.getPerson().setFamilyName(person.getFamilyName());
                            personAssociation.setPersonType(((PeoplePerson) person).getType());

                            saveCommunicationDevice(personAssociation, person);
                            saveOrganizationInformation(personAssociation, person);
                            saveLocationInformation(personAssociation, person);

                            personFound = true;
                            break;
                        }
                    }
                }
                // Add Person Association
                if (!personFound)
                {
                    PersonAssociation pa = new PersonAssociation();
                    pa.setPerson(person.returnBase());
                    pa.setPersonType(((PeoplePerson) person).getType());
                    paArray.add(pa);
                }
            }

            caseFile.setPersonAssociations(paArray);
        }

        return caseFile;
    }

    private void saveLocationInformation(PersonAssociation personAssociation, Person formPerson)
    {
        List<PostalAddress> addresses = new ArrayList<PostalAddress>();
        if (personAssociation.getPerson().getAddresses() != null)
        {
            addresses = personAssociation.getPerson().getAddresses();
        }
        for (PostalAddress formAddress : formPerson.getAddresses())
        {
            boolean found = false;
            for (PostalAddress existingAddress : addresses)
            {
                if (existingAddress.getId().equals(formAddress.getId()))
                {
                    existingAddress.setType(formAddress.getType());
                    existingAddress.setStreetAddress(formAddress.getStreetAddress());
                    existingAddress.setCity(formAddress.getCity());
                    existingAddress.setState(formAddress.getState());
                    existingAddress.setZip(formAddress.getZip());
                    existingAddress.setCreated(formAddress.getCreated());
                    existingAddress.setCreator(formAddress.getCreator());
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                PostalAddress postalAddress = new PostalAddress();
                postalAddress.setType(formAddress.getType());
                postalAddress.setStreetAddress(formAddress.getStreetAddress());
                postalAddress.setCity(formAddress.getCity());
                postalAddress.setState(formAddress.getState());
                postalAddress.setZip(formAddress.getZip());
                postalAddress.setCreated(formAddress.getCreated());
                postalAddress.setCreator(formAddress.getCreator());
                addresses.add(postalAddress);
            }
        }
        personAssociation.getPerson().setAddresses(addresses);
    }

    private void saveCommunicationDevice(PersonAssociation personAssociation, Person formPerson)
    {
        List<ContactMethod> contactMethods = new ArrayList<ContactMethod>();
        if (personAssociation.getPerson().getContactMethods() != null)
        {
            contactMethods = personAssociation.getPerson().getContactMethods();
        }
        for (ContactMethod formContacts : formPerson.getContactMethods())
        {
            boolean found = false;
            for (ContactMethod existingContact : contactMethods)
            {
                if (existingContact.getId().equals(formContacts.getId()))
                {
                    existingContact.setType(formContacts.getType());
                    existingContact.setValue(formContacts.getValue());
                    existingContact.setCreated(formContacts.getCreated());
                    existingContact.setCreator(formContacts.getCreator());
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                ContactMethod contactMethod = new ContactMethod();
                contactMethod.setType(formContacts.getType());
                contactMethod.setValue(formContacts.getValue());
                contactMethod.setCreated(formContacts.getCreated());
                contactMethod.setCreator(formContacts.getCreator());
                contactMethods.add(contactMethod);
            }
        }
        personAssociation.getPerson().setContactMethods(contactMethods);
    }

    private void saveOrganizationInformation(PersonAssociation personAssociation, Person formPerson)
    {
        List<Organization> organizations = new ArrayList<Organization>();
        if (personAssociation.getPerson().getOrganizations() != null)
        {
            organizations = personAssociation.getPerson().getOrganizations();
        }
        for (Organization formOrganizations : formPerson.getOrganizations())
        {
            boolean found = false;
            for (Organization existingOrganization : organizations)
            {
                if (existingOrganization.getOrganizationId().equals(formOrganizations.getOrganizationId()))
                {
                    existingOrganization.setOrganizationType(formOrganizations.getOrganizationType());
                    existingOrganization.setOrganizationValue(formOrganizations.getOrganizationValue());
                    existingOrganization.setCreated(formOrganizations.getCreated());
                    existingOrganization.setCreator(formOrganizations.getCreator());
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                Organization organization = new Organization();
                organization.setOrganizationType(formOrganizations.getOrganizationType());
                organization.setOrganizationValue(formOrganizations.getOrganizationValue());
                organization.setCreated(formOrganizations.getCreated());
                organization.setCreator(formOrganizations.getCreator());
                organizations.add(organization);
            }
        }
        personAssociation.getPerson().setOrganizations(organizations);
    }

    public CaseFileForm asFrevvoCaseFile(CaseFile caseFile, CaseFileForm form, FrevvoFormAbstractService formService)
    {
        try
        {
            if (form == null)
            {
                form = new CaseFileForm();
            }

            if (caseFile != null)
            {
                form.setId(caseFile.getId());
                form.setCaseTitle(caseFile.getTitle());
                form.setCaseType(caseFile.getCaseType());
                form.setCaseNumber(caseFile.getCaseNumber());
                form.setCaseDescription(caseFile.getDetails());
                String cmisFolderId = formService.findFolderIdForAttachments(caseFile.getContainer(), caseFile.getObjectType(),
                        caseFile.getId());
                form.setCmisFolderId(cmisFolderId);
                form.setParticipants(asFrevvoParticipants(caseFile.getParticipants()));
                form.setOwningGroup(asFrevvoGroupParticipant(caseFile.getParticipants()));

                if (caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null)
                {
                    InitiatorPerson initiator = new InitiatorPerson(caseFile.getOriginator().getPerson());
                    initiator.setType(caseFile.getOriginator().getPersonType());

                    form.setInitiator(initiator);
                }

                if (caseFile.getPersonAssociations() != null)
                {
                    List<Person> people = new ArrayList<>();
                    for (PersonAssociation pa : caseFile.getPersonAssociations())
                    {
                        if (pa.getPerson() != null && !pa.getPersonType().equals("Initiator"))
                        {
                            PeoplePerson peoplePerson = new PeoplePerson(pa.getPerson());
                            peoplePerson.setType(pa.getPersonType());

                            people.add(peoplePerson);
                        }
                    }

                    form.setPeople(people);
                }
            }
        } catch (Exception e)
        {
            LOG.error("Cannot convert Object to Frevvo form.", e);
        }

        return form;
    }

    public ObjectAssociationDao getObjectAssociationDao()
    {
        return objectAssociationDao;
    }

    public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao)
    {
        this.objectAssociationDao = objectAssociationDao;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public AcmHistoryDao getAcmHistoryDao()
    {
        return acmHistoryDao;
    }

    public void setAcmHistoryDao(AcmHistoryDao acmHistoryDao)
    {
        this.acmHistoryDao = acmHistoryDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

}
