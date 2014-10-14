package com.armedia.acm.plugins.dashboard.web.api;
import java.beans.Introspector;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleName;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 9/30/2014.
 */
@Controller
@RequestMapping({"/api/v1/plugin/dashboard/widgets", "/api/latest/plugin/dashboard/widgets"})
public class GetRolesByWidgetsAPIController {

    private UserDao userDao;
    private WidgetDao widgetDao;
    private WidgetEventPublisher eventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/rolesByWidget/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<RolesGroupByWidgetDto> getDashboardConfig(Authentication authentication, HttpSession session) throws AcmWidgetException, AcmObjectNotFoundException {
        if (log.isInfoEnabled()) {
            log.info("Finding all roles per all widgets'");
        }
         List<RolesGroupByWidgetDto> result = null;
         try {
            result = addNotAuthorizedRolesPerWidget(getWidgetDao().getRolesGroupByWidget());
            raiseGetEvent(authentication,session,result,true);
            return result;
         } catch (AcmObjectNotFoundException e) {
             throw new AcmWidgetException("No Roles per Widgets found",e);
         }
    }

    protected void raiseGetEvent(Authentication authentication, HttpSession session, List<RolesGroupByWidgetDto> rolesPerWidgets, boolean succeeded) {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishGeRolesByWidgets(rolesPerWidgets, authentication, ipAddress, succeeded);
    }

    private List<RolesGroupByWidgetDto> addNotAuthorizedRolesPerWidget(List<RolesGroupByWidgetDto> rolesPerWidget) {
        List<AcmRole> allRoles = getUserDao().findAllRoles();
        List<Widget> allWidgets = getWidgetDao().findAll();
        List<WidgetRoleName> notAuthorized = new ArrayList<WidgetRoleName>();
        boolean isNotAuthorized = true;
        List<RolesGroupByWidgetDto> tmpRolesPerWidget = new ArrayList<RolesGroupByWidgetDto>();
        boolean isAddedToRolesGroupByWidgetLsit = false;
            for (RolesGroupByWidgetDto rolePerW : rolesPerWidget) {
                rolePerW.setName(widgetName(rolePerW.getWidgetName()));
                for (AcmRole role : allRoles) {
                    for (WidgetRoleName roleName : rolePerW.getWidgetAuthorizedRoles()) {
                        if (roleName.getName().equals(role.getRoleName())) {
                            isNotAuthorized = false;
                            break;
                        }
                    }
                    if (isNotAuthorized) {
                        notAuthorized.add(new WidgetRoleName(role.getRoleName()));
                    }
                    isNotAuthorized = true;
                }
                rolePerW.setWidgetNotAuthorizedRoles(notAuthorized);
                notAuthorized = new ArrayList<WidgetRoleName>();
            }
        for(Widget widget: allWidgets) {
            for(RolesGroupByWidgetDto roleW: rolesPerWidget) {
                if(roleW.getWidgetName().equals(widget.getWidgetName())){
                    tmpRolesPerWidget.add(roleW);
                    isAddedToRolesGroupByWidgetLsit = true;
                    break;
                }
            }
            if(!isAddedToRolesGroupByWidgetLsit){
                RolesGroupByWidgetDto rolesGBW = new RolesGroupByWidgetDto();
                rolesGBW.setWidgetName(widget.getWidgetName());
                rolesGBW.setName(widgetName(widget.getWidgetName()));
                List<WidgetRoleName> notAuth = new ArrayList<WidgetRoleName>();
                for(AcmRole role: allRoles){
                    notAuth.add(new WidgetRoleName(role.getRoleName()));
                }
                rolesGBW.setWidgetNotAuthorizedRoles(notAuth);
                rolesGBW.setWidgetAuthorizedRoles(new ArrayList<WidgetRoleName>());
                tmpRolesPerWidget.add(rolesGBW);
            }
            isAddedToRolesGroupByWidgetLsit = false;
        }
        return tmpRolesPerWidget;
    }
    private String widgetName(String camelName){
        StringBuffer stringBuffer = new StringBuffer();
        //create sentence from camelString
        for (String w : camelName.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            stringBuffer.append(w.substring(0, 1).toUpperCase() + w.substring(1));
            stringBuffer.append(" ");
        }
        return stringBuffer.toString();
    }

    public WidgetDao getWidgetDao() {
        return widgetDao;
    }

    public void setWidgetDao(WidgetDao widgetDao) {
        this.widgetDao = widgetDao;
    }

    public WidgetEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(WidgetEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}