package com.armedia.acm.services.users.service.ldap;

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

import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapGroupNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LdapGroupUtils
{
    public Set<LdapGroup> findDescendantsForLdapGroupNode(LdapGroupNode startNode)
    {
        Set<LdapGroup> visitedNodes = new HashSet<>();

        Queue<LdapGroupNode> queue = new LinkedList<>();

        queue.add(startNode);

        while (!queue.isEmpty())
        {
            startNode = queue.poll();
            visitedNodes.add(startNode.getLdapGroup());

            startNode.getNodes().stream()
                    .filter(it -> !visitedNodes.contains(it.getLdapGroup()))
                    .forEach(queue::add);

        }
        return visitedNodes;
    }

    /**
     * To find ascendants to LdapGroup we need to traverse from all groups in their descendants and
     * search for the target node. We want to avoid using `memberOf` attribute.
     *
     * @param targetNode
     *            The node we are finding ascendants
     * @param ldapGroups
     *            All the ldap groups
     * @return Set of ascendants
     */
    public Set<LdapGroup> findAscendantsForLdapGroupNode(LdapGroupNode targetNode, Set<LdapGroup> ldapGroups)
    {
        Set<LdapGroupNode> allNodes = ldapGroups.stream()
                .map(LdapGroupNode::new)
                .collect(Collectors.toSet());

        return allNodes.stream()
                .flatMap(node -> findAscendantsStream(targetNode, node))
                .map(LdapGroupNode::getLdapGroup)
                .collect(Collectors.toSet());
    }

    private Stream<? extends LdapGroupNode> findAscendantsStream(LdapGroupNode targetNode, LdapGroupNode startNode)
    {
        Set<LdapGroup> visitedNodes = new HashSet<>();
        Queue<Set<LdapGroupNode>> pathToTargetNode = new LinkedList<>();
        Queue<LdapGroupNode> queue = new LinkedList<>();
        queue.add(startNode);
        pathToTargetNode.add(new HashSet<>(Arrays.asList(startNode)));
        while (!queue.isEmpty())
        {
            startNode = queue.poll();
            Set<LdapGroupNode> path = pathToTargetNode.poll();
            if (startNode.equals(targetNode))
            {
                path.remove(startNode);
                return path.stream();
            }
            visitedNodes.add(startNode.getLdapGroup());

            startNode.getNodes().stream()
                    .filter(it -> !visitedNodes.contains(it.getLdapGroup()))
                    .forEach(it -> {
                        queue.add(it);
                        Set<LdapGroupNode> extendPath = new HashSet<>(path);
                        extendPath.add(it);
                        pathToTargetNode.add(extendPath);
                    });
        }
        return new HashSet<LdapGroupNode>().stream();
    }
}
