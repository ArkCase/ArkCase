package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapGroupNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * To find ascendants to LdapGroup we need to traverse from all groups in their descendants and
     * search for the target node. We want to avoid using `memberOf` attribute.
     *
     * @param targetNode The node we are finding ascendants
     * @param ldapGroups All the ldap groups
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
