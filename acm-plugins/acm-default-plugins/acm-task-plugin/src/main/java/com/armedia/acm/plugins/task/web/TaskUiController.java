package com.armedia.acm.plugins.task.web;

import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.web.AcmPageDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@RequestMapping("/plugin/task")
public class TaskUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmPageDescriptor pageDescriptorList;
    private AcmPageDescriptor pageDescriptorWizard;


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView openTaskList()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("taskList");
        retval.addObject("pageDescriptor",  getPageDescriptorList());
        return retval;
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    public ModelAndView openTaskDetail(@PathVariable(value = "taskId") Long taskId)
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("taskList");
        retval.addObject("taskId",  taskId);
        retval.addObject("pageDescriptor",  getPageDescriptorList());
        return retval;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openTaskWizard(
            @RequestParam(value = "parentType", required = false) String parentType
            ,@RequestParam(value = "parentId", required = false) Integer parentId
    ) {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("taskWizard");
        retval.addObject("pageDescriptor",  getPageDescriptorWizard());
        retval.addObject("parentType",  parentType);
        retval.addObject("parentId",  parentId);
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
