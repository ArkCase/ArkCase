package com.armedia.acm.services.notification.helper;

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserInfoHelper
{
    private Logger log = LogManager.getLogger(getClass().getName());

    private UserDao userDao;
    private SpringContextHolder contextHolder;

    public String removeUserPrefix(String userId)
    {
        AcmUser user = getUserDao().findByUserId(userId);
        String directoryName = user.getUserDirectoryName();

        String baseUserId = user.getUserId();

        if(StringUtils.isNotBlank(directoryName))
        {
            AcmLdapSyncConfig acmLdapSyncConfig = getContextHolder().getBeanByNameIncludingChildContexts(directoryName.concat("_sync"), AcmLdapSyncConfig.class);
            String userPrefix = acmLdapSyncConfig.getUserPrefix();
            if (StringUtils.isNotBlank(userPrefix))
            {
                log.debug(String.format("User Prefix [%s]", userPrefix));
                log.debug(String.format("Full User id: [%s]", baseUserId));
                baseUserId = user.getUserId().replace(userPrefix.concat("."), "");
                log.debug(String.format("User Id without prefix: [%s]", baseUserId));
            }
        }

        return baseUserId;
    }


    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }
}
