package com.armedia.acm.services.users.model;

import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AcmRoleToGroupMapping
{
    private Map<String, String> roleToGroupMap;
    private AcmGroupDao groupDao;
    public static final String GROUP_NAME_WILD_CARD = "@*";

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

    public static Function<String, Stream<String>> mapGroupsString(Function<String, List<AcmGroup>> findGroup)
    {
        return group -> {
            if (group.endsWith(GROUP_NAME_WILD_CARD))
            {
                String groupName = StringUtils.substringBeforeLast(group, GROUP_NAME_WILD_CARD);
                List<AcmGroup> matched = findGroup.apply(groupName);
                return matched.stream()
                        .map(AcmGroup::getName);
            }
            else
            {
                return Stream.of(group);
            }
        };
    }

    public Map<String, Set<String>> getRoleToGroupsMap()
    {
        Map<String, List<AcmGroup>> groupsCache = new HashMap<>();

        Function<String, Set<String>> groupsStringToSet = s -> {
            String[] groupsPerRole = s.split(",");

            return Arrays.stream(groupsPerRole)
                    .filter(StringUtils::isNotBlank)
                    .flatMap(mapGroupsString(name -> groupsCache.computeIfAbsent(name, it -> groupDao.findByMatchingName(it))))
                    .collect(Collectors.toSet());
        };

        return roleToGroupMap.entrySet()
                .stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getKey()))
                .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                .collect(
                        Collectors.toMap(entry -> {
                            String roleName = entry.getKey().trim().toUpperCase();
                            if (!roleName.startsWith("ROLE_"))
                            {
                                roleName = "ROLE_" + roleName;
                            }
                            return roleName;
                        },
                                entry -> groupsStringToSet.apply(entry.getValue().trim().toUpperCase())));
    }

    public void setRoleToGroupMap(Map<String, String> roleToGroupMap)
    {
        this.roleToGroupMap = roleToGroupMap;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }
}
