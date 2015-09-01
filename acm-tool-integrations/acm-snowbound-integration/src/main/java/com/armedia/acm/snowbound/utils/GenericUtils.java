package com.armedia.acm.snowbound.utils;

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
                intArray[i] = Integer.parseInt(listItems[i]);
            }
        }
        return intArray;
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