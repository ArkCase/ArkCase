package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import gov.privacy.dao.PortalSARPersonDao;
import gov.privacy.model.PortalPersonDTO;
import gov.privacy.model.PortalPostalAddressDTO;
import gov.privacy.model.PortalSARFile;
import gov.privacy.model.PortalSubjectAccessRequest;
import gov.privacy.model.SARPerson;
import gov.privacy.model.SARPersonAssociation;
import gov.privacy.model.SubjectAccessRequest;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class PortalCreateRequestService
{
    private final Logger log = LogManager.getLogger(getClass());

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private UserTrackerService userTrackerService;

    private SaveSARService saveSARService;

    private PortalSARPersonDao portalSARPersonDao;

    private PortalRequestService portalRequestService;

    private SARPortalUserServiceProvider portalUserServiceProvider;

    public SubjectAccessRequest createSAR(PortalSubjectAccessRequest in)
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

        SubjectAccessRequest request = populateRequest(in);

        Map<String, List<MultipartFile>> filesMap = new HashMap<>();
        if (in.getFiles() != null)
        {
            Set<Map.Entry<String, List<PortalSARFile>>> uploadedFiles = in.getFiles().entrySet();
            if (isSubjectSameAsRequester().test(in))
            {
                uploadedFiles = uploadedFiles.stream().filter(k -> k.getKey().equalsIgnoreCase("Subject Proof of Identity"))
                        .collect(Collectors.toSet());
            }
            for (Map.Entry<String, List<PortalSARFile>> entry : uploadedFiles)
            {
                List<MultipartFile> files = new ArrayList<>();
                for (PortalSARFile requestFile : entry.getValue())
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

        SubjectAccessRequest saved = (SubjectAccessRequest) getSaveSARService().savePortalRequest(request, filesMap, auth, ipAddress);

        log.debug("Subject Access Request: {}", saved);

        return saved;
    }

    public static Predicate<PortalSubjectAccessRequest> isSubjectSameAsRequester()
    {
        return p -> p.getRequester().getEmail().equalsIgnoreCase(p.getSubject().getEmail());
    }

    public SubjectAccessRequest populateRequest(PortalSubjectAccessRequest in)
    {
        SubjectAccessRequest request = new SubjectAccessRequest();

        populateRequestOnlyProperties(in, request);

        SARPersonAssociation requesterAssociation = createPersonAssociation(in, "Requester");
        SARPersonAssociation subjectAssociation = createPersonAssociation(in, "Subject");

        request.getPersonAssociations().add(requesterAssociation);
        request.getPersonAssociations().add(subjectAssociation);

        return request;
    }

    private void populateRequestOnlyProperties(PortalSubjectAccessRequest in, SubjectAccessRequest request)
    {
        if (in.getTitle() != null)
        {
            request.setTitle(in.getTitle());
        }
        else
        {
            request.setTitle(in.getSubject().getFirstName() + " " + in.getSubject().getLastName());
        }

        request.setReceivedDate(LocalDateTime.now());
        request.setDetails(in.getDetails());
        request.setExternal(true);
        request.setRequestType(in.getRequestType());
        request.setComponentAgency("Sales");
        request.setOriginalRequestNumber(in.getOriginalRequestNumber());
        request.setSignature(in.getSignature());
        request.setSignatureDate(in.getSignatureDate());

        request.setSwornStatement(in.isSwornStatement());
        request.setAccurateAndAuthorizedStatement(in.isAccurateAndAuthorizedStatement());
        request.setInformationAgreementStatement(in.isInformationAgreementStatement());
        request.setUnderstandProcessingRequirementStatement(in.isUnderstandProcessingRequirementStatement());
    }

    private SARPersonAssociation createPersonAssociation(PortalSubjectAccessRequest in, String personType)
    {
        SARPersonAssociation personAssociation = new SARPersonAssociation();
        personAssociation.setPersonType(personType);

        SARPerson portalPerson;
        PortalPersonDTO portalPersonDTO = personType.equalsIgnoreCase("Subject") ? in.getSubject() : in.getRequester();
        portalPerson = populateRequesterAndOrganizationFromRequest(portalPersonDTO);

        personAssociation.setPerson(portalPerson);
        return personAssociation;
    }

    private OrganizationAssociation createOrganizationAssociation(SARPersonAssociation personAssociation)
    {
        OrganizationAssociation organizationAssociation = new OrganizationAssociation();

        Organization organization = personAssociation.getPerson().getDefaultOrganization().getOrganization();

        organizationAssociation.setOrganization(organization);
        organizationAssociation.setAssociationType("Other");

        return organizationAssociation;
    }

    private SARPerson populateRequesterAndOrganizationFromRequest(PortalPersonDTO portalPersonDTO)
    {
        SARPerson portalPerson = new SARPerson();

        portalPerson.setGivenName(portalPersonDTO.getFirstName());
        portalPerson.setFamilyName(portalPersonDTO.getLastName());
        portalPerson.setMiddleName(portalPersonDTO.getMiddleName());
        portalPerson.setTitle(portalPersonDTO.getPrefix());
        portalPerson.setPosition(portalPersonDTO.getPosition());
        portalPerson.setDateOfBirth(portalPersonDTO.getDateOfBirth());

        PostalAddress address = getPostalAddressFromPortalSAR(portalPersonDTO.getAddress());
        if (addressHasData(address))
        {
            portalPerson.getAddresses().add(address);
        }

        List<ContactMethod> contactMethod = new ArrayList<>();
        portalPerson.setContactMethods(contactMethod);

        if (portalPersonDTO.getPhone() != null && !portalPersonDTO.getPhone().isEmpty())
        {
            ContactMethod phone = buildContactMethod("phone", portalPersonDTO.getPhone());
            portalPerson.getContactMethods().add(phone);
        }
        if (portalPersonDTO.getEmail() != null && !portalPersonDTO.getEmail().isEmpty())
        {
            ContactMethod email = buildContactMethod("email", portalPersonDTO.getEmail());
            portalPerson.getContactMethods().add(email);
        }

        return portalPerson;
    }

    private boolean addressHasData(PostalAddress address)
    {
        return (address.getStreetAddress() != null && !address.getStreetAddress().equals(""))
                || (address.getStreetAddress2() != null && !address.getStreetAddress2().equals(""))
                || (address.getCity() != null && !address.getCity().equals(""))
                || (address.getZip() != null && !address.getZip().equals(""))
                || (address.getState() != null && !address.getState().equals(""));
    }

    private PostalAddress getPostalAddressFromPortalSAR(PortalPostalAddressDTO in)
    {
        PostalAddress address = new PostalAddress();
        address.setCity(in.getCity());
        address.setCountry(in.getCountry());
        address.setState(in.getState());
        address.setStreetAddress(in.getStreetAddress());
        address.setStreetAddress2(in.getStreetAddress2());
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
     * @return the saveSARService
     */
    public SaveSARService getSaveSARService()
    {
        return saveSARService;
    }

    /**
     * @param saveSARService
     *            the saveSARService to set
     */
    public void setSaveSARService(SaveSARService saveSARService)
    {
        this.saveSARService = saveSARService;
    }

    public PortalRequestService getPortalRequestService()
    {
        return portalRequestService;
    }

    public void setPortalRequestService(PortalRequestService portalRequestService)
    {
        this.portalRequestService = portalRequestService;
    }

    public PortalSARPersonDao getPortalSARPersonDao()
    {
        return portalSARPersonDao;
    }

    public void setPortalSARPersonDao(PortalSARPersonDao portalSARPersonDao)
    {
        this.portalSARPersonDao = portalSARPersonDao;
    }

    public SARPortalUserServiceProvider getPortalUserServiceProvider()
    {
        return portalUserServiceProvider;
    }

    public void setPortalUserServiceProvider(SARPortalUserServiceProvider portalUserServiceProvider)
    {
        this.portalUserServiceProvider = portalUserServiceProvider;
    }
}
