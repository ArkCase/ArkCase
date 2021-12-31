package com.armedia.acm.camelcontext.utils;

public class FileCamelUtils {

    public static String replaceSurrogateCharacters(String s, Character newChar)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if (Character.isHighSurrogate(c))
            {
                sb.append(newChar);
            }
            else if (!Character.isLowSurrogate(c))
            {
                sb.append(c);
            }
        }
        return sb.toString();

    }
}
