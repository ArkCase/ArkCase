package com.armedia.acm.services.users.service.group;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.services.users.service.ldap.LdapEntryTransformer;
import com.armedia.acm.spring.SpringContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

public class LdapGroupService
{
    private GroupService groupService;

    private SpringLdapGroupDao ldapGroupDao;

    private UserDao userDao;

    private LdapEntryTransformer ldapEntryTransformer;

    private SpringContextHolder acmContextHolder;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Transactional(rollbackFor = Exception.class)
    public AcmGroup createLdapGroup(AcmGroup group, String directoryName) throws AcmLdapActionFailedException
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        String groupName = MapperUtils.buildGroupName(group.getName(), Optional.of(ldapSyncConfig.getUserDomain()));

        AcmGroup existingGroup = groupService.findByName(groupName);
        if (existingGroup != null)
        {
            log.debug("Group with name: [{}] already exists!", group.getName());
            throw new NameAlreadyBoundException(null);
        }

        String groupDN = buildDnForGroup(group.getName(), ldapSyncConfig);
        group.setName(groupName);
        group.setDisplayName(groupName);
        group.setType(AcmGroupType.LDAP_GROUP);
        group.setDescription(group.getDescription());
        group.setDistinguishedName(groupDN);
        group.setDirectoryName(directoryName);
        group.setStatus(AcmGroupStatus.ACTIVE);
        log.debug("Saving Group [{}] with DN [{}] in database", group.getName(), group.getDistinguishedName());
        AcmGroup acmGroup = groupService.saveAndFlush(group);

        ldapGroupDao.createGroup(acmGroup, ldapSyncConfig);
        log.debug("Group [{}] with DN [{}] saved in DB and LDAP", acmGroup.getName(), acmGroup.getDistinguishedName());
        return acmGroup;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmGroup createLdapSubgroup(AcmGroup group, String parentGroupName, String directoryName)
            throws AcmLdapActionFailedException, AcmObjectNotFoundException
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        String givenName = group.getName();
        String groupName = MapperUtils.buildGroupName(givenName, Optional.of(ldapSyncConfig.getUserDomain()));

        AcmGroup existingGroup = groupService.findByName(groupName);
        if (existingGroup != null)
        {
            log.debug("Group with name [{}] already exists!", groupName);
            throw new NameAlreadyBoundException(null);
        }

        AcmGroup parentGroup = groupService.findByName(parentGroupName);
        if (parentGroup == null)
        {
            throw new AcmObjectNotFoundException("LDAP_GROUP", null, "Parent group not found");
        }
        log.debug("Found parent group [{}] for new LDAP sub-group [{}]", parentGroup.getName(), group.getName());

        String groupDN = buildDnForGroup(givenName, ldapSyncConfig);

        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName(groupName);
        acmGroup.setDisplayName(groupName);
        acmGroup.setType(AcmGroupType.LDAP_GROUP);
        acmGroup.setDescription(group.getDescription());
        acmGroup.setDistinguishedName(groupDN);
        acmGroup.setDirectoryName(directoryName);

        Set<String> ancestors = parentGroup.getAscendants();
        ancestors.add(parentGroupName);
        acmGroup.setAscendantsList(AcmGroupUtils.getAscendantsString(ancestors));

        parentGroup.addGroupMember(acmGroup);
        log.debug("Updated parent-group [{}] with sub-group [{}] in database", parentGroup.getName(), acmGroup.getName());
        groupService.saveAndFlush(parentGroup);

        log.debug("Saving sub-group [{}] with parent-group [{}] in LDAP server", acmGroup.getDistinguishedName(), parentGroup.getName());
        ldapGroupDao.createGroup(acmGroup, ldapSyncConfig);

        log.debug("Sub-group [{}] with DN [{}] saved in LDAP server", acmGroup.getName(), acmGroup.getDistinguishedName());
        try
        {
            log.debug("Update parent-group [{}] with DN [{}] with the new member [{}] in LDAP server", parentGroup.getName(),
                    parentGroup.getDistinguishedName(), acmGroup.getDistinguishedName());
            ldapGroupDao.addMemberToGroup(acmGroup.getDistinguishedName(), parentGroup.getDistinguishedName(), ldapSyncConfig);
        }
        catch (AcmLdapActionFailedException e)
        {
            log.error("Updating parent-group DN [{}] failed! Rollback saved sub-group DN [{}] ",
                    parentGroup.getDistinguishedName(), acmGroup.getDistinguishedName());
            ldapGroupDao.deleteGroupEntry(acmGroup.getDistinguishedName(), ldapSyncConfig);
            throw e;
        }
        return acmGroup;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmGroup deleteLdapGroup(String group, String directoryName)
            throws AcmLdapActionFailedException, AcmObjectNotFoundException
    {
        log.debug("Deleting LDAP group [{}]", group);
        AcmGroup markedGroup = groupService.markGroupDeleted(group, true);
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directoryName);
        ldapGroupDao.deleteGroupEntry(markedGroup.getDistinguishedName(), ldapSyncConfig);
        return markedGroup;
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeGroupMembership(String groupName, String parentGroupName, String directoryName)
            throws AcmObjectNotFoundException, AcmLdapActionFailedException
    {
        AcmGroup acmGroup = groupService.removeGroupMembership(groupName, parentGroupName, true);
        AcmGroup parentGroup = groupService.findByName(parentGroupName);
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directoryName);
        if (acmGroup.getStatus() == AcmGroupStatus.DELETE)
        {
            ldapGroupDao.deleteGroupEntry(acmGroup.getDistinguishedName(), ldapSyncConfig);
        }
        else
        {
            ldapGroupDao.removeMemberFromGroup(acmGroup.getDistinguishedName(), parentGroup.getDistinguishedName(), ldapSyncConfig);
        }
    }

    private AcmLdapSyncConfig getLdapSyncConfig(String directoryName)
    {
        return acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).get(String.format("%s_sync", directoryName));
    }

    private String buildDnForGroup(String cn, AcmLdapSyncConfig ldapSyncConfig)
    {
        String cnRdn = String.format("cn=%s", cn);
        return MapperUtils.appendToDn(cnRdn, ldapSyncConfig.getGroupSearchBase(), ldapSyncConfig.getBaseDC());
    }

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    public SpringLdapGroupDao getLdapGroupDao()
    {
        return ldapGroupDao;
    }

    public void setLdapGroupDao(SpringLdapGroupDao ldapGroupDao)
    {
        this.ldapGroupDao = ldapGroupDao;
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public LdapEntryTransformer getLdapEntryTransformer()
    {
        return ldapEntryTransformer;
    }

    public void setLdapEntryTransformer(LdapEntryTransformer ldapEntryTransformer)
    {
        this.ldapEntryTransformer = ldapEntryTransformer;
    }
}
