/**
 * 
 */
package com.armedia.acm.plugins.complaint.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.plugins.complaint.model.complaint.Complaint;
import com.armedia.acm.plugins.complaint.model.incident.Incident;
import com.armedia.acm.plugins.complaint.model.initiator.Initiator;
import com.armedia.acm.plugins.complaint.model.initiator.InitiatorAliasInformation;
import com.armedia.acm.plugins.complaint.model.initiator.InitiatorCommunicationDevice;
import com.armedia.acm.plugins.complaint.model.initiator.InitiatorLocationInformation;
import com.armedia.acm.plugins.complaint.model.initiator.InitiatorMainInformation;
import com.armedia.acm.plugins.complaint.model.initiator.InitiatorOrganizationInformation;
import com.armedia.acm.plugins.complaint.model.people.People;
import com.armedia.acm.plugins.complaint.model.people.PeopleAliasInformation;
import com.armedia.acm.plugins.complaint.model.people.PeopleCommunicationDevice;
import com.armedia.acm.plugins.complaint.model.people.PeopleLocationInformation;
import com.armedia.acm.plugins.complaint.model.people.PeopleMainInformation;
import com.armedia.acm.plugins.complaint.model.people.PeopleOrganizationInformation;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;


/**
 * @author riste.tutureski
 *
 */
public class ComplaintService extends FrevvoFormAbstractService implements FrevvoFormService {

	private Logger LOG = LoggerFactory.getLogger(ComplaintService.class);
	
	private Map<String, Object> properties;
	private HttpServletRequest request;
	private Authentication authentication;
	private AuthenticationTokenService authenticationTokenService;
	
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
				String contactFormType = request.getParameter("type");
				
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
	public boolean save(String xml, Map<String, MultipartFile> attachments) {
		Complaint complaint = (Complaint) convertFromXMLToObject(xml, Complaint.class);
		return false;
	}

	@Override
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	@Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
		
	}
	
	@Override
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;		
	}
	
	@Override
	public void setAuthenticationTokenService(
			AuthenticationTokenService authenticationTokenService) {
		this.authenticationTokenService = authenticationTokenService;		
	}
	
	public Object load() {

		Object result = null;
		
		if (this.properties != null && this.properties.size() > 0) {
			String formType = (String) this.properties.get(FrevvoFormName.COMPLAINT + ".type");	
			
			if (formType != null && !"".equals(formType)) {
				StringBuilder builder = new StringBuilder();
				String token = this.authenticationTokenService.getTokenForAuthentication(authentication);
				
				builder.append("<p0:form xmlns:p0=\"http://www.frevvo.com/schemas/" + formType + "\">");
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
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
		String date = sdf.format(new Date());
		
		Initiator initiator = new Initiator();
		
		InitiatorMainInformation mainInformation = new InitiatorMainInformation();
		List<String> titles = new ArrayList<String>();	
		titles.add("mr=Mr");
		titles.add("mrs=Mrs");
		titles.add("ms=Ms");
		titles.add("miss=Miss");
		List<String> types = new ArrayList<String>();	
		types.add("initiator=Initiator");
		types.add("complaintant=Complaintant");
		types.add("subject=Subject");
		types.add("witness=Witness");
		types.add("wrongdoer=Wrongdoer");
		types.add("other=Other");
		mainInformation.setInitiatorTitles(titles);
		mainInformation.setInitiatorAnonimuos("true");
		mainInformation.setInitiatorTypes(types);
		mainInformation.setInitiatorType("initiator");
		
		List<InitiatorCommunicationDevice> communicationDevices = new ArrayList<InitiatorCommunicationDevice>();
		InitiatorCommunicationDevice communicatoinDevice = new InitiatorCommunicationDevice();
		types = new ArrayList<String>();
		types.add("homePhone=Home phone");
		types.add("cellPhone=Cell phone");
		types.add("officePhone=Office phone");
		types.add("pager=Pager");
		types.add("email=Email");
		types.add("instantMessenger=Instant messenger");
		types.add("socialMedia=Social media");
		types.add("website=Website");
		types.add("blog=Blog");
		communicatoinDevice.setInitiatorDeviceTypes(types);
		communicatoinDevice.setInitiatorDeviceDate(new Date());
		communicatoinDevice.setInitiatorDeviceAddedBy("David Miller");
		communicationDevices.add(communicatoinDevice);
		
		List<InitiatorOrganizationInformation> organizationInformations = new ArrayList<InitiatorOrganizationInformation>();
		InitiatorOrganizationInformation organizationInformation = new InitiatorOrganizationInformation();
		types = new ArrayList<String>();
		types.add("nonProfit=Non-profit");
		types.add("government=Government");
		types.add("corporation=Corporatione");
		organizationInformation.setInitiatorOrganizationTypes(types);
		organizationInformation.setInitiatorOrganizationDate(new Date());
		organizationInformation.setInitiatorOrganizationAddedBy("David Miller");
		organizationInformations.add(organizationInformation);
		
		List<InitiatorLocationInformation> locationInformations = new ArrayList<InitiatorLocationInformation>();
		InitiatorLocationInformation locationInformation = new InitiatorLocationInformation();
		types = new ArrayList<String>();
		types.add("business=Business");
		types.add("home=Home");
		locationInformation.setInitiatorLocationTypes(types);
		locationInformation.setInitiatorLocationDate(new Date());
		locationInformation.setInitiatorLocationAddedBy("David Miller");
		locationInformations.add(locationInformation);
		
		List<InitiatorAliasInformation> aliasInformations = new ArrayList<InitiatorAliasInformation>();
		InitiatorAliasInformation aliasInformation = new InitiatorAliasInformation();
		types = new ArrayList<String>();
		types.add("fka=FKA");
		types.add("married=Married");
		aliasInformation.setInitiatorAliasTypes(types);
		aliasInformation.setInitiatorAliasDate(new Date());
		aliasInformation.setInitiatorAliasAddedBy("David Miller");
		aliasInformations.add(aliasInformation);
		
		
		initiator.setInitiatorMainInformation(mainInformation);
		initiator.setInitiatorCommunicationDevice(communicationDevices);
		initiator.setInitiatorOrganizationInformation(organizationInformations);
		initiator.setInitiatorLocationInformation(locationInformations);
		initiator.setInitiatorAliasInformation(aliasInformations);
		
		JSONObject json = new JSONObject(initiator);
		
		return json;
		
	}
	
	public JSONObject initPeopleFields() {
		
		// TODO: These are hardcoded values. Read from database or somewhere else
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
		String date = sdf.format(new Date());
		
		People initiator = new People();
		
		PeopleMainInformation mainInformation = new PeopleMainInformation();
		List<String> titles = new ArrayList<String>();	
		titles.add("mr=Mr");
		titles.add("mrs=Mrs");
		titles.add("ms=Ms");
		titles.add("miss=Miss");
		List<String> types = new ArrayList<String>();	
		types.add("complaintant=Complaintant");
		types.add("subject=Subject");
		types.add("witness=Witness");
		types.add("wrongdoer=Wrongdoer");
		types.add("other=Other");
		mainInformation.setPeopleTitles(titles);
		mainInformation.setPeopleAnonimuos("true");
		mainInformation.setPeopleTypes(types);
		
		List<PeopleCommunicationDevice> communicationDevices = new ArrayList<PeopleCommunicationDevice>();
		PeopleCommunicationDevice communicatoinDevice = new PeopleCommunicationDevice();
		types = new ArrayList<String>();
		types.add("homePhone=Home phone");
		types.add("cellPhone=Cell phone");
		types.add("officePhone=Office phone");
		types.add("pager=Pager");
		types.add("email=Email");
		types.add("instantMessenger=Instant messenger");
		types.add("socialMedia=Social media");
		types.add("website=Website");
		types.add("blog=Blog");
		communicatoinDevice.setPeopleDeviceTypes(types);
		communicatoinDevice.setPeopleDeviceDate(new Date());
		communicatoinDevice.setPeopleDeviceAddedBy("David Miller");
		communicationDevices.add(communicatoinDevice);
		
		List<PeopleOrganizationInformation> organizationInformations = new ArrayList<PeopleOrganizationInformation>();
		PeopleOrganizationInformation organizationInformation = new PeopleOrganizationInformation();
		types = new ArrayList<String>();
		types.add("nonProfit=Non-profit");
		types.add("government=Government");
		types.add("corporation=Corporatione");
		organizationInformation.setPeopleOrganizationTypes(types);
		organizationInformation.setPeopleOrganizationDate(new Date());
		organizationInformation.setPeopleOrganizationAddedBy("David Miller");
		organizationInformations.add(organizationInformation);
		
		List<PeopleLocationInformation> locationInformations = new ArrayList<PeopleLocationInformation>();
		PeopleLocationInformation locationInformation = new PeopleLocationInformation();
		types = new ArrayList<String>();
		types.add("business=Business");
		types.add("home=Home");
		locationInformation.setPeopleLocationTypes(types);
		locationInformation.setPeopleLocationDate(new Date());
		locationInformation.setPeopleLocationAddedBy("David Miller");
		locationInformations.add(locationInformation);
		
		List<PeopleAliasInformation> aliasInformations = new ArrayList<PeopleAliasInformation>();
		PeopleAliasInformation aliasInformation = new PeopleAliasInformation();
		types = new ArrayList<String>();
		types.add("fka=FKA");
		types.add("married=Married");
		aliasInformation.setPeopleAliasTypes(types);
		aliasInformation.setPeopleAliasDate(new Date());
		aliasInformation.setPeopleAliasAddedBy("David Miller");
		aliasInformations.add(aliasInformation);
		
		
		initiator.setPeopleMainInformation(mainInformation);
		initiator.setPeopleCommunicationDevice(communicationDevices);
		initiator.setPeopleOrganizationInformation(organizationInformations);
		initiator.setPeopleLocationInformation(locationInformations);
		initiator.setPeopleAliasInformation(aliasInformations);
		
		JSONObject json = new JSONObject(initiator);
		
		return json;
		
	}

	public JSONObject initIncidentFields() {
		
		// TODO: These are hardcoded values. Read from database or somewhere else
		
		Incident incident = new Incident();
		
		List<String> categories = new ArrayList<String>();
		
		categories.add("1=Category 1");
		categories.add("2=Category 2");
		categories.add("3=Category 3");
		categories.add("4=Category 4");
		
		List<String> priorities = new ArrayList<String>();
		
		priorities.add("low=Low");
		priorities.add("medium=Medium");
		priorities.add("high=High");
		priorities.add("expedite=Expedite");
		
		List<String> frequencies = new ArrayList<String>();
		
		frequencies.add("once=Once");
		frequencies.add("ongoing=Ongoing");
		frequencies.add("intermittent=Intermittent");
		frequencies.add("other=Other (free form)");
		
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
		String date = sdf.format(new Date());
		
		incident.setIncidentCategories(categories);
		incident.setPriorities(priorities);
		incident.setFrequencies(frequencies);
		incident.setIncidentDate(new Date());
		incident.setPriority("low");
		
		JSONObject json = new JSONObject(incident);
		
		return json;
		
	}
	
}
