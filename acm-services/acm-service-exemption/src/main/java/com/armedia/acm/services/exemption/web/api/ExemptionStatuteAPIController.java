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

import com.armedia.acm.services.exemption.exception.DeleteExemptionStatuteException;
import com.armedia.acm.services.exemption.exception.GetExemptionStatuteException;
import com.armedia.acm.services.exemption.exception.SaveExemptionStatuteException;
import com.armedia.acm.services.exemption.model.ExemptionStatute;
import com.armedia.acm.services.exemption.service.ExemptionStatuteService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/service/exemption-statute", "/api/latest/service/exemption-statute" })
public class ExemptionStatuteAPIController
{

    private final Logger log = LogManager.getLogger(getClass());
    private ExemptionStatuteService exemptionStatuteService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ExemptionStatute saveExemptionStatute(@RequestBody ExemptionStatute exemptionStatute,
                                                 Authentication authentication) throws SaveExemptionStatuteException
    {
        String user = authentication.getName();
        return getExemptionStatuteService().saveExemptionStatute(exemptionStatute, user);

    }

    @RequestMapping(value = "/{statuteId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteExemptionStatute(@PathVariable Long statuteId) throws DeleteExemptionStatuteException
    {
        getExemptionStatuteService().deleteExemptionStatute(statuteId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/file/{fileId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void saveExemptionStatutesOnDocument(
            @PathVariable(value = "fileId") String fileId,
            @RequestParam(value = "statutes") List<String> statutes,
            Authentication auth,
            HttpSession session) throws SaveExemptionStatuteException
    {
        String user = auth.getName();

        String fileIdOnly = fileIdOnlyChecker(fileId);
        Long realFileId = Long.valueOf(fileIdOnly);

        log.debug("User [{}] is saving exemption statute [{}] of document [{}]", user, statutes, fileId);
        getExemptionStatuteService().saveExemptionStatutesOnDocument(realFileId, statutes, user);
        log.debug("Exemption statutes [{}] of document [{}] saved", statutes, fileId);
    }

    @RequestMapping(value = "/case/{caseId}/file/{fileId}", method = RequestMethod.GET)
    public @ResponseBody List<ExemptionStatute> getExemptionStatutesOnDocument(
            @PathVariable(value = "caseId") Long caseId,
            @PathVariable(value = "fileId") Long fileId,
            Authentication auth,
            HttpSession session) throws GetExemptionStatuteException
    {
        List<ExemptionStatute> statutes;
        String user = auth.getName();

        log.debug("User [{}] is getting exemption statutes of foia request (case file) [{}]", user, caseId);
        statutes = getExemptionStatuteService().getExemptionStatutesOnDocument(caseId, fileId);
        log.debug("Exemption statutes [{}] of foia request (case file) [{}] returned", statutes, caseId);
        return statutes;
    }

    private String fileIdOnlyChecker(String fileId)
    {

        // the file id may have a version identifier attached
        String fileIdOnly = fileId == null ? null
                : fileId.contains(":") ? StringUtils.substringBefore(fileId, ":")
                        : fileId;
        log.debug("File id without version identifier: {}", fileIdOnly);

        return fileIdOnly;

    }

    public ExemptionStatuteService getExemptionStatuteService()
    {
        return exemptionStatuteService;
    }

    public void setExemptionStatuteService(ExemptionStatuteService exemptionStatuteService)
    {
        this.exemptionStatuteService = exemptionStatuteService;
    }
}
