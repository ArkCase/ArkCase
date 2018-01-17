package com.armedia.acm.services.users.model;

import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AcmRoleToGroupMapping
{
    private Map<String, String> roleToGroupMap;

    public void reloadRoleToGroupMap(Properties properties)
    {
        roleToGroupMap = properties.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> entry.getValue().toString()));
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
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
        };

        return roleToGroupMap.entrySet()
                .stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getKey()))
                .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                .collect(
                        Collectors.toMap(entry -> {
                            String groupName = entry.getKey().trim().toUpperCase();
                            if (!groupName.startsWith("ROLE_"))
                            {
                                groupName = "ROLE_" + groupName;
                            }
                            return groupName;
                        },
                                entry -> groupsStringToSet.apply(entry.getValue().trim().toUpperCase())));
    }

    public void setRoleToGroupMap(Map<String, String> roleToGroupMap)
    {
        this.roleToGroupMap = roleToGroupMap;
    }
}
