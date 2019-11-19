package com.armedia.acm.configuration.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Util class for merging properties from property sources
 *
 * @author mario.gjurcheski on 09/26/2019.
 */
public class MergePropertiesUtil
{

    public static final String LEFT_BRACKET = "[";
    public static final String RIGHT_BRACKET = "]";

    public static void mergePropertiesFromSources(Map<String, Object> mergedConfig, Map<String, Object> props)
    {

        for (Map.Entry<String, Object> entry : props.entrySet())
        {
            String k = entry.getKey();
            Object v = entry.getValue();

            // put all the properties from the first source where there is no merge/delete.
            if ((!k.contains(MergeFlags.REMOVE.getSymbol()) && !k.contains(MergeFlags.MERGE.getSymbol()))
                    && (v.toString().indexOf(MergeFlags.MERGE.getSymbol()) != 0
                            && v.toString().indexOf(MergeFlags.REMOVE.getSymbol()) != 0))
            {
                mergedConfig.put(k, entry.getValue());
            }
            else
            {
                mergeCurrentSourceWithAlreadyMergedSources(mergedConfig, k, v);
            }
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
        mergedConfig.entrySet().removeIf(mergedEntry -> mergedEntry.getKey().startsWith(k));
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
        String[] subkeysList = value.split("\\.");
        return subkeysList[subkeysList.length - 1];
    }
}
