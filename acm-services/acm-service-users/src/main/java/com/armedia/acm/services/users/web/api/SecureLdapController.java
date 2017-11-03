package com.armedia.acm.services.users.web.api;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;
import com.armedia.acm.services.users.model.ldap.UserDTO;
import com.armedia.acm.services.users.service.ldap.PasswordValidationService;
import com.armedia.acm.spring.SpringContextHolder;
import groovy.ui.SystemOutputInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Base LDAP Controller
 */
public class SecureLdapController
{
    protected SpringContextHolder acmContextHolder;
    private Logger log = LoggerFactory.getLogger(getClass());
    private PasswordValidationService passwordValidationService;

    protected void validateLdapPassword(UserDTO userDTO) throws  AcmAppErrorJsonMsg
    {
        List<String> violations = passwordValidationService.validate(userDTO.getUserId(), userDTO.getPassword());

        if(!violations.isEmpty()) {
            String errorMsg = violations.stream().collect(Collectors.joining("<br/>"));
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
        AcmLdapAuthenticateConfig acmLdapAuthenticateConfig = acmContextHolder.getAllBeansOfType(AcmLdapAuthenticateConfig.class).
                get(String.format("%s_authenticate", directory));
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

    public void setPasswordValidationService(PasswordValidationService passwordValidationService) {
        this.passwordValidationService = passwordValidationService;
    }
}
