package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLabelManagementException;
import com.armedia.acm.plugins.admin.service.LabelManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by sergey on 2/14/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelManagementRetrieveLanguages {
    private Logger log = LoggerFactory.getLogger(getClass());
    private LabelManagementService labelManagementService;

    @RequestMapping(value = "/labelmanagement/languages", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<String> retrieveLanguages(
            HttpServletResponse response) throws IOException, AcmLabelManagementException {

        try {
            return labelManagementService.getLanguages();
        } catch (Exception e) {
            String msg = "Can't retrieve languages";
            log.error(msg, e);
            throw new AcmLabelManagementException(msg, e);
        }
    }

    public void setLabelManagementService(LabelManagementService labelManagementService) {
        this.labelManagementService = labelManagementService;
    }
}