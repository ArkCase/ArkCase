package com.armedia.acm.services.transcribe.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class TranscribeUtils
{
    public static String getFirstWords(String text, int numberOfWords)
    {
        if (text != null)
        {
            String[] words = StringUtils.split(text, " ");
            return words.length > numberOfWords ? StringUtils.join(words, " ", 0, numberOfWords) + " ..." : StringUtils.join(words, " ", 0, words.length);
        }

        return "";
    }

    public static String extractMediaType(String mimeType)
    {
        if (mimeType != null && mimeType.contains("/") && !mimeType.endsWith("/") && mimeType.chars().filter(c -> c == '/').count() == 1)
        {
            return mimeType.substring(mimeType.indexOf("/") + 1, mimeType.length());
        }

        return "";
    }
}
