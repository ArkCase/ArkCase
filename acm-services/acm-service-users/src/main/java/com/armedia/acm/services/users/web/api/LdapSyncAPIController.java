package com.armedia.acm.services.users.web.api;

import com.armedia.acm.services.users.service.ldap.LdapSyncService;
import com.armedia.acm.spring.SpringContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;

@Controller
@RequestMapping({ "/api/v1/ldap", "/api/latest/ldap" })
public class LdapSyncAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private SpringContextHolder acmContextHolder;

    @RequestMapping(value = "/{directory:.+}/partial-sync", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity initiatePartialSync(@PathVariable String directory, Authentication authentication)
    {
        LdapSyncService ldapSyncService = acmContextHolder.getAllBeansOfType(LdapSyncService.class)
                .get(String.format("%s_ldapSyncJob", directory));
        if (ldapSyncService != null)
        {
            log.debug("User [{}] initiated partial sync of directory [{}]", authentication.getName(), directory);
            ldapSyncService.initiateSync(authentication.getName(), false);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(Collections.singletonMap("message", "Service for directory [ " + directory + " ] not found"),
                HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{directory:.+}/full-sync", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity initiateFullSync(@PathVariable String directory, Authentication authentication)
    {
        LdapSyncService ldapSyncService = acmContextHolder.getAllBeansOfType(LdapSyncService.class)
                .get(String.format("%s_ldapSyncJob", directory));
        if (ldapSyncService != null)
        {
            log.debug("User [{}] initiated full sync of directory [{}]", authentication.getName(), directory);
            ldapSyncService.initiateSync(authentication.getName(), true);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(Collections.singletonMap("message", "Service for directory [ " + directory + " ] not found"),
                HttpStatus.BAD_REQUEST);
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }
}
