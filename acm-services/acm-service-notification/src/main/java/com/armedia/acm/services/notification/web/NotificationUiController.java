package com.armedia.acm.services.notification.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/plugin/notification")
public class NotificationUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showNotificationPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("notification");
        return retval;
    }
}
