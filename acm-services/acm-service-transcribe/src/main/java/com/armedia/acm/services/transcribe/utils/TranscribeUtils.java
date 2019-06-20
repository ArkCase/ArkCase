package com.armedia.acm.services.transcribe.utils;

/*-
 * #%L
 * ACM Service: Transcribe
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.services.transcribe.model.TranscribeItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
    private final static Logger LOG = LogManager.getLogger(TranscribeUtils.class);

    public static String getFirstWords(String text, int numberOfWords)
    {
        if (text != null)
        {
            String[] words = StringUtils.split(text, " ");
            return words.length > numberOfWords ? StringUtils.join(words, " ", 0, numberOfWords) + " ..."
                    : StringUtils.join(words, " ", 0, words.length);
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
                    clone = new TranscribeItem();
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
        if (items != null && !items.isEmpty())
        {
            return String.join(" ", items.stream().map(item -> item.getText()).collect(Collectors.toList()));
        }

        return null;
    }
}
