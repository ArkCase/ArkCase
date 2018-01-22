package com.armedia.acm.services.users.model.ldap;

import static org.hamcrest.Matchers.containsInAnyOrder;
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

}