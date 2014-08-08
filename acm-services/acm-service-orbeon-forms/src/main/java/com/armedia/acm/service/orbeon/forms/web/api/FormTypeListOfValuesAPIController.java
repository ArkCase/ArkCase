package com.armedia.acm.service.orbeon.forms.web.api;


import com.armedia.acm.configuration.ListOfValuesService;
import com.armedia.acm.configuration.LookupTableDescriptor;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/service/form", "/api/latest/service/form" })
public class FormTypeListOfValuesAPIController {
    
    private ListOfValuesService listOfValuesService;
    private LookupTableDescriptor typesDescriptor;
    

    @RequestMapping(
            value = "types",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<String> getFormTypes() throws AcmListObjectsFailedException
    {
        try
        {
            List<String> types = getListOfValuesService().lookupListOfStringValues(getTypesDescriptor());
            return types;
        }
        catch (DataAccessException e)
        {
            throw new AcmListObjectsFailedException("Form types", e.getMessage(), e);
        }
    }


    public ListOfValuesService getListOfValuesService()
    {
        return listOfValuesService;
    }

    public void setListOfValuesService(ListOfValuesService listOfValuesService)
    {
        this.listOfValuesService = listOfValuesService;
    }

    public LookupTableDescriptor getTypesDescriptor()
    {
        return typesDescriptor;
    }

    public void setTypesDescriptor(LookupTableDescriptor typesDescriptor)
    {
        this.typesDescriptor = typesDescriptor;
    }
}
