package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.services.dataaccess.service.DataAccessControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class DataAccessControlAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private DataAccessControlService dataAccessControlService;

    @RequestMapping(value = "/getDataAccessControlProperties", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> loadDataAccessControlProperties()
    {
        return new ResponseEntity<>(getDataAccessControlService().loadProperties(), HttpStatus.OK);
    }

    public DataAccessControlService getDataAccessControlService() {
        return dataAccessControlService;
    }

    public void setDataAccessControlService(DataAccessControlService dataAccessControlService) {
        this.dataAccessControlService = dataAccessControlService;
    }
}
