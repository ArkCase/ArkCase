package com.armedia.acm.plugins.person.web.api;


import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping({"/api/v1/plugin/person", "/api/latest/plugin/person"})
public class GetPersonTypesAPIController {

    private Properties personProperties;

    @RequestMapping(
            value = "types",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    List<String> getPersonTypes() throws AcmListObjectsFailedException {

        String commaSeparated = personProperties.getProperty("person.types");
        if (StringUtils.isEmpty(commaSeparated))
            throw new AcmListObjectsFailedException("PersonTypes", "person property with key [person.types] is not set or is empty", null);

        String[] retval = commaSeparated.split(",");
        return Arrays.asList(retval);

    }

    public void setPersonProperties(Properties personProperties) {
        this.personProperties = personProperties;
    }
}
