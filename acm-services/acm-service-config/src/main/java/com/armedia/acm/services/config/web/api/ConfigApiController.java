package com.armedia.acm.services.config.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.config.model.AcmConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.*;


@Controller
@RequestMapping({"/api/v1/service/config", "/api/latest/service/config"})
public class ConfigApiController {
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private List<AcmConfig> configList;

    @RequestMapping(value = "/{name}"
            ,method = RequestMethod.GET
            ,produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE}
    )
    @ResponseBody
    public Object getConfig (
            @PathVariable("name") String name
            ,Authentication authentication
            ,HttpSession session
    ) {
        String rc = "{}";
        try {
            rc =  configList.stream()
                    .filter(x -> x.getConfigName().equals(name))
                    .findFirst()
                    .get()
                    .getConfigAsJson()
                    ;
        } catch (NoSuchElementException e) {
            log.error(e.getMessage());
        }
        return rc;
    }

    public List<AcmConfig> getConfigList() {
        return configList;
    }

    public void setConfigList(List<AcmConfig> configList) {
        this.configList = configList;
    }
}
