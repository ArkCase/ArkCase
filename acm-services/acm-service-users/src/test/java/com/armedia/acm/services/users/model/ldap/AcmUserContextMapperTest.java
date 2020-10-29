package com.armedia.acm.services.users.model.ldap;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.springframework.ldap.core.DirContextAdapter;

public class AcmUserContextMapperTest extends EasyMockSupport
{
    private AcmUserContextMapper unit;

    @Test
    public void setLdapUser_test()
    {
        AcmLdapSyncConfig acmLdapSyncConfig = new AcmLdapSyncConfig();
        acmLdapSyncConfig.setUserIdAttributeName("samAccountName");
        acmLdapSyncConfig.setMailAttributeName("mail");
        acmLdapSyncConfig.setBaseDC("dc=armedia");
        acmLdapSyncConfig.setAllUsersSortingAttribute("cn");
        acmLdapSyncConfig.setDirectoryType("openldap");
        acmLdapSyncConfig.setUserDomain("arkcase.org");
        unit = new AcmUserContextMapper(acmLdapSyncConfig);

        DirContextAdapter dirContextAdapter = new DirContextAdapter();
        dirContextAdapter.setAttributeValue("cn", "common");
        dirContextAdapter.setAttributeValue("userAccountControl", "user account control");
        dirContextAdapter.setAttributeValue("givenName", "given name");
        dirContextAdapter.setAttributeValue("mail", "ann-acm@arkcase.org");
        dirContextAdapter.setAttributeValue("co", "United States of America");
        dirContextAdapter.setAttributeValue("c", "USA");
        dirContextAdapter.setAttributeValue("company", "armedia");
        dirContextAdapter.setAttributeValue("department", "technical");
        dirContextAdapter.setAttributeValue("title", "admin");
        dirContextAdapter.setAttributeValue("samAccountName", "ann-acm");
        dirContextAdapter.setAttributeValue("userPrincipalName", "ann-acm");
        dirContextAdapter.setAttributeValue("uid", null);
        dirContextAdapter.setAttributeValue("memberOf", "arkcase");

        LdapUser user = unit.mapToLdapUser(dirContextAdapter);

        assertNotNull(user);
        assertNotNull(user.getUserId());
        assertEquals(user.getUserId(), "ann-acm@arkcase.org");
        assertNotNull(user.getMail());
        assertEquals(user.getMail(), "ann-acm@arkcase.org");
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
