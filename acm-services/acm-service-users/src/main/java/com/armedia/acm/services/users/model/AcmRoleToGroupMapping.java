package com.armedia.acm.services.users.model;

import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AcmRoleToGroupMapping
{
    private final Map<String, String> roleToGroupMap;

    public AcmRoleToGroupMapping(Map<String, String> roleToGroupMap)
    {
        this.roleToGroupMap = roleToGroupMap;
    }

    public Map<String, List<String>> getGroupToRolesMap()
    {
        // generate all value-key pairs from the original map and then group the keys by these values
        return getRoleToGroupsMap().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(it -> new AbstractMap.SimpleEntry<>(it, entry.getKey())))
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                        Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toList())));
    }

    public Map<String, Set<String>> getRoleToGroupsMap()
    {
        Function<String, Set<String>> groupsStringToSet = s -> {
            String[] groupsPerRole = s.split(",");
            return Arrays.stream(groupsPerRole)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toSet());
        };

        return roleToGroupMap.entrySet()
                .stream()
                .filter(entry -> StringUtils.isNotEmpty(entry.getKey()))
                .filter(entry -> StringUtils.isNotEmpty(entry.getValue()))
                .collect(
                        Collectors.toMap(entry -> entry.getKey().toUpperCase(),
                                entry -> groupsStringToSet.apply(entry.getValue()))
                );
    }

}
