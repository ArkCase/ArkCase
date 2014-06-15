package com.armedia.acm.plugins.search.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.armedia.acm.web.AcmPageDescriptor;

@RequestMapping("/plugin/search")
public class SearchUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPageDescriptor pageDescriptorSimple;

    //@RequestMapping(value = "/simple", method = RequestMethod.GET)
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView simpleSearch()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("simpleSearch");
        retval.addObject("pageDescriptor", getPageDescriptorSimple());
        return retval;
    }

    public AcmPageDescriptor getPageDescriptorSimple() {
        return pageDescriptorSimple;
    }

    public void setPageDescriptorSimple(AcmPageDescriptor pageDescriptorSimple) {
        this.pageDescriptorSimple = pageDescriptorSimple;
    }
}
