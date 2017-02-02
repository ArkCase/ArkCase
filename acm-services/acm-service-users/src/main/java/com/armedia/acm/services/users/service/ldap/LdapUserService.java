package com.armedia.acm.services.users.service.ldap;


import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Name;
import java.util.List;
import java.util.Map;

public class LdapUserService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private SpringLdapDao ldapDao;

    private UserDao userDao;

    private AcmGroupDao groupDao;

    private SpringContextHolder acmContextHolder;

    @Transactional
    public AcmUser createLdapUser(AcmUser user, String groupName, String password)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).get("armedia_sync");

        Map<String, String> roleToGroup = ldapSyncConfig.getRoleToGroupMap();
        Map<String, List<String>> groupToRoleMap = LdapSyncService.reverseRoleToGroupMap(roleToGroup);
        Name dn = buildDnForUser(user.getFirstName(), ldapSyncConfig);
        user.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));
        user.setDistinguishedName(dn.toString());
        String sAMAccountName = buildSAMAccountName(user.getFirstName());
        user.setUserId(sAMAccountName);
        user.setUserDirectoryName(sAMAccountName);
        AcmGroup group = getGroupDao().findByName(groupName);
        user.addGroup(group);
        log.debug("Saving User in database...");
        AcmUser ldapUser = getUserDao().save(user);
        log.debug("User saved");

        List<String> userRoles = groupToRoleMap.get(groupName);
        userRoles.forEach(role ->
        {
            AcmUserRole userRole = new AcmUserRole();
            userRole.setUserId(sAMAccountName);
            userRole.setRoleName(role);
            userRole.setUserRoleState("VALID");
            log.debug("Saving AcmUserRole: {}", userRole);
            getUserDao().saveAcmUserRole(userRole);
        });
        log.debug("User roles saved");

        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);
        DirContextAdapter context = createContextForUser(user, password,
                group.getDistinguishedName(), sAMAccountName);
        log.debug("Save in LDAP...");
        ldapTemplate.bind(context);
        return ldapUser;
    }

    private DirContextAdapter createContextForUser(AcmUser user, String password, String groupDn, String sAMAccountName)
    {
        DirContextAdapter context = new DirContextAdapter(user.getDistinguishedName());
        context.setAttributeValues("objectClass", new String[]{"top", "person", "inetOrgPerson",
                "organizationalPerson", "simulatedMicrosoftSecurityPrincipal", "uacPerson"});
        context.setAttributeValue("cn", user.getFirstName());
        context.setAttributeValue("sn", user.getLastName());
        context.setAttributeValue("sAMAccountName", sAMAccountName);
        context.setAttributeValue("givenName", user.getFirstName());
        context.setAttributeValue("mail", user.getMail());
        context.setAttributeValue("memberOf", groupDn);
        context.setAttributeValue("userAccountControl", "1");
        context.setAttributeValue("userPassword", password);
        return context;
    }

    private String buildSAMAccountName(String firstName)
    {
        return firstName.toLowerCase().replaceAll("\\s+", "-");
    }

    private Name buildDnForUser(String cn, AcmLdapSyncConfig ldapSyncConfig)
    {
        String dnPath = String.format("cn=%s,%s", cn, ldapSyncConfig.getUserSearchBase());
        return new DistinguishedName(dnPath);
    }

    public SpringLdapDao getLdapDao()
    {
        return ldapDao;
    }

    public void setLdapDao(SpringLdapDao ldapDao)
    {
        this.ldapDao = ldapDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

}
