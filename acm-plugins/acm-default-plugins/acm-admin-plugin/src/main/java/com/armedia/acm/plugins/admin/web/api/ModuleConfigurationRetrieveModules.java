package com.armedia.acm.plugins.admin.web.api;

/**
 * Created by admin on 6/26/15.
 */

import com.armedia.acm.plugins.admin.exception.AcmModuleConfigurationException;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class ModuleConfigurationRetrieveModules  {
    private Logger log = LoggerFactory.getLogger(getClass());

    private ModuleConfigurationService moduleConfigurationService;


    @RequestMapping(value = "/moduleconfiguration/modules", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<Map<String, String>> retrieveModules() throws IOException, AcmModuleConfigurationException {

        try {
            return moduleConfigurationService.retrieveModules();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't retrieve roles", e);
            }
            throw new AcmModuleConfigurationException("Can't retrieve modules", e);
        }
    }

    public void setModuleConfigurationService(ModuleConfigurationService moduleConfigurationService) {
        this.moduleConfigurationService = moduleConfigurationService;
    }
}
