package com.armedia.acm.services.users.service.group;

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

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupConstants;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class AcmGroupUtils
{

    public static String buildAncestorsStringForAcmGroup(AcmGroup startNode)
    {
        String targetNode = startNode.getName();

        Set<AcmGroup> visitedNodes = new HashSet<>();

        Queue<AcmGroup> queue = new LinkedList<>();

        queue.add(startNode);

        while (!queue.isEmpty())
        {
            startNode = queue.poll();
            visitedNodes.add(startNode);

            startNode.getMemberOfGroups().stream()
                    .filter(it -> !visitedNodes.contains(it))
                    .forEach(queue::add);
        }

        String ancestorsString = visitedNodes.stream()
                .map(AcmGroup::getName)
                .filter(node -> !node.equals(targetNode))
                .sorted()
                .collect(Collectors.joining(","));

        return ancestorsString.isEmpty() ? null : ancestorsString;
    }

    public static Set<AcmGroup> findDescendantsForAcmGroup(AcmGroup startNode)
    {
        AcmGroup targetNode = startNode;

        Set<AcmGroup> visitedNodes = new HashSet<>();

        Queue<AcmGroup> queue = new LinkedList<>();

        queue.add(startNode);

        while (!queue.isEmpty())
        {
            startNode = queue.poll();
            visitedNodes.add(startNode);

            startNode.getMemberGroups().stream()
                    .filter(it -> !visitedNodes.contains(it))
                    .forEach(queue::add);
        }

        visitedNodes.remove(targetNode);

        return visitedNodes;
    }

    public static String getAscendantsString(Set<String> ascendantGroupNames)
    {
        return ascendantGroupNames.stream()
                .sorted()
                .collect(Collectors.joining(AcmGroupConstants.ASCENDANTS_STRING_DELIMITER));
    }
}
