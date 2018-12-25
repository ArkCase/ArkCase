package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.AcmApplication;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplatingEngine
{
    private AcmApplication acmApplication;

    public String process(String emailBodyTemplate, String modelReferenceName, Object model) throws TemplateException, IOException
    {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_22));
        cfg.setClassicCompatible(true); // does't throw error on null values
        cfg.setDateFormat("MM/dd/yyyy");
        cfg.setDateTimeFormat("MM/dd/yyyy HH:MM");

        Map<String, Object> templatingModel = new HashMap<>();
        templatingModel.put(modelReferenceName, model);
        // formatting date time for java.time.* classes isn't implemented in Freemarker
        templatingModel.put("formatDateTime", new FormatDateTimeMethodModel());
        // set the application base URL as a variable, to be used in any templates
        templatingModel.put("baseURL", getAcmApplication().getBaseUrl());

        Template t = new Template("templateName", new StringReader(emailBodyTemplate), cfg);

        Writer out = new StringWriter();
        t.process(templatingModel, out);

        return out.toString();
    }

    public AcmApplication getAcmApplication()
    {
        return acmApplication;
    }

    public void setAcmApplication(AcmApplication acmApplication)
    {
        this.acmApplication = acmApplication;
    }
}
