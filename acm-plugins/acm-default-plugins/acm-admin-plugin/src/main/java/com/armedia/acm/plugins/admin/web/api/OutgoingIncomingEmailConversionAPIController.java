package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.model.DocumentUploadPolicyConfig;
import com.armedia.acm.plugins.admin.model.OutgoingIncomingEmailConversionConfig;
import com.armedia.acm.plugins.admin.service.OutgoingIncomingEmailConversionConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class OutgoingIncomingEmailConversionAPIController
{
    private Logger log = LogManager.getLogger(getClass().getName());

    private OutgoingIncomingEmailConversionConfigurationService outgoingIncomingEmailConversionConfigurationService;


    @RequestMapping(value = "/outgoingIncomingEmailConversion", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOutgoingIncomingEmailConversionConfiguration()
    {
        log.debug("Reading Outgoing/Incoming Email conversion configuration");
        return new ResponseEntity<>(getOutgoingIncomingEmailConversionConfigurationService().getOutgoingIncomingEmailConversionConfiguration(), HttpStatus.OK);
    }

    @RequestMapping(value = "/outgoingIncomingEmailConversion", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> saveOutgoingIncomingEmailConversionConfiguration(@RequestBody OutgoingIncomingEmailConversionConfig outgoingIncomingEmailConversionConfig)
    {
        try
        {
            log.debug("Saving Outgoing/Incoming Email conversion configuration");
            getOutgoingIncomingEmailConversionConfigurationService().saveOutgoingIncomingEmailConversionConfiguration(outgoingIncomingEmailConversionConfig);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error("Error during saving Outgoing/Incoming Email conversion configuration", e);
            throw e;
        }
    }

    public OutgoingIncomingEmailConversionConfigurationService getOutgoingIncomingEmailConversionConfigurationService()
    {
        return outgoingIncomingEmailConversionConfigurationService;
    }

    public void setOutgoingIncomingEmailConversionConfigurationService(OutgoingIncomingEmailConversionConfigurationService outgoingIncomingEmailConversionConfigurationService)
    {
        this.outgoingIncomingEmailConversionConfigurationService = outgoingIncomingEmailConversionConfigurationService;
    }
}
