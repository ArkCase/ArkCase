package com.armedia.acm.services.transcribe.editor;

import java.beans.PropertyEditorSupport;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/01/2018
 */
public class BooleanEditor extends PropertyEditorSupport
{
    @Override
    public String getAsText()
    {
        return getValue() instanceof Boolean ? String.valueOf(getValue()) : "";
    }

    @Override
    public void setAsText(String value) throws IllegalArgumentException
    {
        setValue(new Boolean(Boolean.parseBoolean(value)));
    }

    @Override
    public String[] getTags()
    {
        return new String[] { String.valueOf(true), String.valueOf(false) };
    }
}
