package gov.foia.web.api;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import gov.foia.exception.DeleteExemptionStatuteException;
import gov.foia.exception.GetExemptionStatuteException;
import gov.foia.exception.SaveExemptionStatuteException;
import gov.foia.model.ExemptionStatute;
import gov.foia.service.FOIAExemptionStatuteService;
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
public class FOIAExemptionStatuteAPIController
{

    private final Logger log = LogManager.getLogger(getClass());
    private FOIAExemptionStatuteService foiaExemptionStatuteService;

    @RequestMapping(value = "/{parentObjectId}/{parentObjectType}/statutes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ExemptionStatute> getExemptionStatutesOnRequest(@PathVariable Long parentObjectId, @PathVariable String parentObjectType)
            throws GetExemptionStatuteException
    {
        return getFoiaExemptionStatuteService().getExemptionStatutesOnRequest(parentObjectId, parentObjectType);
    }

    @RequestMapping(value = "/file/{fileId}", method = RequestMethod.GET)
    public @ResponseBody List<ExemptionStatute> getExemptionStatutesOnDocument(
            @PathVariable(value = "fileId") Long fileId,
            Authentication auth,
            HttpSession session) throws GetExemptionStatuteException
    {
        List<ExemptionStatute> statutes;
        String user = auth.getName();

        log.debug("User [{}] is getting exemption statutes of file [{}]", user, fileId);
        statutes = getFoiaExemptionStatuteService().getExemptionStatutesOnDocument(fileId);
        log.debug("Exemption statutes [{}] of file [{}] returned", statutes, fileId);
        return statutes;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ExemptionStatute saveExemptionStatute(@RequestBody ExemptionStatute exemptionStatute,
            Authentication authentication) throws SaveExemptionStatuteException
    {
        return getFoiaExemptionStatuteService().saveExemptionStatute(exemptionStatute);
    }

    @RequestMapping(value = "/file/{fileId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ExemptionStatute saveExemptionStatutesOnDocument(
            @RequestBody ExemptionStatute exemptionStatute,
            @PathVariable(value = "fileId") String fileId,
            Authentication auth,
            HttpSession session) throws SaveExemptionStatuteException
    {
        String user = auth.getName();

        String fileIdOnly = removeVersionFromFileId(fileId);
        Long realFileId = Long.valueOf(fileIdOnly);

        log.debug("User [{}] is saving exemption statute [{}] of document [{}]", user, exemptionStatute, fileId);
        return getFoiaExemptionStatuteService().saveExemptionStatutesOnDocument(realFileId, exemptionStatute);
    }

    @RequestMapping(value = "/{statuteId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteExemptionStatute(@PathVariable(value = "statuteId") Long statuteId) throws DeleteExemptionStatuteException
    {
        getFoiaExemptionStatuteService().deleteExemptionStatute(statuteId);
        return new ResponseEntity(HttpStatus.OK);
    }


    private String removeVersionFromFileId(String fileId)
    {

        // the file id may have a version identifier attached
        String fileIdOnly = fileId == null ? null
                : fileId.contains(":") ? StringUtils.substringBefore(fileId, ":")
                        : fileId;
        log.debug("File id without version identifier: {}", fileIdOnly);

        return fileIdOnly;

    }

    public FOIAExemptionStatuteService getFoiaExemptionStatuteService()
    {
        return foiaExemptionStatuteService;
    }

    public void setFoiaExemptionStatuteService(FOIAExemptionStatuteService foiaExemptionStatuteService)
    {
        this.foiaExemptionStatuteService = foiaExemptionStatuteService;
    }
}
