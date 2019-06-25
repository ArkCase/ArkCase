package com.armedia.acm.services.users.web.api;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.core.AcmParticipantType;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = { "/api/v1/users", "/api/latest/users" })
public class FindParticipantTypesForObjectTypeNameAPIController
{
    private final Logger log = LogManager.getLogger(getClass());

    private AcmApplication acmApplication;

    @RequestMapping(method = RequestMethod.GET, value = "/participantTypesForObjectTypeName/{objectTypeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AcmParticipantType> participantTypesForObjectTypeName(
            @PathVariable(value = "objectTypeName") String objectTypeName)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Looking for participant types for '" + objectTypeName + "'");
        }

        AcmObjectType objectType = getAcmApplication().getBusinessObjectByName(objectTypeName);
        List<AcmParticipantType> retval = objectType.getParticipantTypes();
        return retval;
    }

    public AcmApplication getAcmApplication()
    {
        return acmApplication;
    }

    public void setAcmApplication(AcmApplication acmApplication)
    {
        this.acmApplication = acmApplication;
    }
}
