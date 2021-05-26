package gov.foia.service.dataupdate;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

public class MultiplePortalUsersWithSameEmailCleanupExecutor implements AcmDataUpdateExecutor
{

    private SpringContextHolder acmContextHolder;

    private SpringLdapUserDao springLdapUserDao;

    private final Logger log = LogManager.getLogger(getClass());

    @Value("${portal.serviceProvider.directory.name}")
    private String directoryName;

    private UserDao userDao;

    @Override
    public String getUpdateId()
    {
        return "multiple-portal-user-with-same-email-cleanup";
    }

    @Override
    public void execute()
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));
        if (ldapSyncConfig != null && ldapSyncConfig.getUserPrefix() != null)
        {
            List<AcmUser> portalUsers = userDao.findByPrefix(ldapSyncConfig.getUserPrefix());
            Map<String, List<AcmUser>> map = portalUsers.stream().filter(user -> user.getUserState().equals(AcmUserState.VALID)).collect(groupingBy(AcmUser::getMail));
            map.entrySet().stream()
                    .filter(entry -> entry.getValue().size() > 1)
                    .sorted(Comparator.comparing(
                            e -> e.getValue().stream().map(AcmUser::getCreated).min(Comparator.naturalOrder()).orElse(new Date(0))))
                    .forEach(stringListEntry -> {
                        List<AcmUser> users = stringListEntry.getValue();
                        for (int i = 0; i < users.size() - 1; i++)
                        {
                            users.get(i).setUserState(AcmUserState.INVALID);
                            try
                            {
                                userDao.save(users.get(i));
                            }
                            catch (Exception e)
                            {
                                log.error("Updating user failed", e);
                            }
                            try
                            {
                                getSpringLdapUserDao().deleteUserEntry(users.get(i).getUserId(), ldapSyncConfig);
                            }
                            catch (AcmLdapActionFailedException e)
                            {
                                log.error("LDAP action failed to execute", e);
                            }

                        }
                    });
        }

    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public SpringLdapUserDao getSpringLdapUserDao()
    {
        return springLdapUserDao;
    }

    public void setSpringLdapUserDao(SpringLdapUserDao springLdapUserDao)
    {
        this.springLdapUserDao = springLdapUserDao;
    }
}
