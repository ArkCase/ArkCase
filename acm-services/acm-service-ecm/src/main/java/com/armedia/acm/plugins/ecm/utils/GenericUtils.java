package com.armedia.acm.plugins.ecm.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by joseph.mcgrady on 9/24/2015.
 */
public class GenericUtils {

    /**
     * Takes a string containing a comma separated list of string values and
     * generates a list of trimmed String objects for each individual entry
     * @param commaSeparatedList - comma separated list of string values (e.x. "1,abc,6c")
     * @return array of Strings parsed from the list with trailing/leading whitespace removed from each entry
     */
    public static List<String> parseStringList(String commaSeparatedList) {
        List<String> stringList = new ArrayList<String>();
        if (commaSeparatedList != null && commaSeparatedList.length() > 0) {
            String[] listItems = commaSeparatedList.split(",");
            stringList = Arrays.asList(listItems).stream().map(String::trim).collect(Collectors.toList());
        }
        return stringList;
    }

    /**
     * Determines if the file type is present in the supplied list of acceptable types
     * @param fileType - type of file to search for in the list
     * @param typeListString - comma separated types (e.x. ArkCase model type) to search
     * @return true if the specified type is in the list, false otherwise
     */
    public static boolean isFileTypeInList(String fileType, String typeListString) {
        boolean isInList = false;
        if (fileType != null && typeListString != null) {
            List<String> typeList = parseStringList(typeListString);
            for (String supportedType : typeList) {
                if (supportedType.equalsIgnoreCase(fileType)) {
                    isInList = true;
                    break;
                }
            }
        }
        return isInList;
    }
}