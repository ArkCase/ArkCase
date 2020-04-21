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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.services.exemption.exception.DeleteExemptionCodeException;
import com.armedia.acm.services.exemption.exception.SaveExemptionCodeException;
import com.armedia.acm.services.exemption.exception.UpdateExemptionStatuteException;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.exemption.service.ExemptionService;

/**
 * Created by ana.serafimoska
 */

@Controller
@RequestMapping({ "/api/v1/service/exemption", "/api/latest/service/exemption" })
public class ExemptionCodeAPIController
{
    private ExemptionService exemptionService;

    @RequestMapping(value = "/tags", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ExemptionCode> saveExemptionCodes(@RequestBody ExemptionCode exemptionCodes,
            Authentication authentication) throws SaveExemptionCodeException
    {
        String user = authentication.getName();
        return exemptionService.saveExemptionCodes(exemptionCodes, user);

    }

    @RequestMapping(value = "/{tagId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteExemptionCode(@PathVariable Long tagId) throws DeleteExemptionCodeException
    {
        exemptionService.deleteExemptionCode(tagId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/statute", method = RequestMethod.PUT)
    @ResponseBody
    public void updateExemptionStatute(@RequestBody ExemptionCode exemptionData) throws UpdateExemptionStatuteException
    {
        exemptionService.updateExemptionStatute(exemptionData);
    }

    public ExemptionService getExemptionService()
    {
        return exemptionService;
    }

    public void setExemptionService(ExemptionService exemptionService)
    {
        this.exemptionService = exemptionService;
    }
}
