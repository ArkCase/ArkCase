package com.armedia.acm.plugins.admin.web.api;
/*-
 * #%L
 * ACM Service: Labels Service
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.services.labels.service.LabelCheckService;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lin on 11/27/18.
 */

@Controller
@RequestMapping({"/api/v1/plugin/admin/labelcheck"})
public class MissingLabelCheckControllerAPI
{
    private Logger log = LogManager.getLogger(getClass());
    private LabelCheckService labelCheckService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<String> checkMissingLabel(){
        List<String> resultList = labelCheckService.checkLabel();
        Collections.sort(resultList);
        StringBuffer outputSB = new StringBuffer();
        for(String output : resultList){
            outputSB = outputSB.append(output+"\n");

        }
        log.info("Missing Label: \n" + outputSB.toString());

        return resultList;
    }

    public LabelCheckService getLabelCheckService()
    {
        return labelCheckService;
    }

    public void setLabelCheckService(LabelCheckService labelCheckService){
        this.labelCheckService = labelCheckService;
    }
}
