package gov.foia.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;

import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIAPerson;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIARequesterAssociation;
import gov.foia.model.PortalFOIARequest;
import gov.foia.model.PortalFOIARequestFile;

public class PortalCreateRequestService
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private UserTrackerService userTrackerService;

    private SaveFOIARequestService saveFOIARequestService;

    public FOIARequest createFOIARequest(PortalFOIARequest in)
            throws PipelineProcessException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        log.debug("Received request {}", in);

        getAuditPropertyEntityAdapter().setUserId(in.getUserId());
        String ipAddress = in.getIpAddress();

        Authentication auth = new UsernamePasswordAuthenticationToken(in.getUserId(), in.getUserId());

        SecurityContextHolder.getContext().setAuthentication(auth);

        getUserTrackerService().trackUser(ipAddress);

        FOIARequest request = populateRequest(in);

        List<MultipartFile> files = new ArrayList<>();
        for (PortalFOIARequestFile requestFile : in.getFiles())
        {
            try
            {
                files.add(portalRequestFileToMultipartFile(requestFile));
            }
            catch (IOException e)
            {
                log.error("Failed to receive file {}", requestFile.getFileName());
            }
        }

        FOIARequest saved = (FOIARequest) getSaveFOIARequestService().savePortalRequest(request, files, auth, ipAddress);

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

        InputStream input = new FileInputStream(file);
        OutputStream os = fileItem.getOutputStream();
        IOUtils.copy(input, os);

        return new CommonsMultipartFile(fileItem);

    }

    private FOIARequest populateRequest(PortalFOIARequest in)
    {
        FOIARequest request = new FOIARequest();

        request.setExternal(true);

        request.setRequestType(FOIAConstants.NEW_REQUEST_TYPE);
        request.setRequestSubType("FOIA");
        request.setComponentAgency("FOIA");
        request.setOriginalRequestNumber(in.getOriginalRequestNumber());
        request.setRequestCategory(in.getRequestCategory());
        request.setDeliveryMethodOfResponse(in.getDeliveryMethodOfResponse());

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
        requester.setPosition(in.getTitle());
        // requester.setCompany(in.getOrganization());

        if (in.getOrganization() != null && in.getOrganization().length() > 0)
        {
            Organization organization = new Organization();
            organization.setOrganizationValue(in.getOrganization());
            organization.setOrganizationType("Corporation");
            requester.getOrganizations().add(organization);

            PostalAddress orgAddress = new PostalAddress();
            orgAddress.setType("Business");
            organization.getAddresses().add(orgAddress);

            List<PersonOrganizationAssociation> personOrganizationAssociations = new ArrayList<>();
            PersonOrganizationAssociation personOrganizationAssociation = new PersonOrganizationAssociation();
            personOrganizationAssociation.setOrganization(organization);
            personOrganizationAssociation.setDefaultOrganization(true);
            personOrganizationAssociation.setPerson(requester);
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
        requester.getAddresses().add(address);

        // the UI expects the contact methods in this order: Phone, Fax, Email
        ContactMethod phone = buildContactMethod("phone", in.getPhone());
        requester.getContactMethods().add(phone);
        ContactMethod fax = buildContactMethod("fax", null);
        requester.getContactMethods().add(fax);
        ContactMethod email = buildContactMethod("email", in.getEmail());
        requester.getContactMethods().add(email);

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
