package com.armedia.acm.plugins.complaint.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
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

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.dao.ldap.UserActionDao;
import com.armedia.acm.services.users.model.AcmUserAction;
import com.armedia.acm.services.users.model.AcmUserActionName;


@RequestMapping("/plugin/complaint")
public class ComplaintUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPlugin plugin;
    private AuthenticationTokenService authenticationTokenService;
	private FormUrl formUrl;
	private UserActionDao userActionDao;
	private Map<String, Object> formProperties;
    private Map<String, Object> notificationProperties;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView openComplaints(Authentication auth, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("complaint");

        initModelAndView(mv, auth);


        if (null != request && "successful".equals(request.getParameter("frevvoFormSubmit_status")))
        {
        	AcmUserAction userAction = getUserActionDao().findByUserIdAndName(auth.getName(), AcmUserActionName.LAST_COMPLAINT_CREATED);
        	
        	if (null != userAction)
			{
        		Long id = userAction.getObjectId();
        		
        		if (null != id)
        		{
        			String page = request.getParameter("frevvoFormSubmit_page");

        			mv.addObject("frevvoFormSubmit_id", id);
        			mv.addObject("frevvoFormSubmit_page", page);
        		}
			}
        }
        
        return mv;
    }

    @PreAuthorize("hasPermission(#complaintId, 'COMPLAINT', 'read')")
    @RequestMapping(value = "/{complaintId}", method = RequestMethod.GET)
    public ModelAndView openComplaint(Authentication auth, @PathVariable(value = "complaintId") Long complaintId
    ) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("complaint");
        mv.addObject("objId", complaintId);

        initModelAndView(mv, auth);
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
    private ModelAndView initModelAndView(ModelAndView mv, Authentication auth) {
        Map<String, Object> props = getPlugin().getPluginProperties();
        addJsonArrayProp(mv, props, "search.tree.filter", "treeFilter");
        addJsonArrayProp(mv, props, "search.tree.sort", "treeSort");
        addJsonArrayProp(mv, props, "fileTypes", "fileTypes");
        mv.addObject("arkcaseUrl",getNotificationProperties().get("arkcase.url"));
        mv.addObject("arkcasePort",getNotificationProperties().get("arkcase.port"));

        String token = this.authenticationTokenService.getTokenForAuthentication(auth);
        mv.addObject("token", token);
        log.debug("Security token: " + token);

        // Frevvo form URLs
        mv.addObject("roiFormUrl", formUrl.getNewFormUrl(FrevvoFormName.ROI));
        mv.addObject("closeComplaintFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CLOSE_COMPLAINT));
        mv.addObject("electronicCommunicationFormUrl", formUrl.getNewFormUrl(FrevvoFormName.ELECTRONIC_COMMUNICATION));
        mv.addObject("formDocuments", getFormProperties().get("form.documents"));
        return mv;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openComplaintWizard()
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("complaintWizard");

        // Frevvo form URLs
        mv.addObject("newComplaintFormUrl", formUrl.getNewFormUrl(FrevvoFormName.COMPLAINT));

        return mv;

    }


	public AuthenticationTokenService getAuthenticationTokenService() {
		return authenticationTokenService;
	}

	public void setAuthenticationTokenService(
			AuthenticationTokenService authenticationTokenService) {
		this.authenticationTokenService = authenticationTokenService;
	}

	public FormUrl getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(FormUrl formUrl) {
		this.formUrl = formUrl;
	}

	public UserActionDao getUserActionDao() {
		return userActionDao;
	}

	public void setUserActionDao(UserActionDao userActionDao) {
		this.userActionDao = userActionDao;
	}

	public Map<String, Object> getFormProperties() {
		return formProperties;
	}

	public void setFormProperties(Map<String, Object> formProperties) {
		this.formProperties = formProperties;
	}

    public AcmPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(AcmPlugin plugin) {
        this.plugin = plugin;
    }

    public Map<String, Object> getNotificationProperties() {
        return notificationProperties;
    }

    public void setNotificationProperties(Map<String, Object> notificationProperties) {
        this.notificationProperties = notificationProperties;
    }
}
