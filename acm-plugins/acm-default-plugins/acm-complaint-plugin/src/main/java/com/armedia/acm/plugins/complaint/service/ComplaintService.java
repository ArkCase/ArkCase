
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
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.complaint.CommunicationDevice;
import com.armedia.acm.plugins.complaint.model.complaint.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.Contact;
import com.armedia.acm.plugins.complaint.model.complaint.MainInformation;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;


/**
 * @author riste.tutureski
 *
 */
public class ComplaintService extends FrevvoFormAbstractService implements FrevvoFormService {

	private Logger LOG = LoggerFactory.getLogger(ComplaintService.class);
	
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
		
		// TODO: Save Complaint to database and save attachments
		
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
	
	@Override
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;		
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
		
		String userId = authentication.getName();
        AcmUser user = userDao.findByUserId(userId);
		
		Contact initiator = new Contact();

		MainInformation mainInformation = new MainInformation();
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
		mainInformation.setTitles(titles);
		mainInformation.setAnonimuos("true");
		mainInformation.setTypes(types);
		mainInformation.setType("initiator");
		
		List<CommunicationDevice> communicationDevices = new ArrayList<CommunicationDevice>();
		CommunicationDevice communicatoinDevice = new CommunicationDevice();
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
		communicatoinDevice.setTypes(types);
		communicatoinDevice.setDate(new Date());
		communicatoinDevice.setCreator(user.getFullName());
		communicationDevices.add(communicatoinDevice);
		
		List<Organization> organizationInformations = new ArrayList<Organization>();
		Organization organizationInformation = new Organization();
		types = new ArrayList<String>();
		types.add("nonProfit=Non-profit");
		types.add("government=Government");
		types.add("corporation=Corporatione");
		organizationInformation.setOrganizationTypes(types);
		organizationInformation.setCreated(new Date());
		organizationInformation.setCreator(user.getFullName());
		organizationInformations.add(organizationInformation);
		
		List<PostalAddress> locationInformations = new ArrayList<PostalAddress>();
		PostalAddress locationInformation = new PostalAddress();
		types = new ArrayList<String>();
		types.add("business=Business");
		types.add("home=Home");
		locationInformation.setTypes(types);
		locationInformation.setCreated(new Date());
		locationInformation.setCreator(user.getFullName());
		locationInformations.add(locationInformation);
		

		PersonAlias aliasInformation = new PersonAlias();
		types = new ArrayList<String>();
		types.add("fka=FKA");
		types.add("married=Married");
		aliasInformation.setAliasTypes(types);
		aliasInformation.setCreated(new Date());
		aliasInformation.setCreator(user.getFullName());
		
		
		initiator.setMainInformation(mainInformation);
		initiator.setCommunicationDevice(communicationDevices);
		initiator.setOrganization(organizationInformations);
		initiator.setLocation(locationInformations);
		initiator.setAlias(aliasInformation);
		
		JSONObject json = new JSONObject(initiator);
		
		return json;
		
	}
	
	public JSONObject initPeopleFields() {
		
		// TODO: These are hardcoded values. Read from database or somewhere else
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
		String date = sdf.format(new Date());
		
		String userId = authentication.getName();
        AcmUser user = userDao.findByUserId(userId);
		
		Contact initiator = new Contact();
		
		MainInformation mainInformation = new MainInformation();
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
		mainInformation.setTitles(titles);
		mainInformation.setAnonimuos("true");
		mainInformation.setTypes(types);
		
		List<CommunicationDevice> communicationDevices = new ArrayList<CommunicationDevice>();
		CommunicationDevice communicatoinDevice = new CommunicationDevice();
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
		communicatoinDevice.setTypes(types);
		communicatoinDevice.setDate(new Date());
		communicatoinDevice.setCreator(user.getFullName());
		communicationDevices.add(communicatoinDevice);
		
		List<Organization> organizationInformations = new ArrayList<Organization>();
		Organization organizationInformation = new Organization();
		types = new ArrayList<String>();
		types.add("nonProfit=Non-profit");
		types.add("government=Government");
		types.add("corporation=Corporatione");
		organizationInformation.setOrganizationTypes(types);
		organizationInformation.setCreated(new Date());
		organizationInformation.setCreator(user.getFullName());
		organizationInformations.add(organizationInformation);
		
		List<PostalAddress> locationInformations = new ArrayList<PostalAddress>();
		PostalAddress locationInformation = new PostalAddress();
		types = new ArrayList<String>();
		types.add("business=Business");
		types.add("home=Home");
		locationInformation.setTypes(types);
		locationInformation.setCreated(new Date());
		locationInformation.setCreator(user.getFullName());
		locationInformations.add(locationInformation);
		
		PersonAlias aliasInformation = new PersonAlias();
		types = new ArrayList<String>();
		types.add("fka=FKA");
		types.add("married=Married");
		aliasInformation.setAliasTypes(types);
		aliasInformation.setCreated(new Date());
		aliasInformation.setCreator(user.getFullName());
		
		
		initiator.setMainInformation(mainInformation);
		initiator.setCommunicationDevice(communicationDevices);
		initiator.setOrganization(organizationInformations);
		initiator.setLocation(locationInformations);
		initiator.setAlias(aliasInformation);
		
		JSONObject json = new JSONObject(initiator);
		
		return json;
		
	}

	public JSONObject initIncidentFields() {
		
		// TODO: These are hardcoded values. Read from database or somewhere else
		
		Complaint complaint = new Complaint();
		
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
		
		complaint.setCategories(categories);
		complaint.setPriorities(priorities);
		complaint.setFrequencies(frequencies);
		complaint.setDate(new Date());
		complaint.setPriority("low");
		
		JSONObject json = new JSONObject(complaint);
		
		return json;
		
	}
	
}
