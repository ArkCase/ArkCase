package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.service.AcmGroupEventPublisher;
import com.armedia.acm.services.users.service.group.LdapGroupService;
import com.armedia.acm.services.users.web.api.SecureLdapController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
@RequestMapping({ "/api/v1/ldap", "/api/latest/ldap" })
public class LdapGroupAPIController extends SecureLdapController
{
    private LdapGroupService ldapGroupService;
    private AcmGroupEventPublisher acmGroupEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{directory:.+}/groups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup createLdapGroup(@RequestBody AcmGroup group, @PathVariable String directory)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
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
                    AcmGroupType.LDAP_GROUP.name(), "groupName", e);
            acmAppErrorJsonMsg.putExtra("group", group);
            throw acmAppErrorJsonMsg;
        }
        catch (Exception e)
        {
            log.error("Adding new LDAP group [{}] failed!", group, e);
            throw new AcmAppErrorJsonMsg("Adding new LDAP group failed", "LDAP_GROUP", e);
        }
    }

    @RequestMapping(value = "/{directory:.+}/groups/{parentGroupName:.+}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup createLdapSubgroup(@RequestBody AcmGroup group, @PathVariable String directory, @PathVariable String parentGroupName)
            throws AcmAppErrorJsonMsg
    {

        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapGroupService.createLdapSubgroup(group, parentGroupName, directory);
        }
        catch (NameAlreadyBoundException e)
        {
            log.warn("Duplicate sub-group name [{}]", group.getName(), e);
            AcmAppErrorJsonMsg acmAppErrorJsonMsg = new AcmAppErrorJsonMsg("Group name already exists!",
                    "LDAP_GROUP", "groupName", e);
            acmAppErrorJsonMsg.putExtra("subgroup", group);
            throw acmAppErrorJsonMsg;
        }
        catch (Exception e)
        {
            log.warn("Adding subgroup:{} within LDAP group:{} failed!", group.getName(), parentGroupName, e);
            throw new AcmAppErrorJsonMsg("Adding new LDAP subgroup failed!", "LDAP_GROUP", e);
        }
    }

    @RequestMapping(value = "/{directory:.+}/groups/{groupName:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteLdapGroup(@PathVariable("directory") String directory,
                                             @PathVariable("groupName") String groupName)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            AcmGroup source = ldapGroupService.deleteLdapGroup(groupName, directory);
            getAcmGroupEventPublisher().publishLdapGroupDeletedEvent(source);
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

    public LdapGroupService getLdapGroupService()
    {
        return ldapGroupService;
    }

    public void setLdapGroupService(LdapGroupService ldapGroupService)
    {
        this.ldapGroupService = ldapGroupService;
    }

    public AcmGroupEventPublisher getAcmGroupEventPublisher()
    {
        return acmGroupEventPublisher;
    }

    public void setAcmGroupEventPublisher(AcmGroupEventPublisher acmGroupEventPublisher)
    {
        this.acmGroupEventPublisher = acmGroupEventPublisher;
    }
}
