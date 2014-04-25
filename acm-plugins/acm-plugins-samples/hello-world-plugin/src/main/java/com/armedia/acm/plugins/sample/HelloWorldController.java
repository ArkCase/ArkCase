package com.armedia.acm.plugins.sample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/plugin/sample/helloWorld")
public class HelloWorldController
{
    private String name;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView homePage()
    {
        ModelAndView retval = new ModelAndView("helloWorld");
        retval.getModel().put("name", getName());

        return retval;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
