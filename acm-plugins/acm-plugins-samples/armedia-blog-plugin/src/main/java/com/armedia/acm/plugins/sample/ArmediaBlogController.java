package com.armedia.acm.plugins.sample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/plugin/sample/armediaBlog")
public class ArmediaBlogController
{
    private String feedUrl;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView homePage()
    {
        ModelAndView retval = new ModelAndView("armediaBlog");
        retval.getModel().put("feedUrl", getFeedUrl());

        return retval;
    }

    public String getFeedUrl()
    {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl)
    {
        this.feedUrl = feedUrl;
    }
}
