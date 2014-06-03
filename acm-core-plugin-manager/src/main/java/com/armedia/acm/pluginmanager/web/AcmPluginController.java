package com.armedia.acm.pluginmanager.web;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmUserAction;
import com.armedia.acm.web.api.AcmSpringMvcErrorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugins", "/api/latest/plugins" })
public class AcmPluginController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmSpringMvcErrorManager errorManager;

    /**
     * REST service to get the list of accessible navigator tabs.  It is NOT used by the ACM webapp (the webapp uses
     * JSTIL to iterate over the AcmApplication which is added to the user session at login time).  This service is to
     * ensure the tab list is available via REST... since all data that appears in the UI should be REST-accessible.
     */
    @RequestMapping(value="/navigatorPlugins", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<AcmUserAction> enabledNavigatorPlugins(
            HttpSession userSession,
            HttpServletResponse response)
    throws IOException
    {
        Map<String, Boolean> userPrivileges = (Map<String, Boolean>) userSession.getAttribute("acm_privileges");
        AcmApplication acmApplication = (AcmApplication) userSession.getAttribute("acm_application");

        if ( log.isDebugEnabled() )
        {
            log.debug("User Privileges is null? " + ( userPrivileges == null ));
            log.debug("ACM App is null? " + ( acmApplication == null));
        }

        if ( userPrivileges == null || acmApplication == null )
        {
            getErrorManager().sendErrorResponse(HttpStatus.BAD_REQUEST, "Invalid ACM session: no user privileges set", response);
        }

        List<AcmUserAction> tabs = acmApplication.getNavigatorTabs();
        List<AcmUserAction> userAccessibleTabs = new ArrayList<>();
        for ( AcmUserAction action : tabs )
        {
            String requiredPrivilege = action.getRequiredPrivilege();
            if ( userPrivileges.containsKey(requiredPrivilege) && userPrivileges.get(requiredPrivilege))
            {
                userAccessibleTabs.add(action);
            }
        }

        return userAccessibleTabs;
    }

    public AcmSpringMvcErrorManager getErrorManager()
    {
        return errorManager;
    }

    public void setErrorManager(AcmSpringMvcErrorManager errorManager)
    {
        this.errorManager = errorManager;
    }
}
