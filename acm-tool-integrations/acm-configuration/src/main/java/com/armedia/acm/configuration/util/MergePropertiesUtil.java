package com.armedia.acm.configuration.util;

/*-
 * #%L
 * ACM Tool Integrations: Configuration Library
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.configuration.api.environment.PropertySource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Util class for merging properties from property sources
 *
 * @author mario.gjurcheski on 09/26/2019.
 */
public class MergePropertiesUtil
{

    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";

    public static void mergePropertiesFromSources(Map<String, Object> mergedConfig, Map<String, Object> props, PropertySource sourceName,
            List<String> annotatedProperties, List<String> activeApplications)
    {

        List<String> mergedMapProperties = new LinkedList<>();

        for (Map.Entry<String, Object> entry : props.entrySet())
        {
            String k = entry.getKey();
            Object v = entry.getValue();

            // put all the properties from the first source where there is no merge/delete.
            if ((!k.contains(MergeFlags.REMOVE.getSymbol()) && !k.contains(MergeFlags.MERGE.getSymbol()))
                    && (v.toString().indexOf(MergeFlags.MERGE.getSymbol()) != 0
                            && v.toString().indexOf(MergeFlags.REMOVE.getSymbol()) != 0))
            {
                String parent = k.contains(".") ? k.substring(0, k.lastIndexOf(".")) : k;

                if (!activeApplications.contains(sourceName.extractApplicationNameFromSourceName()) && (!k.contains(LEFT_BRACKET))
                        && annotatedProperties.contains(parent))
                {
                    overrideAnnotatedPropertyMap(mergedConfig, mergedMapProperties, k, v, parent);
                }
                else
                {
                    mergedConfig.put(k, v);
                }
            }
            else
            {
                mergeCurrentSourceWithAlreadyMergedSources(mergedConfig, k, v);
            }
        }
    }

    private static void overrideAnnotatedPropertyMap(Map<String, Object> mergedConfig, List<String> executed, String k, Object v,
            String parent)
    {
        if (!parent.contains(MergeFlags.REMOVE.getSymbol()) && !parent.contains(MergeFlags.MERGE.getSymbol()))
        {
            if (!executed.contains(parent))
            {
                removeMergedPropertyIfAlreadyMerged(mergedConfig, parent);
                executed.add(parent);
            }
            mergedConfig.put(k, v);
        }
    }

    private static void mergeCurrentSourceWithAlreadyMergedSources(Map<String, Object> mergedConfig, String k, Object v)
    {

        if (!k.contains(LEFT_BRACKET) && !k.contains(RIGHT_BRACKET) && !v.toString().equals(""))
        {
            mergeNonListProperty(mergedConfig, k, v);
        }
        else
        {
            if (k.contains(MergeFlags.REMOVE.getSymbol()) && v.toString().indexOf(MergeFlags.MERGE.getSymbol()) != 0)
            {
                if (mergedConfig.containsKey(k))
                {
                    k = k.replace(MergeFlags.REMOVE.getSymbol(), "");
                    v = v.toString().replace(MergeFlags.REMOVE.getSymbol(), "");
                    mergedConfig.remove(k);
                }
            }

            // if we want to delete already merged property value
            if (v.toString().contains(MergeFlags.REMOVE.getSymbol()))
            {
                k = removeAlreadyMergedListElement(mergedConfig, k, v);
                mergeAndRearrangeListElements(mergedConfig, k);
            }

            if (k.contains(MergeFlags.MERGE.getSymbol()) && v.toString().indexOf(MergeFlags.REMOVE.getSymbol()) != 0
                    && !k.contains(MergeFlags.REMOVE.getSymbol()))
            {
                mergeListEntry(mergedConfig, k, v);
            }
            else
            {
                removeAndRearrangeAlreadyMergedListEntries(mergedConfig, k, v);
            }
        }
    }

    private static void mergeNonListProperty(Map<String, Object> mergedConfig, String k, Object v)
    {
        if (k.contains(MergeFlags.REMOVE.getSymbol()))
        {
            k = k.replace(MergeFlags.REMOVE.getSymbol(), "");
            mergedConfig.remove(k);
        }
        else if (k.contains(MergeFlags.MERGE.getSymbol()))
        {
            k = k.replace(MergeFlags.MERGE.getSymbol(), "");
            mergedConfig.put(k, v);
        }
    }

    private static void removeAndRearrangeAlreadyMergedListEntries(Map<String, Object> mergedConfig, String k, Object v)
    {
        if (v.toString().indexOf(MergeFlags.REMOVE.getSymbol()) == 0 || k.contains(MergeFlags.REMOVE.getSymbol()))
        {
            k = k.replace(MergeFlags.MERGE.getSymbol(), "");
            k = k.replace(MergeFlags.REMOVE.getSymbol(), "");

            if (k.contains(LEFT_BRACKET))
            {

                int positionBeforeLeftBracket = k.indexOf(LEFT_BRACKET);
                String rootKey = k.substring(0, positionBeforeLeftBracket + 1);
                mergedConfig.remove(k);
                mergeAndRearrangeListElements(mergedConfig, rootKey);

            }
            else
            {
                removeMergedPropertyIfAlreadyMerged(mergedConfig, k);
            }
        }
    }

    private static String removeAlreadyMergedListElement(Map<String, Object> mergedConfig, String k, Object v)
    {
        Object value = v.toString().replace(MergeFlags.REMOVE.getSymbol(), "");

        for (Iterator<Map.Entry<String, Object>> it = mergedConfig.entrySet().iterator(); it.hasNext();)
        {
            if (k.contains(LEFT_BRACKET))
            {
                int positionBeforeLeftBracket = k.indexOf(LEFT_BRACKET);
                k = k.substring(0, positionBeforeLeftBracket + 1);
                k = k.replace(MergeFlags.MERGE.getSymbol(), "");
            }

            Map.Entry<String, Object> mergedEntry = it.next();
            if (mergedEntry.getKey().contains(k)
                    && mergedEntry.getValue().toString().equals(value.toString()))
            {
                it.remove();
            }
        }

        return k;
    }

    private static void removeMergedPropertyIfAlreadyMerged(Map<String, Object> mergedConfig, String k)
    {
        mergedConfig.entrySet().removeIf(mergedEntry -> mergedEntry.getKey().equals(k) || mergedEntry.getKey().startsWith(k + "."));
    }

    private static void mergeListEntry(Map<String, Object> mergedConfig, String k, Object v)
    {
        k = k.replace(MergeFlags.MERGE.getSymbol(), "");

        int counter = 0;

        for (Map.Entry<String, Object> mergedEntry : mergedConfig.entrySet())
        {
            int positionBeforeLeftBracket = k.indexOf(LEFT_BRACKET);
            k = k.substring(0, positionBeforeLeftBracket + 1);

            if (mergedEntry.getKey().contains(k))
            {
                counter++;
            }
        }

        String sb = appendPosition(k, counter);

        mergedConfig.put(sb, v);

    }

    public static void mergeAndRearrangeListElements(Map<String, Object> mergedConfig, String rootKey)
    {
        // check if there is need of rearrange the elements of the list
        int counter = 0;
        Map<String, Object> rearrangedKeys = new HashMap<>();

        Iterator<Map.Entry<String, Object>> iter = mergedConfig.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry<String, Object> mergedEntry = iter.next();
            if (mergedEntry.getKey().startsWith(rootKey))
            {
                String sb = appendPosition(rootKey, counter);
                rearrangedKeys.put(sb, mergedEntry.getValue());
                iter.remove();
                counter++;
            }
        }
        mergedConfig.putAll(rearrangedKeys);
    }

    private static String appendPosition(String rootKey, int counter)
    {
        return String.format("%s%s%s", rootKey, counter, RIGHT_BRACKET);
    }

    public static String getLastKey(String value)
    {
        String[] subKeysList = value.split("\\.");
        return subKeysList[subKeysList.length - 1];
    }
}
