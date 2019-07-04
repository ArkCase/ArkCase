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
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIAPerson;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIARequesterAssociation;
import gov.foia.model.PortalFOIARequest;
import gov.foia.model.PortalFOIARequestFile;

public class PortalCreateRequestService
{
    private final Logger log = LogManager.getLogger(getClass());

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private UserTrackerService userTrackerService;

    private SaveFOIARequestService saveFOIARequestService;

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
                        files.add(portalRequestFileToMultipartFile(requestFile));
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

    private MultipartFile portalRequestFileToMultipartFile(PortalFOIARequestFile requestFile) throws IOException
    {
        byte[] content = Base64.getDecoder().decode(requestFile.getContent());

        File file = new File(requestFile.getFileName());
        Path path = Paths.get(file.getAbsolutePath());
        Files.write(path, content);

        FileItem fileItem = new DiskFileItem("", requestFile.getContentType(), false, file.getName(), (int) file.length(),
                file.getParentFile());

        try (InputStream input = new FileInputStream(file))
        {
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
        }

        return new CommonsMultipartFile(fileItem);

    }

    public FOIARequest populateRequest(PortalFOIARequest in)
    {
        FOIARequest request = new FOIARequest();

        request.setExternal(true);

        request.setRequestType(FOIAConstants.NEW_REQUEST_TYPE);
        request.setRequestSubType("FOIA");
        request.setComponentAgency("FOIA");
        request.setOriginalRequestNumber(in.getOriginalRequestNumber());
        request.setRequestCategory(in.getRequestCategory());
        request.setDeliveryMethodOfResponse(in.getDeliveryMethodOfResponse());

        if(in.getTitle() != null)
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

        FOIARequesterAssociation requesterAssociation = new FOIARequesterAssociation();
        requesterAssociation.setPersonType("Requester");

        FOIAPerson requester = new FOIAPerson();
        requesterAssociation.setPerson(requester);
        request.getPersonAssociations().add(requesterAssociation);

        requester.setGivenName(in.getFirstName());
        requester.setFamilyName(in.getLastName());
        requester.setMiddleName(in.getMiddleName());
        requester.setTitle(in.getPrefix());
        requester.setPosition(in.getPosition());
        // requester.setCompany(in.getOrganization());

        if (in.getOrganization() != null && in.getOrganization().length() > 0)
        {
            Organization organization = new Organization();
            organization.setOrganizationValue(in.getOrganization());
            organization.setOrganizationType("Corporation");
            requester.getOrganizations().add(organization);

            List<PersonOrganizationAssociation> personOrganizationAssociations = new ArrayList<>();
            PersonOrganizationAssociation personOrganizationAssociation = new PersonOrganizationAssociation();
            personOrganizationAssociation.setOrganization(organization);
            personOrganizationAssociation.setDefaultOrganization(true);
            personOrganizationAssociation.setPerson(requester);
            personOrganizationAssociation.setPersonToOrganizationAssociationType("owner");
            personOrganizationAssociation.setOrganizationToPersonAssociationType("owned");
            personOrganizationAssociations.add(personOrganizationAssociation);
            requester.setOrganizationAssociations(personOrganizationAssociations);
        }

        PostalAddress address = new PostalAddress();
        address.setCity(in.getCity());
        address.setCountry(in.getCountry());
        address.setState(in.getState());
        address.setStreetAddress(in.getAddress1());
        address.setStreetAddress2(in.getAddress2());
        address.setZip(in.getZip());
        address.setType("Business");
        if((address.getStreetAddress() != null && !address.getStreetAddress().equals(""))
                || (address.getStreetAddress2() != null && !address.getStreetAddress2().equals(""))
                || (address.getCity() != null && !address.getCity().equals(""))
                || (address.getZip() != null && !address.getZip().equals(""))
                || (address.getState() != null && !address.getState().equals(""))){
            requester.getAddresses().add(address);
        }

        // the UI expects the contact methods in this order: Phone, Fax, Email
        List<ContactMethod> contactMethod = new ArrayList<>();
        requester.setContactMethods(contactMethod);
        ContactMethod phone = buildContactMethod("phone", in.getPhone());
        if (phone.getValue() != null && !phone.getValue().equals(""))
        {
            requester.getContactMethods().add(0, phone);
        }
        else
        {
            requester.getContactMethods().add(0, null);
        }
        ContactMethod fax = buildContactMethod("fax", null);
        if (fax.getValue() != null && !fax.getValue().equals(""))
        {
            requester.getContactMethods().add(1, fax);
        }
        else
        {
            requester.getContactMethods().add(1, null);
        }
        ContactMethod email = buildContactMethod("email", in.getEmail());
        if (email.getValue() != null && !email.getValue().equals(""))
        {
            requester.getContactMethods().add(2, email);
        }
        else
        {
            requester.getContactMethods().add(2, null);
        }

        return request;
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

}
