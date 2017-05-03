package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.springframework.ldap.core.DirContextAdapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AcmUserGroupsContextMapperTest extends EasyMockSupport
{
    private AcmUserGroupsContextMapper unit;

    @Test
    public void setLdapUser_test()
    {
        AcmLdapSyncConfig acmLdapSyncConfig = new AcmLdapSyncConfig();
        acmLdapSyncConfig.setUserIdAttributeName("samAccountName");
        acmLdapSyncConfig.setMailAttributeName("mail");
        acmLdapSyncConfig.setBaseDC("dc=armedia");
        acmLdapSyncConfig.setAllUsersSortingAttribute("cn");
        unit = new AcmUserGroupsContextMapper(acmLdapSyncConfig);

        DirContextAdapter dirContextAdapter = new DirContextAdapter();
        dirContextAdapter.setAttributeValue("cn", "common");
        dirContextAdapter.setAttributeValue("userAccountControl", "user account control");
        dirContextAdapter.setAttributeValue("givenName", "given name");
        dirContextAdapter.setAttributeValue("mail", "***REMOVED***");
        dirContextAdapter.setAttributeValue("co", "United States of America");
        dirContextAdapter.setAttributeValue("c", "USA");
        dirContextAdapter.setAttributeValue("company", "armedia");
        dirContextAdapter.setAttributeValue("department", "technical");
        dirContextAdapter.setAttributeValue("title", "admin");
        dirContextAdapter.setAttributeValue("samAccountName", "ann-acm");
        dirContextAdapter.setAttributeValue("userPrincipalName", "ann-acm");
        dirContextAdapter.setAttributeValue("uid", null);
        dirContextAdapter.setAttributeValue("memberOf", "arkcase");

        AcmUser user = unit.setLdapUser(new AcmUser(), dirContextAdapter);

        assertNotNull(user);
        assertNotNull(user.getUserId());
        assertEquals(user.getUserId(), "ann-acm");
        assertNotNull(user.getMail());
        assertEquals(user.getMail(), "***REMOVED***");
        assertNotNull(user.getCountry());
        assertEquals(user.getCountry(), "United States of America");
        assertNotNull(user.getCountryAbbreviation());
        assertEquals(user.getCountryAbbreviation(), "USA");
        assertNotNull(user.getCompany());
        assertEquals(user.getCompany(), "armedia");
        assertNotNull(user.getDepartment());
        assertEquals(user.getDepartment(), "technical");
        assertNotNull(user.getTitle());
        assertEquals(user.getTitle(), "admin");
    }
}
