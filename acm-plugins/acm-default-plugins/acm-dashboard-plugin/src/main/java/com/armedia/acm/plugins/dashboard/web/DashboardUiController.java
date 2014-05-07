package com.armedia.acm.plugins.dashboard.web;

import com.armedia.acm.pluginmanager.AcmPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Arrays;

@RequestMapping("/plugin/dashboard")
public class DashboardUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView complaint()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("home");
        return retval;
    }

}
