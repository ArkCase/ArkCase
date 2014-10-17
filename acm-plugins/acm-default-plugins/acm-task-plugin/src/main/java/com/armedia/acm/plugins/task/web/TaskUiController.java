package com.armedia.acm.plugins.task.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/plugin/task")
public class TaskUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView openTaskList()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("taskList");
        return retval;
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    public ModelAndView openTaskDetail(@PathVariable(value = "taskId") Long taskId)
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("taskList");
        retval.addObject("taskId",  taskId);
        return retval;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openTaskWizard(
            @RequestParam(value = "parentType", required = false) String parentType
            ,@RequestParam(value = "reference", required = false) String reference
    ) {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("taskWizard");
        retval.addObject("parentType",  parentType);
        retval.addObject("reference",  reference);
        return retval;

    }

}
