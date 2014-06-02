package com.armedia.acm.plugins.task.web;

import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.web.AcmPageDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@RequestMapping("/plugin/task")
public class TaskUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmPageDescriptor pageDescriptorWizard;
    private AcmPageDescriptor pageDescriptorList;


    @RequestMapping(method = RequestMethod.GET)
    //@RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView listTask()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("taskList");
        retval.addObject("pageDescriptor",  getPageDescriptorList());
        return retval;
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    //@RequestMapping(value = "/detail/{taskId}", method = RequestMethod.GET)
    public ModelAndView showTask(@PathVariable(value = "taskId") Long taskId)
//    @RequestMapping(value = "/detail", method = RequestMethod.GET)
//    public ModelAndView showTask()
    {
        Long a = taskId;
        ModelAndView retval = new ModelAndView();
        retval.setViewName("taskDetail");
        retval.addObject("pageDescriptor",  getPageDescriptorList());
        return retval;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView createTask(
            @Valid AcmTask task,
            BindingResult bindingResult,
            Authentication authentication)
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("taskWizard");
        retval.addObject("pageDescriptor",  getPageDescriptorWizard());
        return retval;
    }


    public AcmPageDescriptor getPageDescriptorWizard() {
        return pageDescriptorWizard;
    }

    public void setPageDescriptorWizard(AcmPageDescriptor pageDescriptorWizard) {
        this.pageDescriptorWizard = pageDescriptorWizard;
    }

    public AcmPageDescriptor getPageDescriptorList() {
        return pageDescriptorList;
    }

    public void setPageDescriptorList(AcmPageDescriptor pageDescriptorList) {
        this.pageDescriptorList = pageDescriptorList;
    }
}
