package com.armedia.acm.services.users.web.api;

import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = { "/api/v1/users", "/api/latest/users" } )
public class FindUsersWithPrivilegeAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPluginManager pluginManager;
    private UserDao userDao;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/withPrivilege/{privilege}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AcmUser> withPrivilege(
            @PathVariable(value = "privilege") String privilege)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Looking for users with privilege '" + privilege + "'");
        }

        List<String> rolesForPrivilege = getPluginManager().getRolesForPrivilege(privilege);
        List<AcmUser> users = getUserDao().findUsersWithRoles(rolesForPrivilege);

        return users;


    }

    public void setPluginManager(AcmPluginManager pluginManager)
    {
        this.pluginManager = pluginManager;
    }

    public AcmPluginManager getPluginManager()
    {
        return pluginManager;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }
}
