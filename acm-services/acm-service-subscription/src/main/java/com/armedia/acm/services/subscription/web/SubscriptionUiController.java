package com.armedia.acm.services.subscription.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.Map;

@RequestMapping("/plugin/subscription")
public class SubscriptionUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView search()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("subscription");
        return retval;
    }
}
