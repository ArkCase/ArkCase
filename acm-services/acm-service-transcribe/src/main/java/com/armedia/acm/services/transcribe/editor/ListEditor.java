package com.armedia.acm.services.transcribe.editor;

import com.armedia.acm.services.transcribe.model.TranscribeServiceProvider;

import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/01/2018
 */
public class ListEditor extends PropertyEditorSupport
{
    @Override
    public String getAsText()
    {
        if (getValue() instanceof List && getValue() != null)
        {
            return StringUtils.join((List) getValue(), ", ");
        }

        return "";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        if (text != null)
        {
            List<String> items = Arrays.asList(text.split("\\s*,\\s*"));
            List<TranscribeServiceProvider> providers = items.stream().map(item -> TranscribeServiceProvider.valueOf(item))
                    .collect(Collectors.toList());
            setValue(providers);
            return;
        }
        throw new java.lang.IllegalArgumentException(text);
    }
}
