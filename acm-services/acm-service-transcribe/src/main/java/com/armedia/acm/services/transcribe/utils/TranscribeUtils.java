package com.armedia.acm.services.transcribe.utils;

import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.services.transcribe.model.TranscribeItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class TranscribeUtils
{
    private final static Logger LOG = LoggerFactory.getLogger(TranscribeUtils.class);

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

    public static <T> Predicate<T> distinctByProperty(Function<? super T, ?> propertyExtractor)
    {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(propertyExtractor.apply(t));
    }

    public static List<TranscribeItem> clone(List<TranscribeItem> items)
    {
        List<String> excludeFields = new ArrayList<>();
        excludeFields.add("className");

        ArkCaseBeanUtils arkCaseBeanUtils = new ArkCaseBeanUtils();
        arkCaseBeanUtils.setExcludeFields(excludeFields);

        if (items != null)
        {
            List<TranscribeItem> clonedItems = new ArrayList<>();
            items.forEach(item -> {
                TranscribeItem clone = null;
                try
                {
                    clone =  new TranscribeItem();
                    arkCaseBeanUtils.copyProperties(clone, item);
                }
                catch (IllegalAccessException | InvocationTargetException e)
                {
                    LOG.warn("Could not copy properties from [{}] to [{}]", item, clone);
                }

                if (clone != null)
                {
                     clonedItems.add(clone);
                }
            });

            return clonedItems;
        }

        return null;
    }

    public static String getText(List<TranscribeItem> items)
    {
        if (items != null && items.size() > 0)
        {
            return String.join(" ", items.stream().map(item -> item.getText()).collect(Collectors.toList()));
        }

        return null;
    }
}
