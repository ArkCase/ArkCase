package com.armedia.acm.services.users.web.api;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.core.AcmParticipantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = { "/api/v1/users", "/api/latest/users" } )
public class FindParticipantTypesForObjectTypeNameAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AcmApplication acmApplication;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/participantTypesForObjectTypeName/{objectTypeName}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<AcmParticipantType> participantTypesForObjectTypeName(
            @PathVariable(value = "objectTypeName") String objectTypeName)
    {
        if ( log.isDebugEnabled() )
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
