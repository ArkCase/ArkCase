package com.armedia.acm.snowbound.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joseph.mcgrady on 8/30/2015.
 */
public class WebUtils {

    /**
     * Extracts only the argument section from a full url
     * @param fullUrl url string which may contain the protocol, host, port, app context, and argument section
     * @return url argument section (after the '?')
     */
    public static String getUrlArgumentSection(String fullUrl) {
        String argUrlSection = fullUrl;
        if (fullUrl != null) {
            int argSectionStart = fullUrl.lastIndexOf('?');
            if (argSectionStart >= 0 && (argSectionStart + 1 < fullUrl.length())) {
                argUrlSection = fullUrl.substring(argSectionStart + 1);
            }
        }
        return argUrlSection;
    }

    /**
     * Builds a hash map containing the arguments contained in the given url
     * @param urlArgString - url which contains an argument section (e.x. ?arg1=val1&arg2=val2)
     * @return map of the key value pair arguments from the url
     */
    public static Map<String, String> getUrlArguments(String urlArgString) {
        Map<String, String> argMap = new HashMap<String, String>();
        if (urlArgString != null) {
            urlArgString = getUrlArgumentSection(urlArgString);
            String[] argSections = urlArgString.split("&");
            for (String arg : argSections) {
                String[] keyValuePair = arg.split("=");
                if (keyValuePair.length == 2) {
                    argMap.put(keyValuePair[0], keyValuePair[1]);
                }
            }
        }
        return argMap;
    }

    /**
     * Finds the value of the specified parameter in the url
     * @param paramName - name of the url parameter whose value will be returned
     * @param url - full url string which contains the arguments to search for the parameter
     * @return value of the parameter, or null if it was not found in the url
     */
    public static String getUrlArgument(String paramName, String url) {
        String paramValue = null;
        Map<String, String> argMap = getUrlArguments(url);
        if (argMap.containsKey(paramName))
            paramValue = argMap.get(paramName);
        return paramValue;
    }

    /**
     * Generates a random string for a multipart form boundary in the body of an HTTP POST request
     * @return boundary string which separates sections in a multipart POST request
     */
    public static String createMultipartFormBoundary() {
        return Long.toHexString(System.currentTimeMillis());
    }
}