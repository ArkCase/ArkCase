package com.armedia.acm.services.notification.service;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class FormatDateTimeMethodModel implements TemplateMethodModelEx
{

    @Override
    public Object exec(List args) throws TemplateModelException
    {
        if (args.size() != 2)
        {
            throw new TemplateModelException("Wrong arguments");
        }
        if (args.get(0) == null)
        {
            return null;
        }
        TemporalAccessor time = (TemporalAccessor) ((StringModel) args.get(0)).getWrappedObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(((SimpleScalar) args.get(1)).getAsString());
        return formatter.format(time);
    }
}
