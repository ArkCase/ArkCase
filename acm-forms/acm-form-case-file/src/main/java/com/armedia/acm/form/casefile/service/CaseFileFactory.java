/**
 * 
 */
package com.armedia.acm.form.casefile.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.form.casefile.model.AddressHistory;
import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.EmploymentHistory;
import com.armedia.acm.form.casefile.model.Subject;
import com.armedia.acm.form.casefile.model.frevvoxmlmarshal.Employee;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.addressable.model.frevvoxmlmarshal.GeneralPostalAddress;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonIdentification;
import com.armedia.acm.plugins.person.model.frevvoxmlmarshal.GeneralOrganization;
import com.armedia.acm.service.history.dao.AcmHistoryDao;
import com.armedia.acm.service.history.model.AcmHistory;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileFactory 
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	public static final String PERSON_TYPE = "Subject";
	public static final String PERSON_IDENTIFICATION_EMPLOYEE_ID = "EMPLOYEE_ID";
	public static final String PERSON_IDENTIFICATION_SSN = "SSN";
	
	private MuleClient muleClient;
	private ObjectAssociationDao objectAssociationDao;
	private EcmFileDao ecmFileDao;
	private AcmHistoryDao acmHistoryDao;

	public CaseFile asAcmCaseFile(CaseFileForm form, CaseFile caseFile)
	{
		if (caseFile == null)
		{
			caseFile = new CaseFile();
		}
		
		caseFile.setTitle(form.getTitle());
		caseFile.setCaseType(form.getType());
		
		if (form.getSubject() != null)
		{
			PersonAssociation personAssociation = null;
			Person person = null;
			
			if (caseFile.getOriginator() != null)
			{
				personAssociation = caseFile.getOriginator();
			}
			else
			{
				personAssociation = new PersonAssociation();
			}
			
			if (caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null)
			{
				person = caseFile.getOriginator().getPerson();
			}
			else
			{
				person = new Person();
			}
			
			personAssociation.setPerson(person);
			
			caseFile.setOriginator(personAssociation);
			
			populatePerson(form, personAssociation, person);
		}
		
		return caseFile;
	}
	
	private void populatePerson(CaseFileForm form, PersonAssociation personAssociation, Person person)
	{
		Subject subject = form.getSubject();
		List<AddressHistory> addressHistoryArray = form.getAddressHistory();
		List<EmploymentHistory> employmentHistoryArray = form.getEmploymentHistory();
		
		personAssociation.setPersonType(PERSON_TYPE);
		
		person.setTitle(subject.getTitle());
		person.setGivenName(subject.getFirstName());
		person.setFamilyName(subject.getLastName());
		person.setDateOfBirth(subject.getDateOfBirth());
		
		person.setAddresses(new ArrayList<PostalAddress>());
		person.setOrganizations(new ArrayList<Organization>());
		
		if (null != addressHistoryArray && addressHistoryArray.size() > 0)
		{
			for (AddressHistory addressHistory : addressHistoryArray)
			{
				if ( addressHistory.getLocation() != null)
		        {
					person.getAddresses().addAll(Arrays.asList(addressHistory.getLocation().returnBase()));
		        }
			}
		}
		
		if (null != employmentHistoryArray && employmentHistoryArray.size() > 0)
		{
			for (EmploymentHistory employmentHistory : employmentHistoryArray)
			{
				if (employmentHistory.getOrganization() != null)
				{
					person.getOrganizations().addAll(Arrays.asList(employmentHistory.getOrganization().returnBase()));
				}
			}
		}

		String employeeId = subject.getEmployeeId();
		String ssn = subject.getSocialSecurityNumber();
		
		populatePersonIdentification(PERSON_IDENTIFICATION_EMPLOYEE_ID, employeeId, person);
		populatePersonIdentification(PERSON_IDENTIFICATION_SSN, ssn, person);

	}
	
	private void populatePersonIdentification(String key, String value, Person person)
	{
		if ( value != null && !value.trim().isEmpty() )
		{
			boolean exists = false;
			if ( person.getPersonIdentification() != null )
			{
				for ( PersonIdentification pi : person.getPersonIdentification() )
				{
					if ( key.equals(pi.getIdentificationType())  )
					{
						pi.setIdentificationNumber(value);
						exists = true;
						break;
					}
				}
			}

			if ( ! exists )
			{
				if ( person.getPersonIdentification() == null )
				{
					person.setPersonIdentification(new ArrayList<PersonIdentification>());
				}
				
				PersonIdentification pi = new PersonIdentification();
				pi.setIdentificationNumber(value);
				pi.setIdentificationType(key);
				pi.setPerson(person);
				
				person.getPersonIdentification().add(pi);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public CaseFileForm asFrevvoCaseFile(CaseFile caseFile, CaseFileForm form)
	{		
		if (caseFile != null)
		{
			CaseFileForm oldForm = populateFrevvoOldCaseFile(caseFile, form);

			form = new CaseFileForm();
			
			form.setId(caseFile.getId());
			form.setNumber(caseFile.getCaseNumber());
			form.setTitle(caseFile.getTitle());
			form.setType(caseFile.getCaseType());
			form.setCmisFolderId(caseFile.getEcmFolderId());
			
			if (caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null)
			{
				Subject subject = populateFrevvoSubject(caseFile.getOriginator().getPerson(), oldForm);
				form.setSubject(subject);
				
				Map<String, List<?>> historyArray = populateFrevvoHistory(caseFile.getOriginator().getPerson(), oldForm);
				
				form.setAddressHistory((List<AddressHistory>) historyArray.get("addressHistory"));
				form.setEmploymentHistory((List<EmploymentHistory>) historyArray.get("employmentHistory"));
			}
		}
		
		return form;
	}
	
	private CaseFileForm populateFrevvoOldCaseFile(CaseFile caseFile, CaseFileForm form)
	{
		CaseFileForm oldForm = null;
		
		if (caseFile != null)
		{			
			ObjectAssociation association = getObjectAssociationDao().findFrevvoXMLAssociation(FrevvoFormName.CASE_FILE.toUpperCase(), caseFile.getId(), FrevvoFormName.CASE_FILE.toLowerCase() + "_xml");
			
			if (association != null)
			{
				EcmFile ecmFile = getEcmFileDao().find(association.getTargetId());
				
				try 
				{
					MuleMessage message = getMuleClient().send("vm://downloadFileFlow.in", ecmFile.getEcmFileId(), null);
					
					if (null != message && message.getPayload() instanceof ContentStream)
					{
						String result = getContent((ContentStream) message.getPayload());
						
						AcmUnmarshaller unmarshaller = ObjectConverter.createXMLUnmarshaller();
						oldForm = (CaseFileForm) unmarshaller.unmarshall(result, CaseFileForm.class);
					}
				} 
				catch (MuleException e) 
				{
					LOG.error("Cannot retrieve XML file for CaseFile with id=" + caseFile.getId(), e);
				}
			}
		}
		
		if (oldForm == null && form != null)
		{
			return form;
		}
		
		return oldForm;
	}
	
	private Subject populateFrevvoSubject(Person person, CaseFileForm oldForm)
	{
		Subject subject = null;
		
		if (person != null)
		{
			subject = new Employee();
			
			subject.setId(person.getId());
			
			String employeeId = null;
			String ssn = null;
			if (person.getPersonIdentification() != null)
			{
				for (PersonIdentification personIdentification : person.getPersonIdentification())
				{
					if (PERSON_IDENTIFICATION_EMPLOYEE_ID.equals(personIdentification.getIdentificationType()))
					{
						employeeId = personIdentification.getIdentificationNumber();
					}
					else if (PERSON_IDENTIFICATION_SSN.equals(personIdentification.getIdentificationType()))
					{
						ssn = personIdentification.getIdentificationNumber();
					}
				}
			}
			
			subject.setEmployeeId(employeeId);
			subject.setSocialSecurityNumber(ssn);
			subject.setTitle(person.getTitle());
			subject.setFirstName(person.getGivenName());
			subject.setLastName(person.getFamilyName());
			subject.setDateOfBirth(person.getDateOfBirth());
			
			if (oldForm != null && oldForm.getSubject() != null)
			{
				subject.setSuffix(oldForm.getSubject().getSuffix());
				subject.setMiddleName(oldForm.getSubject().getMiddleName());
				subject.setPhoneNumber(oldForm.getSubject().getPhoneNumber());
			}		
		}
		
		return subject;
	}
	
	private Map<String, List<?>> populateFrevvoHistory(Person person, CaseFileForm oldForm)
	{
		Map<String, List<?>> historyMap = new HashMap<String, List<?>>();
		
		List<AddressHistory> addressHistoryArray = null;
		List<EmploymentHistory> employmentHistoryArray = null;

		List<AcmHistory> historyArray = getAcmHistoryDao().findByPersonId(person.getId());
		if (historyArray != null)
		{
			for (AcmHistory history : historyArray)
			{
				if ("POSTAL_ADDRESS".equals(history.getObjectType()))
				{
					AddressHistory addressHistory = new AddressHistory();
					
					addressHistory.setId(history.getId());
					addressHistory.setStartDate(history.getStartDate());
					addressHistory.setEndDate(history.getEndDate());
					
					List<PostalAddress> addresses = person.getAddresses();
					if (addresses != null)
					{
						for (PostalAddress address : addresses)
						{
							if (address.getId().equals(history.getObjectId()))
							{
								addressHistory.setLocation(new GeneralPostalAddress(address));
								break;
							}
						}
					}
					
					if (oldForm != null && oldForm.getAddressHistory() != null)
					{
						for (AddressHistory oldAddressHistory: oldForm.getAddressHistory())
						{
							if (oldAddressHistory.getId() != null &&  oldAddressHistory.getId().equals(history.getId()))
							{
								addressHistory.setReference(oldAddressHistory.getReference());
							}
						}
					}
					
					if (addressHistoryArray == null)
					{
						addressHistoryArray = new ArrayList<AddressHistory>();
					}
					
					addressHistoryArray.add(addressHistory);					
				}
				else if ("ORGANIZATION".equals(history.getObjectType()))
				{
					EmploymentHistory employmentHistory = new EmploymentHistory();
					
					employmentHistory.setId(history.getId());
					employmentHistory.setStartDate(history.getStartDate());
					employmentHistory.setEndDate(history.getEndDate());
					employmentHistory.setType(history.getPersonType());
					
					List<Organization> organizations = person.getOrganizations();
					if (organizations != null && history.getObjectId() != null)
					{
						for (Organization organization: organizations)
						{
							if (organization.getOrganizationId().equals(history.getObjectId()))
							{
								employmentHistory.setOrganization(new GeneralOrganization(organization));
								break;
							}
						}
					}
					
					if (oldForm != null && oldForm.getEmploymentHistory() != null)
					{
						for (EmploymentHistory oldEmploymentHistory: oldForm.getEmploymentHistory())
						{
							if (oldEmploymentHistory.getId() != null &&  oldEmploymentHistory.getId().equals(history.getId()))
							{
								employmentHistory.setReference(oldEmploymentHistory.getReference());
								employmentHistory.setSupervisor(oldEmploymentHistory.getSupervisor());
							}
						}
					}
					
					if (employmentHistoryArray == null)
					{
						employmentHistoryArray = new ArrayList<EmploymentHistory>();
					}
					
					employmentHistoryArray.add(employmentHistory);
				}	
			}
		}
		
		historyMap.put("addressHistory", addressHistoryArray);
		historyMap.put("employmentHistory", employmentHistoryArray);
		
		return historyMap;
	}
	
	private String getContent(ContentStream contentStream)
	{
		String content = "";
		InputStream inputStream = null;
		
		try
        {
			inputStream = contentStream.getStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer);
			content = writer.toString();
        } 
		catch (IOException e) 
		{
        	LOG.error("Could not copy input stream to the writer: " + e.getMessage(), e);
		}
		finally
        {
            if ( inputStream != null )
            {
                try
                {
                	inputStream.close();
                }
                catch (IOException e)
                {
                    LOG.error("Could not close CMIS content stream: " + e.getMessage(), e);
                }
            }
        }
		
		return content;
	}

	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}

	public ObjectAssociationDao getObjectAssociationDao() {
		return objectAssociationDao;
	}

	public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao) {
		this.objectAssociationDao = objectAssociationDao;
	}

	public EcmFileDao getEcmFileDao() {
		return ecmFileDao;
	}

	public void setEcmFileDao(EcmFileDao ecmFileDao) {
		this.ecmFileDao = ecmFileDao;
	}

	public AcmHistoryDao getAcmHistoryDao() {
		return acmHistoryDao;
	}

	public void setAcmHistoryDao(AcmHistoryDao acmHistoryDao) {
		this.acmHistoryDao = acmHistoryDao;
	}
	
}
