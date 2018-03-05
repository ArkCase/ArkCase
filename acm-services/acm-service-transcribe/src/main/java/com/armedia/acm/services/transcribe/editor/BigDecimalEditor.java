package com.armedia.acm.services.transcribe.editor;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/02/2018
 */
public class BigDecimalEditor extends PropertyEditorSupport
{
    @Override
    public String getAsText()
    {
        return getValue() instanceof BigDecimal ? ((BigDecimal) getValue()).toPlainString() : "";
    }

    @Override
    public void setAsText(String value) throws IllegalArgumentException
    {
        setValue(new BigDecimal(value));
    }
}
