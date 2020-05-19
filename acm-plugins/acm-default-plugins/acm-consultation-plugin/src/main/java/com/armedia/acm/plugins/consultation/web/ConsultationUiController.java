package com.armedia.acm.plugins.consultation.web;

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Properties;

/**
 * Created by jwu on 8/28/14.
 */
@RequestMapping("/plugin/consultation")
public class ConsultationUiController
{
    private Logger log = LogManager.getLogger(getClass());
    private Properties properties;
    private AcmPlugin plugin;
    private FormUrl formUrl;
    private Map<String, Object> formProperties;
    private Map<String, Object> notificationProperties;

    @RequestMapping(value = "/{consultationId}", method = RequestMethod.GET)
    public ModelAndView viewFile(Authentication auth, @PathVariable(value = "consultationId") Long consultationId)
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName(getViewName());
        initModelAndView(mv);
        mv.addObject("objId", consultationId);
        return mv;
    }

    private String getViewName()
    {
        String jsp = "consultation";
        return jsp;
    }

    private void addJsonArrayProp(ModelAndView mv, Map<String, Object> props, String propName, String attrName)
    {
        if (null != props)
        {
            try
            {
                Object prop = props.get(propName);
                if (null != prop)
                {
                    JSONArray ar = new JSONArray(prop.toString());
                    mv.addObject(attrName, ar);
                }

            }
            catch (JSONException e)
            {
                log.error(e.getMessage());
            }
        }
    }

    private ModelAndView initModelAndView(ModelAndView mv)
    {
        Map<String, Object> props = plugin.getPluginProperties();
        addJsonArrayProp(mv, props, "search.tree.filter", "treeFilter");
        addJsonArrayProp(mv, props, "search.tree.sort", "treeSort");
        addJsonArrayProp(mv, props, "fileTypes", "fileTypes");
        mv.addObject("arkcaseUrl", getNotificationProperties().get("arkcase.url"));
        mv.addObject("arkcasePort", getNotificationProperties().get("arkcase.port"));

        mv.addObject("allowMailFilesAsAttachments", getNotificationProperties().get("notification.allowMailFilesAsAttachments"));
        mv.addObject("allowMailFilesToExternalAddresses",
                getNotificationProperties().get("notification.allowMailFilesToExternalAddresses"));

        mv.addObject("newConsultationFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CONSULTATION, false));
        mv.addObject("changeConsultationStatusFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CHANGE_CONSULTATION_STATUS, false));
        mv.addObject("enableFrevvoFormEngine", formUrl.enableFrevvoFormEngine(FrevvoFormName.ROI));
        mv.addObject("editConsultationFileFormUrl", getConsultationFileUrl());
        mv.addObject("reinvestigateConsultationFileFormUrl", getConsultationFileUrl());
        mv.addObject("formDocuments", getFormProperties().get("form.documents"));
        mv.addObject("consultationTreeRootNameExpression", props.get("consultation.tree.root.name.expression"));
        return mv;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openConsultationFileWizard()
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("consultationfileWizard");

        // Frevvo form URLs
        mv.addObject("newConsultationFileFormUrl", getConsultationFileUrl());

        return mv;
    }

    private String getConsultationFileUrl()
    {
        // Default one
        String consultationFormName = ConsultationConstants.OBJECT_TYPE.toLowerCase();
        if (getFormProperties() != null)
        {
            if (getFormProperties().containsKey(ConsultationConstants.ACTIVE_CONSULTATION_FORM_KEY))
            {
                String activeFormName = (String) getFormProperties().get(ConsultationConstants.ACTIVE_CONSULTATION_FORM_KEY);

                if (activeFormName != null && !"".equals(activeFormName))
                {
                    consultationFormName = activeFormName;
                }
            }
        }

        return formUrl.getNewFormUrl(consultationFormName, false);
    }

    public FormUrl getFormUrl()
    {
        return formUrl;
    }

    public void setFormUrl(FormUrl formUrl)
    {
        this.formUrl = formUrl;
    }

    public AcmPlugin getPlugin()
    {
        return plugin;
    }

    public void setPlugin(AcmPlugin plugin)
    {
        this.plugin = plugin;
    }

    public Map<String, Object> getFormProperties()
    {
        return formProperties;
    }

    public void setFormProperties(Map<String, Object> formProperties)
    {
        this.formProperties = formProperties;
    }

    public Map<String, Object> getNotificationProperties()
    {
        return notificationProperties;
    }

    public void setNotificationProperties(Map<String, Object> notificationProperties)
    {
        this.notificationProperties = notificationProperties;
    }

    public Properties getProperties()
    {
        return properties;
    }

    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }
}
