package com.armedia.acm.service.orbeon.forms.web.api;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.service.orbeon.forms.model.OrbeonForm;


@RequestMapping("/api/v1/forms/crud/acm")
public class OrbeonFormController implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());


    /**
     * The Orbeon request URL would look like this:
     * /crud/[APPLICATION_NAME]/[FORM_NAME]/(data|draft)/[FORM_DATA_ID]/data.xml
     * 
     * savemode: data | draft
     * 
     * @param in
     * @param auth
     */
    @RequestMapping(value = "/{formname}/{savemode}/{formdataid}/{file}", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public void saveFormData(Authentication auth,
    		@PathVariable("formname") String formName,
    		@PathVariable("savemode") String saveMode,
    		@PathVariable("formDataId") String formDataId,
    		@PathVariable("file") File file,
    		@RequestBody Object in
            ) {
       	log.info("Form name: " + formName + "; Save mode: " + 
            saveMode + "; form data id: " + formDataId + "; File name: " + file.getName());    		
    	log.info("Object data: " + in.toString());
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;		
	}
}
