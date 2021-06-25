package gov.foia.web.api;

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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFileTypesException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.service.PersonService;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.search.exception.SolrException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.foia.dao.ResponseInstallmentDao;
import gov.foia.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import gov.foia.service.PortalRequestService;

/**
 * @author sasko.tanaskoski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class PortalRequestAPIController
{
    private final Logger log = LogManager.getLogger(getClass());

    private PortalRequestService portalRequestService;
    private PersonService personService;
    private ResponseInstallmentDao responseInstallmentDao;
    private TranslationService translationService;

    @RequestMapping(value = "/external/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PortalFOIARequestStatus> getExternalRequests(PortalFOIARequestStatus requestStatus)
            throws AcmObjectNotFoundException
    {
        return getPortalRequestService().getExternalRequests(requestStatus);
    }

    @RequestMapping(value = "/external/user/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PortalFOIARequestStatus> getLoggedUserExternalRequests(Authentication auth,
            @RequestParam(value = "emailAddress") String emailAddress, @RequestParam(value="requestId") String requestId)
            throws AcmObjectNotFoundException
    {
        return getPortalRequestService().getLoggedUserExternalRequests(emailAddress, requestId);
    }

    @RequestMapping(value = "/external/readingroom", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PortalFOIAReadingRoom> getReadingRoom(PortalFOIAReadingRoom readingRoom, Authentication auth)
            throws SolrException, JSONException, ParseException
    {
        return getPortalRequestService().getReadingRoom(readingRoom, auth);
    }

    @RequestMapping(value = "/external/checkRequestStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PortalFOIARequest checkRequestStatus(PortalFOIARequest portalFOIARequest) throws JSONException
    {
        return getPortalRequestService().checkRequestStatus(portalFOIARequest);
    }

    @RequestMapping(value = "/external/requestDownloadTriggered/{requestId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> requestDownloadTriggered(@PathVariable("requestId") String requestNumber)
    {
        try
        {
            getPortalRequestService().sendRequestDownloadedEmailToOfficersGroup(requestNumber);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/external/withdrawRequest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> withdrawRequest(@RequestBody String withdrawRequestMessage, Authentication auth)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            WithdrawRequest withdrawRequest = mapper.readValue(withdrawRequestMessage, WithdrawRequest.class);

            getPortalRequestService().createRequestWithdrawalTask(withdrawRequest, auth);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{personId}/images", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.IMAGE_PNG_VALUE })
    @ResponseBody
    public ResponseEntity uploadImage(@PathVariable("personId") Long personId,
            @RequestPart(value = "file", required = false) MultipartFile image,
            Authentication auth) throws AcmCreateObjectFailedException,
            AcmUpdateObjectFailedException, IOException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFileTypesException
    {
        Person person = personService.get(personId);

        try
        {
            log.debug("Insert Image for a Person: [{}];", personId);

            EcmFile uploadedFile = personService.insertImageForPortalPerson(person, image, image.getContentType(), auth);

            return new ResponseEntity<>(uploadedFile, HttpStatus.OK);
        }
        catch (PipelineProcessException | PersistenceException e)
        {
            log.error("Error while saving Person: [{}]", person, e);
            throw new AcmCreateObjectFailedException("Person", e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/external/responseInstallment/{requestNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity getResponseInstallmentDetails(@PathVariable("requestNumber") String requestNumber) throws Exception
    {
        if(getResponseInstallmentDao().checkIfInstallmentIsAvailableForDwonload(requestNumber))
        {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        throw new Exception(translationService.translate(NotificationConstants.PORTAL_RESPONSE_EXPIRY));
    }

    /**
     * @return the portalRequestService
     */
    public PortalRequestService getPortalRequestService()
    {
        return portalRequestService;
    }

    /**
     * @param portalRequestService
     *            the portalRequestService to set
     */
    public void setPortalRequestService(PortalRequestService portalRequestService)
    {
        this.portalRequestService = portalRequestService;
    }

    public PersonService getPersonService()
    {
        return personService;
    }

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public ResponseInstallmentDao getResponseInstallmentDao()
    {
        return responseInstallmentDao;
    }

    public void setResponseInstallmentDao(ResponseInstallmentDao responseInstallmentDao)
    {
        this.responseInstallmentDao = responseInstallmentDao;
    }

    public TranslationService getTranslationService()
    {
        return translationService;
    }

    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }
}
