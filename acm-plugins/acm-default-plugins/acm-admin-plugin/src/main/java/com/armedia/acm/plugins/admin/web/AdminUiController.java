package com.armedia.acm.plugins.admin.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.armedia.acm.web.AcmPageDescriptor;

@RequestMapping("/plugin/admin")
public class AdminUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPageDescriptor pageDescriptor;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showHomePage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("admin");
        retval.addObject("pageDescriptor", getPageDescriptor());
        return retval;
    }

    public AcmPageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }

    public void setPageDescriptor(AcmPageDescriptor pageDescriptor) {
        this.pageDescriptor = pageDescriptor;
    }
}
