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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MapperUtilsTest
{
    @Test
    public void arrayToSetMembers()
    {
        String[] array = new String[] {
                "CN=John Doe,OU=Promotions,OU=Marketing,DC=noam,dc=reskit,dc=com",
                "CN=Jane Doe ,OU=Promotions,OU=Marketing,DC=noam,dc=reskit,dc=com",
        };
        Set<String> expected = new HashSet<>(Arrays.asList("JOHN DOE", "JANE DOE"));
        Set<String> actual = MapperUtils.mapAttributes(array, MapperUtils.getRdnMappingFunction("cn"))
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        assertThat("Sets should be equal", actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void arrayToSetMembersWithUnusualNames()
    {
        String[] array = new String[] {
                "CN=John\\, Doe,OU=Promotions,OU=Marketing,DC=noam,dc=reskit,dc=com",
                "CN=Other\\, Name\\, With\\, Many\\, Commas,OU=Promotions,OU=Marketing,DC=noam,dc=reskit,dc=com",
        };
        Set<String> expected = new HashSet<>(Arrays.asList("JOHN, DOE", "OTHER, NAME, WITH, MANY, COMMAS"));
        Set<String> actual = MapperUtils.mapAttributes(array, MapperUtils.getRdnMappingFunction("cn"))
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        assertThat("Sets should be equal", actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void arrayToSetMembersWithInvalidName()
    {
        String[] array = new String[] {
                "invalid name"
        };
        Set<String> expected = new HashSet<>(Arrays.asList(""));
        Set<String> actual = MapperUtils.mapAttributes(array, MapperUtils.getRdnMappingFunction("cn"))
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        assertThat("Sets should be equal", actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void arrayToSetMembersWithNullName()
    {
        String[] array = new String[] { null, "", "     " };
        Set<String> expected = new HashSet<>(Arrays.asList("", "", ""));
        Set<String> actual = MapperUtils.mapAttributes(array, MapperUtils.getRdnMappingFunction("cn"))
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        assertThat("Sets should be equal", actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testUserIdMoreThen20Chars()
    {
        AcmLdapSyncConfig config = new AcmLdapSyncConfig();
        config.setUserPrefix("dco.");
        config.setUserDomain("armedia.com");
        config.setUserIdAttributeName("sAMAccountName");
        String userId = MapperUtils.buildUserId("veryyylongusername", config);
        assertEquals("dco.veryyylonguserna@armedia.com", userId);
    }

    @Test
    public void testUserIdLessThen20Chars()
    {
        AcmLdapSyncConfig config = new AcmLdapSyncConfig();
        config.setUserPrefix("dco.");
        config.setUserDomain("armedia.com");
        config.setUserIdAttributeName("sAMAccountName");
        String userId = MapperUtils.buildUserId("ann-acm", config);
        assertEquals("dco.ann-acm@armedia.com", userId);
    }

    @Test
    public void testUserIdUidAttribute()
    {
        AcmLdapSyncConfig config = new AcmLdapSyncConfig();
        config.setUserPrefix("dco");
        config.setUserDomain("armedia.com");
        config.setUserIdAttributeName("uid");
        String userId = MapperUtils.buildUserId("veryyyyyylongusername", config);
        assertEquals("dco.veryyyyyylongusername@armedia.com", userId);
    }

    @Test
    public void testPrincipalPrefixAndDomainIncluded()
    {
        String principal = "100.ann-acm@armedia.com";

        String result = MapperUtils.buildPrincipalName(principal, "100", "armedia.com");

        assertEquals(principal, result);
    }

    @Test
    public void testPrincipalPrefixIncluded()
    {
        String principal = "100.ann-acm";

        String result = MapperUtils.buildPrincipalName(principal, "100", "armedia.com");

        assertEquals("100.ann-acm@armedia.com", result);
    }

    @Test
    public void testLongPrincipalPrefixNotIncludedShouldTruncateTo20Chars()
    {
        String principal = "ann-acmloooooooooong";

        String result = MapperUtils.buildPrincipalName(principal, "100.", "armedia.com");

        assertEquals("100.ann-acmloooooooo@armedia.com", result);
    }

    @Test
    public void testLongPrincipalPrefixNotIncludedDomainIncluded()
    {
        String principal = "ann-acmloooooooooong@armedia.com";

        String result = MapperUtils.buildPrincipalName(principal, "100.", "armedia.com");

        assertEquals("100.ann-acmloooooooo@armedia.com", result);
    }
}
