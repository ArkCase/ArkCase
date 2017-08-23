package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapGroupNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupBFS
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

    // the idea is to go only through group's member groups and avoid use of "memberOf" attribute
    public Set<LdapGroup> findAscendantsForLdapGroupNode(LdapGroupNode targetNode, Set<LdapGroup> ldapGroups)
    {
        Set<LdapGroupNode> allNodes = ldapGroups.stream()
                .map(LdapGroupNode::new)
                .collect(Collectors.toSet());

        return allNodes.stream()
                .flatMap(node -> {
                    Set<LdapGroup> visitedNodes = new HashSet<>();
                    Queue<Set<LdapGroupNode>> pathToTargetNode = new LinkedList<>();
                    Queue<LdapGroupNode> queue = new LinkedList<>();
                    queue.add(node);
                    pathToTargetNode.add(new HashSet<>(Arrays.asList(node)));
                    while (!queue.isEmpty())
                    {
                        node = queue.poll();
                        Set<LdapGroupNode> path = pathToTargetNode.poll();
                        if (node.equals(targetNode))
                        {
                            path.remove(node);
                            return path.stream();
                        }
                        visitedNodes.add(node.getLdapGroup());

                        node.getNodes().stream()
                                .filter(it -> !visitedNodes.contains(it.getLdapGroup()))
                                .forEach(it -> {
                                    queue.add(it);
                                    Set<LdapGroupNode> extendPath = new HashSet<>(path);
                                    extendPath.add(it);
                                    pathToTargetNode.add(extendPath);
                                });
                    }
                    return new HashSet<LdapGroupNode>().stream();
                })
                .map(LdapGroupNode::getLdapGroup)
                .collect(Collectors.toSet());
    }
}
