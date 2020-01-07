package com.armedia.acm.services.users.web.api.group;

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
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.service.group.LdapGroupService;
import com.armedia.acm.services.users.web.api.SecureLdapController;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import java.util.Base64;

@Controller
@RequestMapping({ "/api/v1/ldap", "/api/latest/ldap" })
public class LdapGroupAPIController extends SecureLdapController
{
    private LdapGroupService ldapGroupService;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/{directory:.+}/groups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup createLdapGroup(@RequestBody @Valid AcmGroup group, @PathVariable String directory)
            throws AcmAppErrorJsonMsg
    {

        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapGroupService.createLdapGroup(group, directory);
        }
        catch (NameAlreadyBoundException e)
        {
            log.error("Duplicate group name [{}]", group.getName(), e);
            AcmAppErrorJsonMsg acmAppErrorJsonMsg = new AcmAppErrorJsonMsg("Group name already exists!",
                    AcmGroupType.LDAP_GROUP.name(), "name", e);
            acmAppErrorJsonMsg.putExtra("group", group);
            throw acmAppErrorJsonMsg;
        }
        catch (AcmLdapActionFailedException e)
        {
            throw new AcmAppErrorJsonMsg(e.getMessage(), "LDAP_GROUP", e);
        }
        catch (Exception e)
        {
            throw new AcmAppErrorJsonMsg("Adding new LDAP group failed", "LDAP_GROUP", e);
        }
    }

    @RequestMapping(value = "/{directory:.+}/groups/{parentGroupName:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup createLdapSubgroup(@RequestBody @Valid AcmGroup group, @PathVariable String directory,
            @PathVariable String parentGroupName)
            throws AcmAppErrorJsonMsg
    {

        parentGroupName = new String(Base64.getUrlDecoder().decode(parentGroupName.getBytes()));

        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapGroupService.createLdapSubgroup(group, parentGroupName, directory);
        }
        catch (NameAlreadyBoundException e)
        {
            log.warn("Duplicate sub-group name [{}]", group.getName(), e);
            AcmAppErrorJsonMsg acmAppErrorJsonMsg = new AcmAppErrorJsonMsg("Group name already exists!",
                    "LDAP_GROUP", "name", e);
            acmAppErrorJsonMsg.putExtra("subgroup", group);
            throw acmAppErrorJsonMsg;
        }
        catch (AcmLdapActionFailedException e)
        {
            throw new AcmAppErrorJsonMsg(e.getMessage(), "LDAP_GROUP", e);
        }
        catch (Exception e)
        {
            throw new AcmAppErrorJsonMsg("Adding new LDAP subgroup failed!", "LDAP_GROUP", e);
        }
    }

    @RequestMapping(value = "/{directory:.+}/groups/{groupName:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteLdapGroup(@PathVariable("directory") String directory,
            @PathVariable("groupName") String groupName)
            throws AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            groupName = new String(Base64.getUrlDecoder().decode(groupName.getBytes()));
            ldapGroupService.deleteLdapGroup(groupName, directory);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (AcmLdapActionFailedException e)
        {
            throw new AcmAppErrorJsonMsg("Deleting LDAP group failed. Cause: " + e.getMessage(),
                    "LDAP_GROUP", e);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw new AcmAppErrorJsonMsg("Deleting LDAP group failed. Cause: " + e.getCauseMessage(),
                    "LDAP_GROUP", e);
        }
        catch (Exception e)
        {
            throw new AcmAppErrorJsonMsg("Deleting LDAP group failed", "LDAP_GROUP", e);
        }
    }

    @RequestMapping(value = "/{directory:.+}/groups/{groupName:.+}/parent/{parentName:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeGroupMembership(@PathVariable("directory") String directory,
            @PathVariable("groupName") String groupName,
            @PathVariable("parentName") String parentName)
            throws AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            groupName = new String(Base64.getUrlDecoder().decode(groupName.getBytes()));
            parentName = new String(Base64.getUrlDecoder().decode(parentName.getBytes()));
            ldapGroupService.removeGroupMembership(groupName, parentName, directory);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (AcmLdapActionFailedException e)
        {
            throw new AcmAppErrorJsonMsg("Removing LDAP group membership failed. Cause: " + e.getMessage(),
                    "LDAP_GROUP", e);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw new AcmAppErrorJsonMsg("Removing LDAP group membership failed. Cause: " + e.getCauseMessage(),
                    "LDAP_GROUP", e);
        }
        catch (Exception e)
        {
            throw new AcmAppErrorJsonMsg("Removing LDAP group membership failed", "LDAP_GROUP", e);
        }
    }

    @RequestMapping(value = "/{directoryName}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getControlGroup(@PathVariable("directoryName") String directoryName)
    {
        directoryName = new String(Base64.getUrlDecoder().decode(directoryName.getBytes()));
        return getLdapGroupService().getControlGroup(directoryName);
    }

    public LdapGroupService getLdapGroupService()
    {
        return ldapGroupService;
    }

    public void setLdapGroupService(LdapGroupService ldapGroupService)
    {
        this.ldapGroupService = ldapGroupService;
    }
}
