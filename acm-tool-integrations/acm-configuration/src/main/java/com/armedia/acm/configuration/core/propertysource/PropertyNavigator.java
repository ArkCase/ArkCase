package com.armedia.acm.configuration.core.propertysource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class {@code PropertyNavigator} is used to navigate through the property key and create necessary Maps and Lists
 * making up the nested structure to finally set the property value at the leaf node.
 * <p>
 * The following rules in yml/json are implemented:
 *
 * <pre>
 * 1. an array element can be:
 *    - a value (leaf)
 *    - a map
 *    - a nested array
 * 2. a map value can be:
 *    - a value (leaf)
 *    - a nested map
 *    - an array
 * </pre>
 *
 * Created by mario.gjurcheski on 08/16/2019.
 */
public class PropertyNavigator
{

    private enum NodeType
    {
        LEAF, MAP, ARRAY
    }

    private final String propertyKey;
    private int currentPos;
    private NodeType valueType;

    public PropertyNavigator(String propertyKey)
    {
        this.propertyKey = propertyKey;
        currentPos = -1;
        valueType = NodeType.MAP;
    }

    public void setMapValue(Map<String, Object> map, Object value)
    {
        String key = getKey();
        if (NodeType.MAP.equals(valueType))
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedMap = (Map<String, Object>) map.get(key);
            if (nestedMap == null)
            {
                nestedMap = new LinkedHashMap<>();
                map.put(key, nestedMap);
            }
            setMapValue(nestedMap, value);
        }
        else if (NodeType.ARRAY.equals(valueType))
        {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) map.get(key);
            if (list == null)
            {
                list = new ArrayList<>();
                map.put(key, list);
            }
            setListValue(list, value);
        }
        else
        {
            map.put(key, value);
        }
    }

    private void setListValue(List<Object> list, Object value)
    {
        int index = getIndex();
        // Fill missing elements if needed
        while (list.size() <= index)
        {
            list.add(null);
        }
        if (NodeType.MAP.equals(valueType))
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) list.get(index);
            if (map == null)
            {
                map = new LinkedHashMap<>();
                list.set(index, map);
            }
            setMapValue(map, value);
        }
        else if (NodeType.ARRAY.equals(valueType))
        {
            @SuppressWarnings("unchecked")
            List<Object> nestedList = (List<Object>) list.get(index);
            if (nestedList == null)
            {
                nestedList = new ArrayList<>();
                list.set(index, nestedList);
            }
            setListValue(nestedList, value);
        }
        else
        {
            list.set(index, value);
        }
    }

    private int getIndex()
    {
        // Consider [
        int start = currentPos + 1;

        for (int i = start; i < propertyKey.length(); i++)
        {
            char c = propertyKey.charAt(i);
            if (c == ']')
            {
                currentPos = i;
                break;
            }
            else if (!Character.isDigit(c))
            {
                throw new IllegalArgumentException("Invalid key: " + propertyKey);
            }
        }
        // If no closing ] or if '[]'
        if (currentPos < start || currentPos == start)
        {
            throw new IllegalArgumentException("Invalid key: " + propertyKey);
        }
        else
        {
            int index = Integer.parseInt(propertyKey.substring(start, currentPos));
            // Skip the closing ]
            currentPos++;
            if (currentPos == propertyKey.length())
            {
                valueType = NodeType.LEAF;
            }
            else
            {
                switch (propertyKey.charAt(currentPos))
                {
                case '.':
                    valueType = NodeType.MAP;
                    break;
                case '[':
                    valueType = NodeType.ARRAY;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid key: " + propertyKey);
                }
            }
            return index;
        }
    }

    private String getKey()
    {
        // Consider initial value or previous char '.' or '['
        int start = currentPos + 1;
        for (int i = start; i < propertyKey.length(); i++)
        {
            char currentChar = propertyKey.charAt(i);
            if (currentChar == '.')
            {
                valueType = NodeType.MAP;
                currentPos = i;
                break;
            }
            else if (currentChar == '[')
            {
                valueType = NodeType.ARRAY;
                currentPos = i;
                break;
            }
        }
        // If there's no delimiter then it's a key of a leaf
        if (currentPos < start)
        {
            currentPos = propertyKey.length();
            valueType = NodeType.LEAF;
            // Else if we encounter '..' or '.[' or start of the property is . or [ then it's invalid
        }
        else if (currentPos == start)
        {
            throw new IllegalArgumentException("Invalid key: " + propertyKey);
        }
        return propertyKey.substring(start, currentPos);
    }
}