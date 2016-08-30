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
import com.armedia.acm.services.participants.model.AcmParticipant;

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

        buildGeneralInformation(form, caseFile);
        buildInitiator(form, caseFile);
        buildPeople(form, caseFile);
        buildParticipants(form, caseFile);

        return caseFile;
    }

    private void buildGeneralInformation(CaseFileForm form, CaseFile caseFile)
    {
        caseFile.setTitle(form.getCaseTitle());
        caseFile.setCaseType(form.getCaseType());
        caseFile.setDetails(form.getCaseDescription());
    }

    private void buildInitiator(CaseFileForm form, CaseFile caseFile)
    {
        if (form.getInitiator() != null)
        {
            PersonAssociation personAssociation = caseFile.getOriginator();
            if (personAssociation == null)
            {
                personAssociation = new PersonAssociation();
            }
            Person person = form.getInitiator();
            if (person.getId() != null)
            {
                // Update Person Association
                saveInformation(personAssociation, person);
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
    }

    private void buildPeople(CaseFileForm form, CaseFile caseFile)
    {
        if (form.getPeople() != null)
        {
            List<PersonAssociation> paArray = caseFile.getPersonAssociations();
            if (paArray == null)
            {
                paArray = new ArrayList<>();
            }

            ArrayList<Person> personsToAdd = new ArrayList<Person>();

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
                            saveInformation(personAssociation, person);
                            personAssociation.setPersonType(((PeoplePerson) person).getType());
                            saveCommunicationDevice(personAssociation, person);
                            saveOrganizationInformation(personAssociation, person);
                            saveLocationInformation(personAssociation, person);
                            personFound = true;
                            break;
                        }
                    }
                }
                if (!personFound)
                {
                    personsToAdd.add(person);
                }
            }

            // Add Person Association
            for (Person personToAdd : personsToAdd)
            {
                PersonAssociation pa = new PersonAssociation();
                pa.setPerson(personToAdd.returnBase());
                pa.setPersonType(((PeoplePerson) personToAdd).getType());
                paArray.add(pa);
            }

            caseFile.setPersonAssociations(paArray);
        }
    }

    private void buildParticipants(CaseFileForm form, CaseFile caseFile)
    {
        List<AcmParticipant> participants = getParticipants(caseFile.getParticipants(), form.getParticipants(), form.getOwningGroup(),
                caseFile.getObjectType());
        caseFile.setParticipants(participants);
    }

    private void saveInformation(PersonAssociation personAssociation, Person formPerson)
    {
        personAssociation.getPerson().setTitle(formPerson.getTitle());
        personAssociation.getPerson().setGivenName(formPerson.getGivenName());
        personAssociation.getPerson().setFamilyName(formPerson.getFamilyName());
    }

    private void saveLocationInformation(PersonAssociation personAssociation, Person formPerson)
    {
        List<PostalAddress> addresses = new ArrayList<PostalAddress>();
        if (personAssociation.getPerson().getAddresses() != null)
        {
            addresses = personAssociation.getPerson().getAddresses();
        }
        List<PostalAddress> addressesToAdd = new ArrayList<PostalAddress>();
        for (PostalAddress formAddress : formPerson.getAddresses())
        {
            boolean found = false;
            for (PostalAddress existingAddress : addresses)
            {
                if (existingAddress.getId().equals(formAddress.getId()))
                {
                    setAddressFields(existingAddress, formAddress);
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                addressesToAdd.add(formAddress);
            }
        }

        for (PostalAddress addressToAdd : addressesToAdd)
        {
            PostalAddress postalAddress = new PostalAddress();
            setAddressFields(postalAddress, addressToAdd);
            addresses.add(postalAddress);
        }

        personAssociation.getPerson().setAddresses(addresses);
    }

    private void setAddressFields(PostalAddress address, PostalAddress formAddress)
    {
        address.setType(formAddress.getType());
        address.setStreetAddress(formAddress.getStreetAddress());
        address.setCity(formAddress.getCity());
        address.setState(formAddress.getState());
        address.setZip(formAddress.getZip());
        address.setCreated(formAddress.getCreated());
        address.setCreator(formAddress.getCreator());
    }

    private void saveCommunicationDevice(PersonAssociation personAssociation, Person formPerson)
    {
        List<ContactMethod> contactMethods = new ArrayList<ContactMethod>();
        if (personAssociation.getPerson().getContactMethods() != null)
        {
            contactMethods = personAssociation.getPerson().getContactMethods();
        }
        ArrayList<ContactMethod> contactMethodsToAdd = new ArrayList<ContactMethod>();
        for (ContactMethod formContact : formPerson.getContactMethods())
        {
            boolean found = false;
            for (ContactMethod existingContact : contactMethods)
            {
                if (existingContact.getId().equals(formContact.getId()))
                {
                    setContactFields(existingContact, formContact);
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                contactMethodsToAdd.add(formContact);
            }
        }

        for (ContactMethod contactMethodToAdd : contactMethodsToAdd)
        {
            ContactMethod contactMethod = new ContactMethod();
            setContactFields(contactMethod, contactMethodToAdd);
            contactMethods.add(contactMethod);
        }

        personAssociation.getPerson().setContactMethods(contactMethods);
    }

    private void setContactFields(ContactMethod contact, ContactMethod formContact)
    {
        contact.setType(formContact.getType());
        contact.setValue(formContact.getValue());
        contact.setCreated(formContact.getCreated());
        contact.setCreator(formContact.getCreator());
    }

    private void saveOrganizationInformation(PersonAssociation personAssociation, Person formPerson)
    {
        List<Organization> organizations = new ArrayList<Organization>();
        if (personAssociation.getPerson().getOrganizations() != null)
        {
            organizations = personAssociation.getPerson().getOrganizations();
        }
        List<Organization> organizationsToAdd = new ArrayList<Organization>();
        for (Organization formOrganization : formPerson.getOrganizations())
        {
            boolean found = false;
            for (Organization existingOrganization : organizations)
            {
                if (existingOrganization.getOrganizationId().equals(formOrganization.getOrganizationId()))
                {
                    setOrganizationFields(existingOrganization, formOrganization);
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                organizationsToAdd.add(formOrganization);
            }
        }

        for (Organization organizationToAdd : organizationsToAdd)
        {
            Organization organization = new Organization();
            setOrganizationFields(organization, organizationToAdd);
            organizations.add(organization);
        }

        personAssociation.getPerson().setOrganizations(organizations);
    }

    private void setOrganizationFields(Organization organization, Organization formOrganization)
    {
        organization.setOrganizationType(formOrganization.getOrganizationType());
        organization.setOrganizationValue(formOrganization.getOrganizationValue());
        organization.setCreated(formOrganization.getCreated());
        organization.setCreator(formOrganization.getCreator());
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
