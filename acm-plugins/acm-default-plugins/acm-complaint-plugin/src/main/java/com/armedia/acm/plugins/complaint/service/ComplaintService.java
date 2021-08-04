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

import com.armedia.acm.form.config.xml.OwningGroupItem;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.ComplaintForm;
import com.armedia.acm.plugins.complaint.model.complaint.SearchResult;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.AssociatedTagService;
import com.armedia.acm.services.tag.service.TagService;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserActionName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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

    private Logger LOG = LogManager.getLogger(ComplaintService.class);

    private SaveComplaintTransaction saveComplaintTransaction;
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
                }
                catch (Exception e)
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

        saveAttachments(attachments, complaint.getCmisFolderId(), FrevvoFormName.COMPLAINT.toUpperCase(), complaint.getComplaintId());

        if (null != complaint && null != complaint.getComplaintId())
        {
            getUserActionExecutor().execute(complaint.getComplaintId(), AcmUserActionName.LAST_COMPLAINT_CREATED,
                    getAuthentication().getName());
        }

        return retval;
    }

    protected ComplaintForm saveComplaint(ComplaintForm complaint) throws PipelineProcessException
    {
        getComplaintFactory().setPersonDao(getPersonDao());
        getComplaintFactory().setFileService(getEcmFileService());
        Complaint acmComplaint = getComplaintFactory().asAcmComplaint(complaint);

        boolean isNew = acmComplaint.getComplaintId() == null;

        Complaint oldComplaint = getOldComplaint(complaint, isNew);

        acmComplaint = getSaveComplaintTransaction().saveComplaint(acmComplaint, getAuthentication());

        getComplaintEventPublisher().publishComplaintEvent(acmComplaint, oldComplaint, getAuthentication(), isNew, true);

        complaint = getComplaintFactory().asFrevvoComplaint(acmComplaint, complaint);

        return complaint;
    }

    @Transactional
    public Complaint saveComplaint(Complaint in, Authentication auth) throws PipelineProcessException
    {
        Complaint saved = getSaveComplaintTransaction().saveComplaint(in, auth);
        getSaveComplaintTransaction().getComplaintDao().getEm().flush();

        return saved;
    }

    protected Complaint saveComplaintObject(ComplaintForm complaint) throws PipelineProcessException
    {
        getComplaintFactory().setPersonDao(getPersonDao());
        getComplaintFactory().setFileService(getEcmFileService());
        Complaint acmComplaint = getComplaintFactory().asAcmComplaint(complaint);

        boolean isNew = acmComplaint.getComplaintId() == null;

        Complaint oldComplaint = getOldComplaint(complaint, isNew);

        acmComplaint = getSaveComplaintTransaction().saveComplaint(acmComplaint, getAuthentication());

        if (acmComplaint.getTag() != null)
        {
            LOG.debug("Creating Tag and AssociatedTag object.");
            String tagName = acmComplaint.getTag();
            AcmTag complaintTag = getTagService().saveTag(tagName, tagName, tagName);
            getAssociatedTagService().saveAssociateTag("COMPLAINT", acmComplaint.getComplaintId(), acmComplaint.getComplaintTitle(),
                    complaintTag);
        }

        getComplaintEventPublisher().publishComplaintEvent(acmComplaint, oldComplaint, getAuthentication(), isNew, true);

        return acmComplaint;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        return getComplaintFactory().asFrevvoComplaint((Complaint) obj, (ComplaintForm) form);
    }

    private Complaint getOldComplaint(ComplaintForm complaint, boolean isNew)
    {
        Complaint oldComplaint = null;
        if (!isNew)
        {
            String old = getObjectConverter().getJsonMarshaller()
                    .marshal(saveComplaintTransaction.getComplaint(complaint.getComplaintId()));
            oldComplaint = getObjectConverter().getJsonUnmarshaller().unmarshall(old, Complaint.class);
        }
        return oldComplaint;
    }

    private JSONObject initFormData()
    {
        ComplaintForm complaint = initIncidentFields();
        JSONObject json = createResponse(complaint);

        return json;
    }

    private JSONObject initParticipantsAndGroupsInfo()
    {
        ComplaintForm complaint = new ComplaintForm();

        // Participants Initialization
        List<String> participantTypes = getStandardLookupEntries("complaintParticipantTypes");
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

    private ComplaintForm initIncidentFields()
    {

        String userId = getAuthentication().getName();
        AcmUser user = getUserDao().findByUserId(userId);

        ComplaintForm complaint = new ComplaintForm();

        List<String> categories = getStandardLookupEntries("complaintTypes");
        List<String> priorities = getStandardLookupEntries("priorities");
        List<String> frequencies = getStandardLookupEntries("frequencies");
        List<String> locationTypes = getStandardLookupEntries("locationTypes");

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

        if ((null != existingContactName && !"".equals(existingContactName))
                || (null != existingContactValue && !"".equals(existingContactValue)))
        {
            List<Person> persons = personDao.findByNameOrContactValue(existingContactName, existingContactValue);
            if (null != persons && persons.size() > 0)
            {
                List<String> result = new ArrayList<>();
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
            String information = "<strong>Title:</strong> " + person.getTitle() + "<br/>" + "<strong>First Name:</strong> "
                    + person.getGivenName() + "<br/>" + "<strong>Last Name:</strong> " + person.getFamilyName() + "<br/>";

            // Communication Devices
            information = information + "<strong>Communication Devices:</strong> <br/>";
            List<ContactMethod> contactMethods = person.getContactMethods();
            if (null != contactMethods && contactMethods.size() > 0)
            {
                for (ContactMethod contactMethod : contactMethods)
                {
                    information = information + "   - " + contactMethod.getType() + ": " + contactMethod.getValue() + "<br/>";
                }

            }
            else
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
                    information = information + "   - " + organization.getOrganizationType() + ": " + organization.getOrganizationValue()
                            + "<br/>";
                }

            }
            else
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
                    information = information + "   - " + location.getType() + ": <br/>" + "      - Address:" + location.getStreetAddress()
                            + "<br/>" + "      - City:" + location.getCity() + "<br/>" + "      - State:" + location.getState() + "<br/>"
                            + "      - Zip Code:" + location.getZip() + "<br/>";
                }

            }
            else
            {
                information = information + "   - No any data.<br/>";
            }

            searchResult.setInformation(information);
        }
        else
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

    public ComplaintEventPublisher getComplaintEventPublisher()
    {
        return complaintEventPublisher;
    }

    public void setComplaintEventPublisher(ComplaintEventPublisher complaintEventPublisher)
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
