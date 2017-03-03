
/**
 *
 */
package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.form.config.xml.OwningGroupItem;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.ComplaintForm;
import com.armedia.acm.plugins.complaint.model.complaint.Contact;
import com.armedia.acm.plugins.complaint.model.complaint.MainInformation;
import com.armedia.acm.plugins.complaint.model.complaint.SearchResult;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.AssociatedTagService;
import com.armedia.acm.services.tag.service.TagService;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserActionName;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author riste.tutureski
 */
public class ComplaintService extends FrevvoFormAbstractService implements FrevvoFormService
{

    private Logger LOG = LoggerFactory.getLogger(ComplaintService.class);

    private SaveComplaintTransaction saveComplaintTransaction;
    private AcmPluginManager acmPluginManager;
    private PersonDao personDao;
    private ComplaintEventPublisher complaintEventPublisher;
    private TagService tagService;
    private AssociatedTagService associatedTagService;

    private ComplaintFactory complaintFactory;

    public ComplaintService()
    {
    }

    @Override
    public Object init()
    {
        Object result = "";

        String mode = getRequest().getParameter("mode");

        if ("edit".equals(mode))
        {
            // TODO: Call service to get the XML form for editing
        }

        return result;
    }

    @Override
    public Object get(String action)
    {
        Object result = null;

        if (action != null)
        {
            if ("init-form-data".equals(action))
            {
                result = initFormData();
            }

            if ("search-existing-initiator".equals(action))
            {
                String existingContactName = getRequest().getParameter("existingContactName");
                String existingContactValue = getRequest().getParameter("existingContactValue");

                result = searchExistingContact(existingContactName, existingContactValue);
            }

            if ("existing-initiator".equals(action))
            {
                Long existingContactId = null;
                try
                {
                    existingContactId = Long.parseLong(getRequest().getParameter("existingContactId"));
                } catch (Exception e)
                {
                    LOG.warn("Provided ID cannot be converted to Long format: ID=" + getRequest().getParameter("existingContactId"));
                }

                result = getExistingContact(existingContactId);
            }

            if ("init-participants-groups".equals(action))
            {
                result = initParticipantsAndGroupsInfo();
            }
        }

        return result;
    }

    @Override
    public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {
        Complaint saved = saveComplaintFromXml(xml, attachments);
        return saved != null;
    }

    public Complaint saveComplaintFromXml(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {
        ComplaintForm complaint = (ComplaintForm) convertFromXMLToObject(cleanXML(xml), getFormClass());
        Complaint retval;

        retval = saveComplaintObject(complaint);

        complaint = getComplaintFactory().asFrevvoComplaint(retval, complaint);

        // Update Frevvo XML (with object ids) after saving the object
        updateXMLAttachment(attachments, getFormName(), complaint);

        saveAttachments(
                attachments,
                complaint.getCmisFolderId(),
                FrevvoFormName.COMPLAINT.toUpperCase(),
                complaint.getComplaintId());

        if (null != complaint && null != complaint.getComplaintId())
        {
            getUserActionExecutor().execute(complaint.getComplaintId(), AcmUserActionName.LAST_COMPLAINT_CREATED, getAuthentication().getName());
        }

        return retval;
    }

    protected ComplaintForm saveComplaint(ComplaintForm complaint) throws PipelineProcessException
    {
        getComplaintFactory().setPersonDao(getPersonDao());
        getComplaintFactory().setFileService(getEcmFileService());
        Complaint acmComplaint = getComplaintFactory().asAcmComplaint(complaint);

        boolean isNew = acmComplaint.getComplaintId() == null;

        acmComplaint = getSaveComplaintTransaction().saveComplaint(acmComplaint, getAuthentication());

        getComplaintEventPublisher().publishComplaintEvent(acmComplaint, getAuthentication(), isNew, true);

        complaint = getComplaintFactory().asFrevvoComplaint(acmComplaint, complaint);

        return complaint;
    }

    protected Complaint saveComplaintObject(ComplaintForm complaint) throws PipelineProcessException
    {
        getComplaintFactory().setPersonDao(getPersonDao());
        getComplaintFactory().setFileService(getEcmFileService());
        Complaint acmComplaint = getComplaintFactory().asAcmComplaint(complaint);

        boolean isNew = acmComplaint.getComplaintId() == null;

        acmComplaint = getSaveComplaintTransaction().saveComplaint(acmComplaint, getAuthentication());

        if (acmComplaint.getTag() != null)
        {
            LOG.debug("Creating Tag and AssociatedTag object.");
            String tagName = acmComplaint.getTag();
            AcmTag complaintTag = getTagService().saveTag(tagName, tagName, tagName, acmComplaint.getComplaintTitle());
            getAssociatedTagService().saveAssociateTag("COMPLAINT", acmComplaint.getComplaintId(), acmComplaint.getComplaintTitle(), complaintTag);
            System.out.println("************************** March 1st ***** the Complaint title is ***********************************"+acmComplaint.getComplaintTitle()+ "**********************************************");
        }

        getComplaintEventPublisher().publishComplaintEvent(acmComplaint, getAuthentication(), isNew, true);

        return acmComplaint;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        return getComplaintFactory().asFrevvoComplaint((Complaint) obj, (ComplaintForm) form);
    }

    private JSONObject initFormData()
    {
        // Initiator, People and Incident initialization
        Contact initiator = initInitiatorFields();
        Contact people = initPeopleFields();

        List<Contact> peoples = new ArrayList<Contact>();
        peoples.add(people);

        ComplaintForm complaint = initIncidentFields();

        complaint.setInitiator(initiator);
        complaint.setPeople(peoples);

        JSONObject json = createResponse(complaint);

        return json;
    }

    private JSONObject initParticipantsAndGroupsInfo()
    {
        ComplaintForm complaint = new ComplaintForm();

        // Participants Initialization
        List<String> participantTypes = convertToList((String) getProperties().get(getFormName() + ".participantTypes"), ",");
        complaint.setParticipantsTypeOptions(participantTypes);
        complaint.setParticipantsPrivilegeTypes(getParticipantsPrivilegeTypes(participantTypes, getFormName()));

        // Init Owning Group information
        String owningGroupType = (String) getProperties().get(getFormName() + ".owningGroupType");
        OwningGroupItem owningGroupItem = new OwningGroupItem();
        owningGroupItem.setType(owningGroupType);

        complaint.setOwningGroup(owningGroupItem);
        complaint.setOwningGroupOptions(getOwningGroups(owningGroupType, getFormName()));

        JSONObject json = createResponse(complaint);

        return json;
    }

    private Contact initInitiatorFields()
    {

        String userId = getAuthentication().getName();
        AcmUser user = getUserDao().findByUserId(userId);

        Contact initiator = new Contact();

        MainInformation mainInformation = new MainInformation();
        List<String> titles = convertToList((String) getProperties().get(getFormName() + ".titles"), ",");
        List<String> types = convertToList((String) getProperties().get(getFormName() + ".types"), ",");

        mainInformation.setTitles(titles);
        mainInformation.setAnonymous("");
        mainInformation.setTypes(types);
        mainInformation.setType("Initiator");

        List<ContactMethod> communicationDevices = new ArrayList<ContactMethod>();
        ContactMethod communicatoinDevice = new ContactMethod();
        types = convertToList((String) getProperties().get(getFormName() + ".deviceTypes"), ",");

        communicatoinDevice.setTypes(types);
        communicatoinDevice.setCreated(new Date());
        communicatoinDevice.setCreator(user.getFullName());
        communicationDevices.add(communicatoinDevice);

        List<Organization> organizationInformations = new ArrayList<Organization>();
        Organization organizationInformation = new Organization();
        types = convertToList((String) getProperties().get(getFormName() + ".organizationTypes"), ",");

        organizationInformation.setOrganizationTypes(types);
        organizationInformation.setCreated(new Date());
        organizationInformation.setCreator(user.getFullName());
        organizationInformations.add(organizationInformation);

        List<PostalAddress> locationInformations = new ArrayList<PostalAddress>();
        PostalAddress locationInformation = new PostalAddress();
        types = convertToList((String) getProperties().get(getFormName() + ".locationTypes"), ",");

        locationInformation.setTypes(types);
        locationInformation.setCreated(new Date());
        locationInformation.setCreator(user.getFullName());
        locationInformations.add(locationInformation);


        PersonAlias aliasInformation = new PersonAlias();
        types = convertToList((String) getProperties().get(getFormName() + ".aliasTypes"), ",");

        aliasInformation.setAliasTypes(types);
        aliasInformation.setCreated(new Date());
        aliasInformation.setCreator(user.getFullName());


        initiator.setMainInformation(mainInformation);
        initiator.setCommunicationDevice(communicationDevices);
        initiator.setOrganization(organizationInformations);
        initiator.setLocation(locationInformations);
        initiator.setAlias(aliasInformation);

        return initiator;

    }

    private Contact initPeopleFields()
    {

        String userId = getAuthentication().getName();
        AcmUser user = getUserDao().findByUserId(userId);

        Contact people = new Contact();

        MainInformation mainInformation = new MainInformation();
        List<String> titles = convertToList((String) getProperties().get(getFormName() + ".titles"), ",");
        List<String> types = convertToList((String) getProperties().get(getFormName() + ".types"), ",");

        if (types != null && types.size() > 0)
        {
            types.remove(0);
        }

        mainInformation.setTitles(titles);
        mainInformation.setAnonymous("");
        mainInformation.setTypes(types);

        List<ContactMethod> communicationDevices = new ArrayList<ContactMethod>();
        ContactMethod communicatoinDevice = new ContactMethod();
        types = convertToList((String) getProperties().get(getFormName() + ".deviceTypes"), ",");

        communicatoinDevice.setTypes(types);
        communicatoinDevice.setCreated(new Date());
        communicatoinDevice.setCreator(user.getFullName());
        communicationDevices.add(communicatoinDevice);

        List<Organization> organizationInformations = new ArrayList<Organization>();
        Organization organizationInformation = new Organization();
        types = convertToList((String) getProperties().get(getFormName() + ".organizationTypes"), ",");

        organizationInformation.setOrganizationTypes(types);
        organizationInformation.setCreated(new Date());
        organizationInformation.setCreator(user.getFullName());
        organizationInformations.add(organizationInformation);

        List<PostalAddress> locationInformations = new ArrayList<PostalAddress>();
        PostalAddress locationInformation = new PostalAddress();
        types = convertToList((String) getProperties().get(getFormName() + ".locationTypes"), ",");

        locationInformation.setTypes(types);
        locationInformation.setCreated(new Date());
        locationInformation.setCreator(user.getFullName());
        locationInformations.add(locationInformation);

        PersonAlias aliasInformation = new PersonAlias();
        types = convertToList((String) getProperties().get(getFormName() + ".aliasTypes"), ",");

        aliasInformation.setAliasTypes(types);
        aliasInformation.setCreated(new Date());
        aliasInformation.setCreator(user.getFullName());


        people.setMainInformation(mainInformation);
        people.setCommunicationDevice(communicationDevices);
        people.setOrganization(organizationInformations);
        people.setLocation(locationInformations);
        people.setAlias(aliasInformation);

        return people;

    }

    private ComplaintForm initIncidentFields()
    {

        String userId = getAuthentication().getName();
        AcmUser user = getUserDao().findByUserId(userId);

        ComplaintForm complaint = new ComplaintForm();

        List<String> categories = convertToList((String) getProperties().get(getFormName() + ".categories"), ",");
        List<String> priorities = convertToList((String) getProperties().get(getFormName() + ".priorities"), ",");
        List<String> frequencies = convertToList((String) getProperties().get(getFormName() + ".frequencies"), ",");
        List<String> locationTypes = convertToList((String) getProperties().get(getFormName() + ".locationTypes"), ",");

        PostalAddress location = new PostalAddress();
        location.setTypes(locationTypes);
        location.setCreated(new Date());
        location.setCreator(user.getFullName());

        complaint.setCategories(categories);
        complaint.setPriorities(priorities);
        complaint.setFrequencies(frequencies);
        complaint.setDate(new Date());
        complaint.setPriority("Low");
        complaint.setLocation(location);

        return complaint;

    }

    // This search is from database. For now it's not used. We moved to SOLR search.
    // Maybe this should be removed.
    private Object searchExistingContact(String existingContactName, String existingContactValue)
    {
        SearchResult searchResult = new SearchResult();

        if ((null != existingContactName && !"".equals(existingContactName)) || (null != existingContactValue && !"".equals(existingContactValue)))
        {
            List<Person> persons = personDao.findByNameOrContactValue(existingContactName, existingContactValue);
            if (null != persons && persons.size() > 0)
            {
                List<String> result = new ArrayList<String>();
                for (Person person : persons)
                {
                    result.add(person.getId() + "=" + person.getGivenName() + " " + person.getFamilyName());
                }

                searchResult.setResult(result);
            }
        }

        JSONObject json = createResponse(searchResult);

        return json;
    }

    private Object getExistingContact(Long id)
    {
        SearchResult searchResult = new SearchResult();

        Person person = getPersonDao().find(id);

        if (null != person)
        {
            String information = "<strong>Title:</strong> " + person.getTitle() + "<br/>" +
                    "<strong>First Name:</strong> " + person.getGivenName() + "<br/>" +
                    "<strong>Last Name:</strong> " + person.getFamilyName() + "<br/>";

            // Communication Devices
            information = information + "<strong>Communication Devices:</strong> <br/>";
            List<ContactMethod> contactMethods = person.getContactMethods();
            if (null != contactMethods && contactMethods.size() > 0)
            {
                for (ContactMethod contactMethod : contactMethods)
                {
                    information = information + "   - " + contactMethod.getType() + ": " + contactMethod.getValue() + "<br/>";
                }

            } else
            {
                information = information + "   - No any data.<br/>";
            }

            // Organizations
            information = information + "<strong>Organizations:</strong> <br/>";
            List<Organization> organizations = person.getOrganizations();
            if (null != organizations && organizations.size() > 0)
            {
                for (Organization organization : organizations)
                {
                    information = information + "   - " + organization.getOrganizationType() + ": " + organization.getOrganizationValue() + "<br/>";
                }

            } else
            {
                information = information + "   - No any data.<br/>";
            }

            // Locations
            information = information + "<strong>Locations:</strong> <br/>";
            List<PostalAddress> locations = person.getAddresses();
            if (null != locations && locations.size() > 0)
            {
                for (PostalAddress location : locations)
                {
                    information = information + "   - " + location.getType() + ": <br/>" +
                            "      - Address:" + location.getStreetAddress() + "<br/>" +
                            "      - City:" + location.getCity() + "<br/>" +
                            "      - State:" + location.getState() + "<br/>" +
                            "      - Zip Code:" + location.getZip() + "<br/>";
                }

            } else
            {
                information = information + "   - No any data.<br/>";
            }

            searchResult.setInformation(information);
        } else
        {
            LOG.warn("There is no any Person with ID=" + id);
        }

        JSONObject json = createResponse(searchResult);

        return json;
    }

    @Override
    public String getFormName()
    {
        return FrevvoFormName.COMPLAINT;
    }

    @Override
    public Class<?> getFormClass()
    {
        return ComplaintForm.class;
    }

    public SaveComplaintTransaction getSaveComplaintTransaction()
    {
        return saveComplaintTransaction;
    }

    public void setSaveComplaintTransaction(SaveComplaintTransaction saveComplaintTransaction)
    {
        this.saveComplaintTransaction = saveComplaintTransaction;
    }

    public ComplaintFactory getComplaintFactory()
    {
        return complaintFactory;
    }

    public void setComplaintFactory(ComplaintFactory complaintFactory)
    {
        this.complaintFactory = complaintFactory;
    }

    /**
     * @return the acmPluginManager
     */
    public AcmPluginManager getAcmPluginManager()
    {
        return acmPluginManager;
    }

    /**
     * @param acmPluginManager the acmPluginManager to set
     */
    public void setAcmPluginManager(AcmPluginManager acmPluginManager)
    {
        this.acmPluginManager = acmPluginManager;
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

    public ComplaintEventPublisher getComplaintEventPublisher()
    {
        return complaintEventPublisher;
    }

    public void setComplaintEventPublisher(
            ComplaintEventPublisher complaintEventPublisher)
    {
        this.complaintEventPublisher = complaintEventPublisher;
    }

    public TagService getTagService()
    {
        return tagService;
    }

    public void setTagService(TagService tagService)
    {
        this.tagService = tagService;
    }

    public AssociatedTagService getAssociatedTagService()
    {
        return associatedTagService;
    }

    public void setAssociatedTagService(AssociatedTagService associatedTagService)
    {
        this.associatedTagService = associatedTagService;
    }
}
