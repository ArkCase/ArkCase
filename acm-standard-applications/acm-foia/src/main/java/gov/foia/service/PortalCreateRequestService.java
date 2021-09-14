package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import gov.foia.dao.PortalFOIAPersonDao;
import gov.foia.model.FOIAPerson;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIARequesterAssociation;
import gov.foia.model.PortalFOIAPerson;
import gov.foia.model.PortalFOIARequest;
import gov.foia.model.PortalFOIARequestFile;

public class PortalCreateRequestService
{
    private final Logger log = LogManager.getLogger(getClass());

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private UserTrackerService userTrackerService;

    private SaveFOIARequestService saveFOIARequestService;

    private PortalFOIAPersonDao portalFOIAPersonDao;

    private PersonDao personDao;

    private PortalRequestService portalRequestService;

    private FOIAPortalUserServiceProvider portalUserServiceProvider;

    public FOIARequest createFOIARequest(PortalFOIARequest in)
            throws PipelineProcessException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        log.debug("Received request {}", in);

        getAuditPropertyEntityAdapter().setUserId(in.getUserId());
        String ipAddress = in.getIpAddress();

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, in.getUserId());
        Authentication _auth = new UsernamePasswordAuthenticationToken(in.getUserId(), in.getUserId());
        AcmAuthentication auth = new AcmAuthentication(_auth);

        SecurityContextHolder.getContext().setAuthentication(auth);

        getUserTrackerService().trackUser(ipAddress);

        FOIARequest request = populateRequest(in);

        Map<String, List<MultipartFile>> filesMap = new HashMap<>();
        if (in.getFiles() != null)
        {
            for (Map.Entry<String, List<PortalFOIARequestFile>> entry : in.getFiles().entrySet())
            {
                List<MultipartFile> files = new ArrayList<>();
                for (PortalFOIARequestFile requestFile : entry.getValue())
                {
                    try
                    {
                        files.add(getPortalRequestService().convertPortalRequestFileToMultipartFile(requestFile));
                    }
                    catch (IOException e)
                    {
                        log.error("Failed to receive file {}, {}", requestFile.getFileName(), e.getMessage());
                    }
                }
                filesMap.put(entry.getKey(), files);
            }
        }

        FOIARequest saved = (FOIARequest) getSaveFOIARequestService().savePortalRequest(request, filesMap, auth, ipAddress);

        log.debug("FOIA Request: {}", saved);

        return saved;
    }

    public FOIARequest populateRequest(PortalFOIARequest in)
    {
        FOIARequest request = new FOIARequest();

        request.setExternal(true);

        request.setRequestType(in.getRequestType());
        request.setRequestSubType("FOIA");
        request.setComponentAgency("FOIA");
        request.setOriginalRequestNumber(in.getOriginalRequestNumber());
        request.setRequestCategory(in.getRequestCategory());
        request.setDeliveryMethodOfResponse(in.getDeliveryMethodOfResponse());
        request.setPortalRequestTrackingId(in.getPortalRequestTrackingId());
        if (in.getTitle() != null)
        {
            request.setTitle(in.getTitle());
        }

        request.setDetails(in.getSubject());
        request.setRecordSearchDateFrom(in.getRecordSearchDateFrom());
        request.setRecordSearchDateTo(in.getRecordSearchDateTo());

        request.setProcessingFeeWaive(in.getProcessingFeeWaive());
        request.setFeeWaiverFlag(in.isRequestFeeWaive());
        request.setRequestFeeWaiveReason(in.getRequestFeeWaiveReason());

        request.setPayFee(in.getPayFee());

        request.setExpediteFlag(in.isRequestExpedite());
        request.setRequestExpediteReason(in.getRequestExpediteReason());

        OrganizationAssociation organizationAssociation = new OrganizationAssociation();
        FOIARequesterAssociation requesterAssociation = new FOIARequesterAssociation();
        requesterAssociation.setPersonType("Requester");

        FOIAPerson requester;

        Optional<Person> existingPerson = getPersonDao().findByEmail(in.getEmail());

        requester = existingPerson
                .map(portalFOIAPerson -> updatePersonInfo(in, (FOIAPerson) portalFOIAPerson))
                .orElseGet(() -> populateRequesterAndOrganizationFromRequest(in));

        requesterAssociation.setPerson(requester);
        request.getPersonAssociations().add(requesterAssociation);

        if (requester.getDefaultOrganization() != null)
        {
            Organization organization = requester.getDefaultOrganization().getOrganization();

            organizationAssociation.setOrganization(organization);
            organizationAssociation.setAssociationType("Other");

            request.getOrganizationAssociations().add(organizationAssociation);
        }

        return request;
    }

    private PortalFOIAPerson populateRequesterAndOrganizationFromRequest(PortalFOIARequest in)
    {
        PortalFOIAPerson requester = new PortalFOIAPerson();

        requester.setGivenName(in.getFirstName());
        requester.setFamilyName(in.getLastName());
        requester.setMiddleName(in.getMiddleName());
        requester.setTitle(in.getPrefix());

        PostalAddress address = getPostalAddressFromPortalFOIARequest(in);
        if (addressHasData(address))
        {
            requester.getAddresses().add(address);
        }

        List<ContactMethod> contactMethod = new ArrayList<>();
        requester.setContactMethods(contactMethod);

        if (in.getPhone() != null && !in.getPhone().isEmpty())
        {
            ContactMethod phone = buildContactMethod("phone", in.getPhone());
            requester.getContactMethods().add(phone);
        }
        if (in.getEmail() != null && !in.getEmail().isEmpty())
        {
            ContactMethod email = buildContactMethod("email", in.getEmail());
            requester.getContactMethods().add(email);
        }

        if (in.getOrganization() != null && in.getOrganization().length() > 0)
        {
            getPortalUserServiceProvider().findOrCreateOrganizationAndPersonOrganizationAssociation(requester, in.getOrganization(), in.getPosition());
        }
        return requester;
    }

    private FOIAPerson updatePersonInfo(PortalFOIARequest in, FOIAPerson existingPerson)
    {
        FOIAPerson requester = existingPerson;

        requester.setGivenName(in.getFirstName());
        requester.setFamilyName(in.getLastName());
        requester.setMiddleName(in.getMiddleName());
        requester.setTitle(in.getPrefix());

        PostalAddress address = getPostalAddressFromPortalFOIARequest(in);
        if (addressHasData(address)
                && requester.getAddresses().stream().noneMatch(existingAddress -> areAddressesEqual(existingAddress, address)))
        {
            requester.getAddresses().add(address);
            requester.setDefaultAddress(address);
        }

        if (in.getPhone() != null && !in.getPhone().isEmpty() && isNewPhoneContactMethod(requester, in.getPhone()))
        {
            ContactMethod phone = buildContactMethod("phone", in.getPhone());
            requester.getContactMethods().add(phone);
            requester.setDefaultPhone(phone);
        }

        if (in.getOrganization() != null && in.getOrganization().length() > 0)
        {
            getPortalUserServiceProvider().findOrCreateOrganizationAndPersonOrganizationAssociation(requester, in.getOrganization(), in.getPosition());
        }
        return requester;
    }

    private boolean addressHasData(PostalAddress address)
    {
        return (address.getStreetAddress() != null && !address.getStreetAddress().equals(""))
                || (address.getStreetAddress2() != null && !address.getStreetAddress2().equals(""))
                || (address.getCity() != null && !address.getCity().equals(""))
                || (address.getZip() != null && !address.getZip().equals(""))
                || (address.getState() != null && !address.getState().equals(""));
    }

    private boolean areAddressesEqual(PostalAddress address1, PostalAddress address2)
    {
        return Objects.equals(address1.getStreetAddress(), address2.getStreetAddress())
                && Objects.equals(address1.getStreetAddress2(), address2.getStreetAddress2())
                && Objects.equals(address1.getCity(), address2.getCity())
                && Objects.equals(address1.getZip(), address2.getZip())
                && Objects.equals(address1.getState(), address2.getState());
    }

    private boolean isNewPhoneContactMethod(Person requester, String contactMethodValue)
    {
        return requester.getContactMethods().stream().filter(cm -> cm.getType().equalsIgnoreCase("Phone"))
                .noneMatch(cm -> cm.getValue().equals(contactMethodValue));
    }

    private PostalAddress getPostalAddressFromPortalFOIARequest(PortalFOIARequest in)
    {
        PostalAddress address = new PostalAddress();
        address.setCity(in.getCity());
        address.setCountry(in.getCountry());
        address.setState(in.getState());
        address.setStreetAddress(in.getAddress1());
        address.setStreetAddress2(in.getAddress2());
        address.setZip(in.getZip());
        address.setType(in.getAddressType());
        return address;
    }

    private ContactMethod buildContactMethod(String type, String value)
    {
        ContactMethod contactMethod = new ContactMethod();
        contactMethod.setType(type);
        contactMethod.setValue(value);
        return contactMethod;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public UserTrackerService getUserTrackerService()
    {
        return userTrackerService;
    }

    public void setUserTrackerService(UserTrackerService userTrackerService)
    {
        this.userTrackerService = userTrackerService;
    }

    /**
     * @return the saveFOIARequestService
     */
    public SaveFOIARequestService getSaveFOIARequestService()
    {
        return saveFOIARequestService;
    }

    /**
     * @param saveFOIARequestService
     *            the saveFOIARequestService to set
     */
    public void setSaveFOIARequestService(SaveFOIARequestService saveFOIARequestService)
    {
        this.saveFOIARequestService = saveFOIARequestService;
    }

    public PortalRequestService getPortalRequestService()
    {
        return portalRequestService;
    }

    public void setPortalRequestService(PortalRequestService portalRequestService)
    {
        this.portalRequestService = portalRequestService;
    }

    public PortalFOIAPersonDao getPortalFOIAPersonDao()
    {
        return portalFOIAPersonDao;
    }

    public void setPortalFOIAPersonDao(PortalFOIAPersonDao portalFOIAPersonDao)
    {
        this.portalFOIAPersonDao = portalFOIAPersonDao;
    }

    public FOIAPortalUserServiceProvider getPortalUserServiceProvider()
    {
        return portalUserServiceProvider;
    }

    public void setPortalUserServiceProvider(FOIAPortalUserServiceProvider portalUserServiceProvider)
    {
        this.portalUserServiceProvider = portalUserServiceProvider;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

}
