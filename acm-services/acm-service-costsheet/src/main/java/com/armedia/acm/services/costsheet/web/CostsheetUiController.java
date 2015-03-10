package com.armedia.acm.services.costsheet.web;

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;
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
        retval.addObject("newCostsheetFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.COST));
        return retval;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openCostsheetWizard()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("costsheetWizard");

        // Frevvo form URLs
        retval.addObject("newCostsheetFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.COST));
        return retval;

    }

    public FormUrl getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(FormUrl formUrl) {
        this.formUrl = formUrl;
    }
}
