package com.armedia.acm.services.users.service.group;

import com.armedia.acm.services.users.model.group.AcmGroup;

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
                .collect(Collectors.joining(","));
    }
}