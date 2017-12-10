package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupType;
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

import java.util.Base64;

@Controller
@RequestMapping({"/api/v1/ldap", "/api/latest/ldap"})
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
            return getLdapGroupService().createLdapGroup(group, directory);
        } catch (NameAlreadyBoundException e)
        {
            log.error("Duplicate group name [{}]", group.getName(), e);
            AcmAppErrorJsonMsg acmAppErrorJsonMsg = new AcmAppErrorJsonMsg("Group name already exists!",
                    AcmGroupType.LDAP_GROUP.name(), "groupName", e);
            acmAppErrorJsonMsg.putExtra("group", group);
            throw acmAppErrorJsonMsg;
        } catch (Exception e)
        {
            log.error("Adding new LDAP group [{}] failed!", group, e);
            throw new AcmUserActionFailedException("create new LDAP group", null, null, "Adding new LDAP group failed", e);
        }
    }

    @RequestMapping(value = "/{directory:.+}/groups/{parentGroupName:.+}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup createLdapSubgroup(@RequestBody AcmGroup group, @PathVariable String directory, @PathVariable String parentGroupName)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {

        parentGroupName = new String(Base64.getUrlDecoder().decode(parentGroupName.getBytes()));

        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return getLdapGroupService().createLdapSubgroup(group, parentGroupName, directory);
        } catch (NameAlreadyBoundException e)
        {
            log.error("Duplicate sub-group name [{}]", group.getName(), e);
            AcmAppErrorJsonMsg acmAppErrorJsonMsg = new AcmAppErrorJsonMsg("Group name already exists!",
                    "LDAP_GROUP", "groupName", e);
            acmAppErrorJsonMsg.putExtra("subgroup", group);
            throw acmAppErrorJsonMsg;
        } catch (Exception e)
        {
            log.error("Adding subgroup:{} within LDAP group:{} failed!", group.getName(), parentGroupName, e);
            throw new AcmUserActionFailedException("create new LDAP subgroup", null, null, "Adding new LDAP subgroup failed!", e);
        }
    }

    @RequestMapping(value = "/{directory:.+}/groups/{groupName:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeLdapGroup(@PathVariable("directory") String directory, @PathVariable("groupName") String groupName)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg

    {
        groupName = new String(Base64.getUrlDecoder().decode(groupName.getBytes()));

        AcmGroup source = getLdapGroupService().getGroupDao().findByName(groupName);
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            ldapGroupService.removeLdapGroup(groupName, directory);
            getAcmGroupEventPublisher().publishLdapGroupDeletedEvent(source);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e)
        {
            log.error("Deleting LDAP group failed!", e);
            throw new AcmUserActionFailedException("Delete", "LDAP group", -1L, "Removing LDAP group failed!", e);
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
