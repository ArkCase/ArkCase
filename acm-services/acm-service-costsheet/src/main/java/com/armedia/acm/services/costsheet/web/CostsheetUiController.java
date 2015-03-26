package com.armedia.acm.services.costsheet.web;

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.service.CostsheetService;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;


@RequestMapping("/plugin/costsheet")
public class CostsheetUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private CostsheetService costsheetService;
    private FormUrl formUrl;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showCostsheetPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("costsheet");

        String treeSort = (String) getCostsheetService().getProperties().get(CostsheetConstants.SEARCH_TREE_SORT);
        if(null != treeSort){
            retval.addObject("treeSort", treeSort);
        } else {
            log.warn("Tree sort missing");
        }


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


    public CostsheetService getCostsheetService() {
        return costsheetService;
    }

    public void setCostsheetService(CostsheetService costsheetService) {
        this.costsheetService = costsheetService;
    }
}
