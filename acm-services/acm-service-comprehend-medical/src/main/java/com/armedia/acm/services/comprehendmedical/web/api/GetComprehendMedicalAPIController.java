package com.armedia.acm.services.comprehendmedical.web.api;

/*-
 * #%L
 * ACM Service: Transcribe
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

import com.armedia.acm.services.comprehendmedical.sevice.ArkCaseComprehendMedicalService;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
@Controller
@RequestMapping({ "/api/v1/service/comprehendmedical", "/api/latest/service/comprehendmedical" })
public class GetComprehendMedicalAPIController
{
    private ArkCaseComprehendMedicalService arkCaseComprehendMedicalService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MediaEngine getComprehendMedical(@PathVariable(value = "id") Long id) throws GetMediaEngineException
    {
        return getArkCaseComprehendMedicalService().get(id);
    }

    @RequestMapping(value = "/media/{mediaVersionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MediaEngine getComprehendMedicalByMediaId(@PathVariable(value = "mediaVersionId") Long mediaVersionId) throws GetMediaEngineException
    {
        return getArkCaseComprehendMedicalService().getByMediaVersionId(mediaVersionId);
    }

    @RequestMapping(value = "/mediaFailure/{mediaVersionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> getFailureMessage(@PathVariable(value = "mediaVersionId") Long mediaVersionId) throws GetMediaEngineException
    {
        return getArkCaseComprehendMedicalService().getFailureReasonMessage(mediaVersionId);
    }

    public ArkCaseComprehendMedicalService getArkCaseComprehendMedicalService()
    {
        return arkCaseComprehendMedicalService;
    }

    public void setArkCaseComprehendMedicalService(ArkCaseComprehendMedicalService arkCaseComprehendMedicalService)
    {
        this.arkCaseComprehendMedicalService = arkCaseComprehendMedicalService;
    }
}
