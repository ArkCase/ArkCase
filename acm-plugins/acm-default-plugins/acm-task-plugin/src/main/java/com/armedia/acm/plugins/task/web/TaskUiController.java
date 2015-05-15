package com.armedia.acm.plugins.task.web;

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequestMapping("/plugin/task")
public class TaskUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPlugin plugin;
    private FormUrl formUrl;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView openTaskList()
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("task");

        initModelAndView(mv);
        return mv;
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    public ModelAndView openTaskDetail(@PathVariable(value = "taskId") Long taskId)
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("task");
        mv.addObject("objId",  taskId);

        initModelAndView(mv);
        return mv;
    }

    private void addJsonArrayProp(ModelAndView mv, Map<String, Object> props, String propName, String attrName) {
        if (null != props) {
            try {
                Object prop = props.get(propName);
                if (null != prop) {
                    JSONArray ar = new JSONArray(prop.toString());
                    mv.addObject(attrName, ar);
                }

            } catch (JSONException e) {
                log.error(e.getMessage());
            }
        }
    }
    private ModelAndView initModelAndView(ModelAndView mv) {
        Map<String, Object> props = getPlugin().getPluginProperties();
        addJsonArrayProp(mv, props, "search.tree.filter", "treeFilter");
        addJsonArrayProp(mv, props, "search.tree.sort", "treeSort");
        addJsonArrayProp(mv, props, "fileTypes", "fileTypes");

        //frevvo form URLs
        mv.addObject("editCloseComplaintFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CLOSE_COMPLAINT));
        mv.addObject("changeCaseStatusFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CHANGE_CASE_STATUS));
        return mv;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openTaskWizard(
            @RequestParam(value = "parentType", required = false) String parentType
            ,@RequestParam(value = "reference", required = false) String reference
    ) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("taskWizard");
        mv.addObject("parentType",  parentType);
        mv.addObject("reference",  reference);
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
