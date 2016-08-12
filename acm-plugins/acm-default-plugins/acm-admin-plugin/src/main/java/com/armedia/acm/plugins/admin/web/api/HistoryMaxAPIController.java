package com.armedia.acm.plugins.admin.web.api;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by will.phillips on 8/12/2016.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class HistoryMaxAPIController
{
    @RequestMapping(value="/historymax", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Integer getHistoryMax()
    {
        return 30;
    }

    @RequestMapping(value="/historymax", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void setHistoryMax(Integer newMax)
    {
        if (newMax != null && newMax > 0)
        {
            // Save.
        }
    }
}
