package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin/transcribe/configuration", "/api/latest/plugin/admin/transcribe/configuration" })
public class GetTranscribeConfigurationAPIController
{
    private ArkCaseTranscribeService arkCaseTranscribeService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TranscribeConfiguration getConfiguration() throws GetConfigurationException
    {
        return getArkCaseTranscribeService().getConfiguration();
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
