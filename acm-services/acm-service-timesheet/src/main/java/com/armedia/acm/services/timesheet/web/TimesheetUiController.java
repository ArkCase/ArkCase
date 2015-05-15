package com.armedia.acm.services.timesheet.web;


import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.timesheet.service.TimesheetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/plugin/timesheet")
public class TimesheetUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private TimesheetService timesheetService;
    private FormUrl formUrl;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showTimesheetPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("timesheet");

        String treeSort = (String) getTimesheetService().getProperties().get(TimesheetConstants.SEARCH_TREE_SORT);
        if(null != treeSort){
            retval.addObject("treeSort", treeSort);
        } else {
            log.warn("Tree sort missing");
        }

        // Frevvo form URLs
        retval.addObject("newTimesheetFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.TIMESHEET));
        return retval;

    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openTimesheetWizard()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("timesheetWizard");

        // Frevvo form URLs
        retval.addObject("newTimesheetFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.TIMESHEET));
        return retval;

    }

    @RequestMapping(value = "/{timesheetId}", method = RequestMethod.GET)
    public ModelAndView openTimesheet(Authentication auth, @PathVariable(value = "timesheetId") Long timesheetId
    ) {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("timesheet");
        retval.addObject("objId", timesheetId);


        // Frevvo form URLs
        retval.addObject("newTimesheetFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.TIMESHEET));
        return retval;

    }

    public FormUrl getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(FormUrl formUrl) {
        this.formUrl = formUrl;
    }

    public TimesheetService getTimesheetService() {
        return timesheetService;
    }

    public void setTimesheetService(TimesheetService timesheetService) {
        this.timesheetService = timesheetService;
    }

}
