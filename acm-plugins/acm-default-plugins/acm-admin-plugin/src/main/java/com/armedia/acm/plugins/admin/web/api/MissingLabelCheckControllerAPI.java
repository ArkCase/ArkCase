package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.services.labels.service.LabelCheckService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lin on 11/27/18.
 */

@Controller
@RequestMapping({"/api/v1/api/labelcheck" ,"/api/latest/api/labelcheck" })
public class MissingLabelCheckControllerAPI
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private LabelCheckService labelCheckService_;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<String> checkMissingLabel(){
        List<String> resultList = labelCheckService_.checkLabel();
        log.info(resultList.toString());
        return resultList;
    }

    public LabelCheckService getLabelCheckService()
    {
        return labelCheckService_;
    }

    public void setLabelCheckService(LabelCheckService labelCheckService){
        labelCheckService_ = labelCheckService;
    }
}
