package com.armedia.acm.plugins.casefile.web;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;

import java.util.Map;

/**
 * Created by jwu on 8/28/14.
 */
@RequestMapping("/plugin/casefile")
public class CaseFileUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPlugin plugin;
	private FormUrl formUrl;


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView openComplaints(Authentication auth) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("casefile");
        initModelAndView(mv);
        return mv;
    }

    @RequestMapping(value = "/{caseId}", method = RequestMethod.GET)
    public ModelAndView openComplaint(Authentication auth, @PathVariable(value = "caseId") Long caseId
    ) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("casefile");
        initModelAndView(mv);
        mv.addObject("objId", caseId);
        return mv;
    }

    private ModelAndView initModelAndView(ModelAndView mv) {
        Map<String, Object> props = plugin.getPluginProperties();
        if (null != props) {
            try {
                Object propFilter = props.get("tree.filter");
                if (null != propFilter) {
                    JSONArray treeFilter = new JSONArray(propFilter.toString());
                    mv.addObject("treeFilter", treeFilter);
                }
                Object propSort = props.get("tree.sort");
                if (null != propSort) {
                    JSONArray treeSort = new JSONArray(propSort.toString());
                    mv.addObject("treeSort", treeSort);
                }

            } catch (JSONException e) {
                log.error(e.getMessage());
            }
        }

        mv.addObject("roiFormUrl", formUrl.getNewFormUrl(FrevvoFormName.ROI));
        mv.addObject("changeCaseStatusFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CHANGE_CASE_STATUS));
        mv.addObject("enableFrevvoFormEngine", formUrl.enableFrevvoFormEngine(FrevvoFormName.ROI));
        mv.addObject("editCaseFileFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CASE_FILE));
        mv.addObject("reinvestigateCaseFileFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CASE_FILE));
        return mv;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openCaseFileWizard()
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("casefileWizard");

        // Frevvo form URLs
        mv.addObject("newCaseFileFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CASE_FILE));

        return mv;

    }

	public FormUrl getFormUrl() {
		return formUrl;
	}
	
	public void setFormUrl(FormUrl formUrl) {
		this.formUrl = formUrl;
	}

    public AcmPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(AcmPlugin plugin) {
        this.plugin = plugin;
    }
}
