
/**
 * 
 */
package com.armedia.acm.plugins.complaint.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.frevvo.config.FrevvoFormUrl;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.complaint.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.Contact;
import com.armedia.acm.plugins.complaint.model.complaint.MainInformation;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.services.users.model.AcmUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * @author riste.tutureski
 *
 */
public class ComplaintService extends FrevvoFormAbstractService implements FrevvoFormService {

	private Logger LOG = LoggerFactory.getLogger(ComplaintService.class);

    private SaveComplaintTransaction saveComplaintTransaction;

    private ComplaintFactory complaintFactory = new ComplaintFactory();

    public ComplaintService() {
		
	}

	@Override
	public Object init() {		
		return load();
	}

	@Override
	public Object get(String action) {
		Object result = null;
		
		if (action != null) {
			
			if ("init-contact-fields".equals(action)) {
				String contactFormType = getRequest().getParameter("type");
				
				if (contactFormType != null && "initiator".equals(contactFormType)){
					result = this.initInitiatorFields();					
				} else if (contactFormType != null && "people".equals(contactFormType)){
					result = this.initPeopleFields();
				}
			}
			
			if ("init-incident-fields".equals(action)) {
				result = this.initIncidentFields();
			}
		}
		
		return result;
	}

	@Override
	public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {
		Complaint complaint = (Complaint) convertFromXMLToObject(xml, Complaint.class);

        complaint = saveComplaint(complaint);

		saveAttachments(attachments, complaint.getCmisFolderId(), FrevvoFormName.COMPLAINT.toUpperCase(), complaint.getComplaintId(), complaint.getComplaintNumber());

		return false;
	}

    protected Complaint saveComplaint(Complaint complaint) throws MuleException
    {
        com.armedia.acm.plugins.complaint.model.Complaint acmComplaint = getComplaintFactory().asAcmComplaint(complaint);

        acmComplaint = getSaveComplaintTransaction().saveComplaint(acmComplaint, getAuthentication());

        complaint.setComplaintId(acmComplaint.getComplaintId());
        complaint.setComplaintNumber(acmComplaint.getComplaintNumber());
        complaint.setCmisFolderId(acmComplaint.getEcmFolderId());

        return complaint;
    }
	
	public Object load() {

		Object result = null;
		
		if (getProperties() != null && getProperties().size() > 0) {
			String formType = (String) getProperties().get(FrevvoFormName.COMPLAINT + ".type");	
			
			if (formType != null && !"".equals(formType)) {
				StringBuilder builder = new StringBuilder();
				String token = getAuthenticationTokenService().getTokenForAuthentication(getAuthentication());
				
				builder.append("<p0:form xmlns:p0=\"http://www.frevvo.com/schemas/" + formType + "\">");
				builder.append("<serviceBaseUrl>" + getProperties().get(FrevvoFormUrl.SERVICE) + "</serviceBaseUrl>");	
				// TODO: Init form data if needed
				if (token != null && !"".equals(token)) {
					builder.append("<acm_ticket>" + token + "</acm_ticket>");					
				}
				builder.append("</p0:form>");
				
				result = builder.toString();
			}else{
				LOG.warn("The form type for form name \"" + FrevvoFormName.COMPLAINT + "\" does not exist.");				
			}
		}else{
			LOG.warn("Unable to load properties or the file is empty.");
		}
		
		return result;
	}
	
	public JSONObject initInitiatorFields() {
		
		// TODO: These are hardcoded values. Read from database or somewhere else
		
		
		String userId = getAuthentication().getName();
        AcmUser user = getUserDao().findByUserId(userId);
		
		Contact initiator = new Contact();

		MainInformation mainInformation = new MainInformation();
		List<String> titles = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".titles"), ",");
		List<String> types = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".types"), ",");
		
		mainInformation.setTitles(titles);
		mainInformation.setAnonimuos("true");
		mainInformation.setTypes(types);
		mainInformation.setType("Initiator");
		
		List<ContactMethod> communicationDevices = new ArrayList<ContactMethod>();
		ContactMethod communicatoinDevice = new ContactMethod();
		types = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".deviceTypes"), ",");
		
		communicatoinDevice.setTypes(types);
		communicatoinDevice.setCreated(new Date());
		communicatoinDevice.setCreator(user.getFullName());
		communicationDevices.add(communicatoinDevice);
		
		List<Organization> organizationInformations = new ArrayList<Organization>();
		Organization organizationInformation = new Organization();
		types = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".organizationTypes"), ",");
		
		organizationInformation.setOrganizationTypes(types);
		organizationInformation.setCreated(new Date());
		organizationInformation.setCreator(user.getFullName());
		organizationInformations.add(organizationInformation);
		
		List<PostalAddress> locationInformations = new ArrayList<PostalAddress>();
		PostalAddress locationInformation = new PostalAddress();
		types = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".locationTypes"), ",");
		
		locationInformation.setTypes(types);
		locationInformation.setCreated(new Date());
		locationInformation.setCreator(user.getFullName());
		locationInformations.add(locationInformation);
		

		PersonAlias aliasInformation = new PersonAlias();
		types = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".aliasTypes"), ",");
		
		aliasInformation.setAliasTypes(types);
		aliasInformation.setCreated(new Date());
		aliasInformation.setCreator(user.getFullName());
		
		
		initiator.setMainInformation(mainInformation);
		initiator.setCommunicationDevice(communicationDevices);
		initiator.setOrganization(organizationInformations);
		initiator.setLocation(locationInformations);
		initiator.setAlias(aliasInformation);
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(initiator);
		
		JSONObject json = new JSONObject(jsonString);
		
		return json;
		
	}
	
	public JSONObject initPeopleFields() {
		
		// TODO: These are hardcoded values. Read from database or somewhere else
		
		
		String userId = getAuthentication().getName();
        AcmUser user = getUserDao().findByUserId(userId);
		
		Contact people = new Contact();
		
		MainInformation mainInformation = new MainInformation();
		List<String> titles = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".titles"), ",");
		List<String> types = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".types"), ",");	
		
		if (types != null && types.size() > 0){
			types.remove(0);
		}
		
		mainInformation.setTitles(titles);
		mainInformation.setAnonimuos("true");
		mainInformation.setTypes(types);
		
		List<ContactMethod> communicationDevices = new ArrayList<ContactMethod>();
		ContactMethod communicatoinDevice = new ContactMethod();
		types = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".deviceTypes"), ",");	
		
		communicatoinDevice.setTypes(types);
		communicatoinDevice.setCreated(new Date());
		communicatoinDevice.setCreator(user.getFullName());
		communicationDevices.add(communicatoinDevice);
		
		List<Organization> organizationInformations = new ArrayList<Organization>();
		Organization organizationInformation = new Organization();
		types = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".organizationTypes"), ",");
		
		organizationInformation.setOrganizationTypes(types);
		organizationInformation.setCreated(new Date());
		organizationInformation.setCreator(user.getFullName());
		organizationInformations.add(organizationInformation);
		
		List<PostalAddress> locationInformations = new ArrayList<PostalAddress>();
		PostalAddress locationInformation = new PostalAddress();
		types = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".locationTypes"), ",");
		
		locationInformation.setTypes(types);
		locationInformation.setCreated(new Date());
		locationInformation.setCreator(user.getFullName());
		locationInformations.add(locationInformation);
		
		PersonAlias aliasInformation = new PersonAlias();
		types = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".aliasTypes"), ",");
		
		aliasInformation.setAliasTypes(types);
		aliasInformation.setCreated(new Date());
		aliasInformation.setCreator(user.getFullName());
		
		
		people.setMainInformation(mainInformation);
		people.setCommunicationDevice(communicationDevices);
		people.setOrganization(organizationInformations);
		people.setLocation(locationInformations);
		people.setAlias(aliasInformation);
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(people);
		
		JSONObject json = new JSONObject(jsonString);
		
		return json;
		
	}

	public JSONObject initIncidentFields() {
		
		// TODO: These are hardcoded values. Read from database or somewhere else
		
		Complaint complaint = new Complaint();
		
		List<String> categories = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".categories"), ",");	
		List<String> priorities = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".priorities"), ",");
		List<String> frequencies = convertToList((String) getProperties().get(FrevvoFormName.COMPLAINT + ".frequencies"), ",");
		
		complaint.setCategories(categories);
		complaint.setPriorities(priorities);
		complaint.setFrequencies(frequencies);
		complaint.setDate(new Date());
		complaint.setPriority("Low");
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(complaint);
		
		JSONObject json = new JSONObject(jsonString);
		
		return json;
		
	}
	
	private List<String> convertToList(String source, String delimiter){
		if (source != null && !"".equals(source)) {
			String[] sourceArray = source.split(delimiter);
			return new LinkedList<String>(Arrays.asList(sourceArray)); 
		}
		
		return null;
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
}
