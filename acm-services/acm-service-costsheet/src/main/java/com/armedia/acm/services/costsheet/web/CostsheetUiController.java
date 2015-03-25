package com.armedia.acm.services.costsheet.web;

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



@RequestMapping("/plugin/costsheet")
public class CostsheetUiController
{

    private FormUrl formUrl;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showCostsheetPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("costsheet");

        // Frevvo form URLs
        retval.addObject("newCostsheetFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.COSTSHEET));
        return retval;
    }

    @RequestMapping(value = "/{costsheetId}", method = RequestMethod.GET)
    public ModelAndView openTimesheet(Authentication auth, @PathVariable(value = "costsheetId") Long costsheetId
    ) {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("costsheet");
        retval.addObject("objId", costsheetId);


        // Frevvo form URLs
        retval.addObject("newCostsheetFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.COSTSHEET));
        return retval;

    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openCostsheetWizard()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("costsheetWizard");

        // Frevvo form URLs
        retval.addObject("newCostsheetFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.COSTSHEET));
        return retval;

    }

    public FormUrl getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(FormUrl formUrl) {
        this.formUrl = formUrl;
    }
}
