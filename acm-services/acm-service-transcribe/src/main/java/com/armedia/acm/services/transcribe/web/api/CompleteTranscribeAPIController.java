package com.armedia.acm.services.transcribe.web.api;

import com.armedia.acm.services.transcribe.exception.SaveTranscribeException;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/22/2018
 */
@Controller
@RequestMapping({ "/api/v1/service/transcribe", "/api/latest/service/transcribe" })
public class CompleteTranscribeAPIController
{
    private ArkCaseTranscribeService arkCaseTranscribeService;

    @RequestMapping(value = "/{id}/complete", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Transcribe completeTranscribe(@PathVariable(value = "id") Long id) throws SaveTranscribeException
    {
        return getArkCaseTranscribeService().complete(id);
    }

    public ArkCaseTranscribeService getArkCaseTranscribeService()
    {
        return arkCaseTranscribeService;
    }

    public void setArkCaseTranscribeService(ArkCaseTranscribeService arkCaseTranscribeService)
    {
        this.arkCaseTranscribeService = arkCaseTranscribeService;
    }
}
