package com.armedia.acm.snowbound.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph.mcgrady on 8/30/2015.
 */
public class GenericUtils {

    /**
     * Takes a string containing a comma separated list of integer values and
     * generates a list of integer objects
     * @param integerListString - comma separated list of integer values (e.x. "1,4,6")
     * @return array of integers parsed from the list
     * @throws Exception if the integer string is not formatted correctly
     */
    public static int[] parseIntegerList(String integerListString) throws Exception {
        int[] intArray = null;
        if (integerListString != null && integerListString.length() > 0) {
            String[] listItems = integerListString.split(",");
            intArray = new int[listItems.length];
            for (int i = 0; i < listItems.length; i++) {
                intArray[i] = Integer.parseInt(listItems[i].trim());
            }
        }
        return intArray;
    }

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
            for (String listItem : listItems) {
                stringList.add(listItem.trim());
            }
        }
        return stringList;
    }

    /**
     * Creates an indexed document name based on the supplied parent name.
     * @param documentName - name of the parent document
     * @param extensionType - extension type (e.x.: .pdf) to use for the new name
     * @param subIndex - index number of the new document name
     * @return indexed document name (e.x. document_0.pdf)
     */
    public static String createSubDocumentName(String documentName, String extensionType, int subIndex) {
        String subName = documentName;
        int extensionIndex = documentName.lastIndexOf(".");
        if (extensionIndex >= 0) {
            subName = documentName.substring(0, extensionIndex);
        }
        subName += "_" + subIndex + "." + extensionType;
        return subName;
    }
}