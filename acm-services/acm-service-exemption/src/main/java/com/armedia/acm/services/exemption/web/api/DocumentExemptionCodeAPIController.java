package com.armedia.acm.services.exemption.web.api;

/*-
 * #%L
 * ACM Service: Exemption
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

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.services.exemption.exception.GetExemptionCodeException;
import com.armedia.acm.services.exemption.exception.SaveExemptionCodeException;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.exemption.service.DocumentExemptionService;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;

/**
 * Created by ana.serafimoska
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm/file", "/api/latest/service/ecm/file" })
public class DocumentExemptionCodeAPIController
{
    private final Logger log = LogManager.getLogger(getClass());
    private DocumentExemptionService documentExemptionService;
    private UserTrackerService userTrackerService;

    /**
     * Update FOIA document exemption codes.
     *
     * @param fileId
     *            FOIA document identifier
     * @param tags
     *            list of redaction tags (exemption codes)
     * @param auth
     *            authentication token
     * @param session
     */
    @RequestMapping(value = "/{fileId}/tags", method = RequestMethod.POST)
    public void setExemptionCodes(@PathVariable(value = "fileId") String fileId, @RequestParam(value = "tags") List<String> tags,
            Authentication auth,
            HttpSession session) throws SaveExemptionCodeException
    {
        String user = auth.getName();
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getUserTrackerService().trackUser(ipAddress);

        // the file id may have a version identifier attached eg. 479:13.0
        String fileIdOnly = fileId == null ? null
                : fileId.contains(":") ? StringUtils.substringBefore(fileId, ":")
                        : fileId;
        log.debug("File id without version identifier: {}", fileIdOnly);

        Long realFileId = Long.valueOf(fileIdOnly);

        log.debug("User [{}] coming from [{}] is updating exemption codes [{}] of document [{}]", user, ipAddress, tags, fileId);
        documentExemptionService.updateExemptionCodes(realFileId, tags, user);
        log.debug("Exemption codes [{}] of document [{}] updated", tags, fileId);
    }

    /**
     * Update FOIA document exemption codes status
     *
     * @param fileId
     *            FOIA document identifier
     * @param status
     *            status of redaction tags (exemption codes)
     * @param auth
     *            authentication token
     * @param session
     *            HTTP session
     */
    @RequestMapping(value = "/{fileId}/update/tags", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity updateExemptionCodesStatusAfterBurn(@PathVariable(value = "fileId") String fileId,
            @RequestParam(value = "status") String status,
            Authentication auth,
            HttpSession session) throws SaveExemptionCodeException
    {
        String user = auth.getName();
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getUserTrackerService().trackUser(ipAddress);

        // the file id may have a version identifier attached eg. 479:13.0
        String fileIdOnly = fileId == null ? null
                : fileId.contains(":") ? StringUtils.substringBefore(fileId, ":")
                        : fileId;
        log.debug("File id without version identifier: {}", fileIdOnly);/**/

        Long realFileId = Long.valueOf(fileIdOnly);

        documentExemptionService.updateExemptionCodesStatusAfterBurn(realFileId, status, user);

        log.debug("updateExemptionCodesStatusFromSnowbound return: {}", "{\"success\": \"true\"}");

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Update FOIA document exemption codes.
     *
     * @param fileId
     *            FOIA document identifier
     * @param tags
     *            list of redaction tags (exemption codes)
     * @param auth
     *            authentication token
     * @param session
     *            HTTP session
     */
    @RequestMapping(value = "/{fileId}/update/tags/manually", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void setExemptionCodesManually(
            @PathVariable(value = "fileId") String fileId,
            @RequestParam(value = "tags") List<String> tags,
            Authentication auth,
            HttpSession session) throws SaveExemptionCodeException
    {
        String user = auth.getName();
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getUserTrackerService().trackUser(ipAddress);

        // the file id may have a version identifier attached eg. 479:13.0
        String fileIdOnly = fileId == null ? null
                : fileId.contains(":") ? StringUtils.substringBefore(fileId, ":")
                        : fileId;
        log.debug("File id without version identifier: {}", fileIdOnly);

        Long realFileId = Long.valueOf(fileIdOnly);

        log.debug("User [{}] coming from [{}] is updating exemption codes [{}] of document [{}]", user, ipAddress, tags, fileId);
        documentExemptionService.saveExemptionCodesManually(realFileId, tags, user);
        log.debug("Exemption codes [{}] of document [{}] updated", tags, fileId);
    }

    /**
     * Get FOIA document exemption codes.
     *
     * @param caseId
     *            FOIA document identifier
     * @param auth
     *            authentication token
     * @param session
     *            HTTP session
     */
    @RequestMapping(value = "/{caseId}/tags/{fileId}", method = RequestMethod.GET)
    public @ResponseBody List<ExemptionCode> getExemptionCodes(
            @PathVariable(value = "caseId") Long caseId,
            @PathVariable(value = "fileId") Long fileId,
            Authentication auth,
            HttpSession session) throws GetExemptionCodeException
    {
        List<ExemptionCode> tags;
        String user = auth.getName();
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getUserTrackerService().trackUser(ipAddress);

        log.debug("User [{}] coming from [{}] is getting exemption codes of foia request (case file) [{}]", user, ipAddress, caseId);
        tags = documentExemptionService.getExemptionCodes(caseId, fileId);
        log.debug("Exemption codes [{}] of foia request (case file) [{}] returned", tags, caseId);
        return tags;
    }

    public DocumentExemptionService getDocumentExemptionService()
    {
        return documentExemptionService;
    }

    public void setDocumentExemptionService(DocumentExemptionService documentExemptionService)
    {
        this.documentExemptionService = documentExemptionService;
    }

    public UserTrackerService getUserTrackerService()
    {
        return userTrackerService;
    }

    public void setUserTrackerService(UserTrackerService userTrackerService)
    {
        this.userTrackerService = userTrackerService;
    }
}
