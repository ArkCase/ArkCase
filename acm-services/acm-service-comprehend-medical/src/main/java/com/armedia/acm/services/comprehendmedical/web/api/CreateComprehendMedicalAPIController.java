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

import com.armedia.acm.services.comprehendmedical.model.ComprehendMedical;
import com.armedia.acm.services.comprehendmedical.sevice.ArkCaseComprehendMedicalService;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
@Controller
@RequestMapping({ "/api/v1/service/comprehendmedical", "/api/latest/service/comprehendmedical" })
public class CreateComprehendMedicalAPIController
{
    private ArkCaseComprehendMedicalService arkCaseComprehendMedicalService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MediaEngine createComprehendMedical(@RequestBody ComprehendMedical comprehendMedical) throws CreateMediaEngineException
    {
        return getArkCaseComprehendMedicalService().create(comprehendMedical);
    }

    @RequestMapping(value = "/{mediaVersionId}/automatic", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MediaEngine createAutomaticComprehendMedical(@PathVariable(value = "mediaVersionId") Long mediaVersionId)
            throws CreateMediaEngineException
    {
        return getArkCaseComprehendMedicalService().create(mediaVersionId, MediaEngineType.AUTOMATIC);
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
