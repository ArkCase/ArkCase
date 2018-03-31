package com.armedia.acm.plugins.admin.web.api;

/**
 * Created by admin on 6/26/15.
 */

import com.armedia.acm.plugins.admin.exception.AcmModuleConfigurationException;
import com.armedia.acm.plugins.admin.model.ModuleItem;
import com.armedia.acm.plugins.admin.service.ModuleConfigurationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class ModuleConfigurationRetrieveModules
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ModuleConfigurationService moduleConfigurationService;

    @RequestMapping(value = "/moduleconfiguration/modules", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<ModuleItem> retrieveModules() throws IOException, AcmModuleConfigurationException
    {
        try
        {
            return moduleConfigurationService.retrieveModules();
        }
        catch (Exception e)
        {
            log.error("Can't retrieve roles", e);
            throw new AcmModuleConfigurationException("Can't retrieve modules", e);
        }
    }

    @RequestMapping(value = "/moduleconfiguration/modules/paged", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<ModuleItem> findModulesPaged(
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows)
            throws IOException, AcmModuleConfigurationException
    {
        try
        {
            return moduleConfigurationService.findModulesPaged(startRow, maxRows, sortDirection);
        }
        catch (Exception e)
        {
            log.error("Can't retrieve modules", e);
            throw new AcmModuleConfigurationException("Can't retrieve modules", e);
        }
    }

    @RequestMapping(value = "/moduleconfiguration/modules", params = { "fn" }, method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<ModuleItem> findModulesByMatchingName(
            @RequestParam(value = "fn") String filterName,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows)
            throws IOException, AcmModuleConfigurationException
    {
        try
        {
            return moduleConfigurationService.findModulesByMatchingName(filterName, startRow, maxRows, sortDirection);
        }
        catch (Exception e)
        {
            log.error("Can't retrieve modules by matching name {}", filterName, e);
            throw new AcmModuleConfigurationException("Can't retrieve modules by matching name", e);
        }
    }

    public void setModuleConfigurationService(ModuleConfigurationService moduleConfigurationService)
    {
        this.moduleConfigurationService = moduleConfigurationService;
    }
}
