package com.armedia.acm.services.users.web.api;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;
import com.armedia.acm.services.users.model.ldap.UserDTO;
import com.armedia.acm.services.users.service.ldap.PasswordValidationService;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base LDAP Controller
 */
public class SecureLdapController
{
    protected SpringContextHolder acmContextHolder;
    private Logger log = LogManager.getLogger(getClass());
    private PasswordValidationService passwordValidationService;

    protected void validateLdapPassword(UserDTO userDTO) throws AcmAppErrorJsonMsg
    {
        List<String> violations = passwordValidationService.validate(userDTO.getUserId(), userDTO.getPassword());

        if (!violations.isEmpty())
        {
            String errorMsg = violations.stream().collect(Collectors.joining("\n"));
            AcmAppErrorJsonMsg e = new AcmAppErrorJsonMsg(errorMsg, null, "password", null);
            userDTO.setPassword(null);
            e.putExtra("userForm", userDTO);
            throw e;
        }
    }

    protected void checkIfLdapManagementIsAllowed(String directory) throws AcmAppErrorJsonMsg
    {
        if (!isLdapManagementEnabled(directory))
        {
            log.warn("Updates on {} LDAP directory are not allowed!", directory);
            throw new AcmAppErrorJsonMsg(String.format("Updates on %s LDAP directory are not allowed!",
                    directory), null, "null", null);
        }
    }

    protected boolean isLdapManagementEnabled(String directory)
    {
        AcmLdapAuthenticateConfig acmLdapAuthenticateConfig = acmContextHolder.getAllBeansOfType(AcmLdapAuthenticateConfig.class)
                .get(String.format("%s_authenticate", directory));
        if (acmLdapAuthenticateConfig != null)
        {
            return acmLdapAuthenticateConfig.getEnableEditingLdapUsers();
        }
        return false;
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public void setPasswordValidationService(PasswordValidationService passwordValidationService)
    {
        this.passwordValidationService = passwordValidationService;
    }
}
