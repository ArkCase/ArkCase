package com.armedia.acm.services.tag.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.tag.model.AcmTag;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/tag", "/api/latest/service/tag"})
public class AddTagAPIController {

    @RequestMapping(value = "/{userId}/{fileId}/{tagName}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTag addTag(
            @PathVariable("userId") String userId,
            @PathVariable("fileId") String fileId,
            @PathVariable("tagName") String tagName,
            Authentication authentication ) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException {



            return null;
    }
}
