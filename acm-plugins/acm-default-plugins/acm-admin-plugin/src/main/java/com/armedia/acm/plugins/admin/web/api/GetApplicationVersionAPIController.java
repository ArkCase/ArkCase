package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.web.api.service.ApplicationMetaInfoService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin/application/version" })
public class GetApplicationVersionAPIController
{
    private ApplicationMetaInfoService applicationMetaInfoService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> getApplicationVersion()
    {
        return Collections.singletonMap("applicationVersion", applicationMetaInfoService.getVersion());
    }

    public ApplicationMetaInfoService getApplicationMetaInfoService() {
        return applicationMetaInfoService;
    }

    public void setApplicationMetaInfoService(ApplicationMetaInfoService applicationMetaInfoService) {
        this.applicationMetaInfoService = applicationMetaInfoService;
    }
}
