package gov.foia.service.dataupdate;

/*-
 * #%L
 * ACM Service: Data Update Service
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.portalgateway.model.PortalInfo;
import com.armedia.acm.portalgateway.service.PortalInfoDAO;
import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;

import gov.foia.dao.UserRegistrationRequestDao;
import gov.foia.model.UserRegistrationRequestRecord;

/**
 * @author sasko.tanaskoski
 *
 */
public class FoiaPortalRegistrationCleanupExecutor implements AcmDataUpdateExecutor
{
    private final Logger log = LogManager.getLogger(getClass());

    private SpringContextHolder acmContextHolder;

    @Value("${portal.serviceProvider.directory.name}")
    private String directoryName;

    private UserDao userDao;

    private UserRegistrationRequestDao registrationDao;

    private PortalInfoDAO portalInfoDAO;

    @Override
    public String getUpdateId()
    {
        return "foia-portal-registration-cleanup-v1";
    }

    @Override
    public void execute()
    {
        List<PortalInfo> portalInfoList = portalInfoDAO.findAll();
        if (portalInfoList.size() > 0)
        {
            AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                    .get(String.format("%s_sync", directoryName));
            if (ldapSyncConfig != null && ldapSyncConfig.getUserPrefix() != null)
            {
                List<AcmUser> acmUsers = userDao.findByPrefix(ldapSyncConfig.getUserPrefix());
                String portalId = portalInfoList.get(0).getPortalId();
                acmUsers.forEach(user -> {
                    Optional<UserRegistrationRequestRecord> registrationRequestRecord = registrationDao.findByEmail(user.getMail(),
                            portalId);
                    if (registrationRequestRecord.isPresent())
                    {
                        registrationDao.delete(registrationRequestRecord.get());
                    }
                });
            }
        }
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setRegistrationDao(UserRegistrationRequestDao registrationDao)
    {
        this.registrationDao = registrationDao;
    }

    public void setPortalInfoDAO(PortalInfoDAO portalInfoDAO)
    {
        this.portalInfoDAO = portalInfoDAO;
    }
}
