package com.armedia.acm.correspondence.web.api;

import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.spring.SpringContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping( { "/api/v1/service/correspondence", "/api/latest/service/correspondence"} )
public class ListCorrespondenceTemplatesAPIController
{
    private SpringContextHolder contextHolder;

    @RequestMapping(value = "/listTemplates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> listTemplates(
            Authentication auth,
            HttpSession session)
    {
        Collection<CorrespondenceTemplate> templates = getContextHolder().getAllBeansOfType(CorrespondenceTemplate.class).values();

        List<String> retval = new ArrayList<>();

        for (CorrespondenceTemplate template : templates )
        {
            retval.add(template.getTemplateFilename());
        }

        Collections.sort(retval);

        return retval;
    }

    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }
}
