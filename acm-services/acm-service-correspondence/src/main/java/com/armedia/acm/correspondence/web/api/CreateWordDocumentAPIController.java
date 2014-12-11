package com.armedia.acm.correspondence.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.mule.api.MuleException;
import org.mule.api.security.Authentication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Created by marjan.stefanoski on 08.12.2014.
 */

@Controller
@RequestMapping( { "/api/v1/plugin/correspondence", "/api/latest/plugin/correspondence"} )
public class CreateWordDocumentAPIController {

    @RequestMapping(value = "/createDoc", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE
    })
    @ResponseBody
    public EcmFile createWordDoc(
            @RequestParam("objectId") Long objectId,
            @RequestParam("objectType") String objectType,
            @RequestHeader("correspondenceType") String correspondenceType,
            Authentication authentication) throws AcmCreateObjectFailedException, AcmObjectNotFoundException, MuleException {

//        CreateWordDocFactory createWordDocFactory = new CreateWordDocFactory();
//        WordDocFromTemplate wordDocFromTemplate  = createWordDocFactory.getWordCreator(CorrespondenceType.getCorrespondenceType(correspondenceType));
//        EcmFile retFile  =  wordDocFromTemplate.create(ObjectType.getObjectType(objectType), Long.toString(objectId));
//        return retFile;
        return null;
    }
}
