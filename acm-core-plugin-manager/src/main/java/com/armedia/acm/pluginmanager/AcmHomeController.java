package com.armedia.acm.pluginmanager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/home")
public class AcmHomeController
{
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView homePage()
    {
        ModelAndView retval = new ModelAndView("home");
        retval.getModel().put("pluginName", "testPlugin");

        return retval;
    }

}
