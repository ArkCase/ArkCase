package com.armedia.acm.services.templateconfiguration.service;

/*-
 * #%L
 * ACM Service: Notification
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

import static org.reflections.Reflections.log;

import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.services.holiday.service.DateTimeService;
import com.armedia.acm.services.templateconfiguration.model.CorrespondenceMergeField;
import com.armedia.acm.services.templateconfiguration.model.FormatDateTimeMethodModel;

import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplatingEngine
{
    public static final String DATE_TYPE = "Date";
    public static final String DATE_TIME_TYPE = "LocalDateTime";
    private ApplicationConfig applicationConfig;
    private CorrespondenceMergeFieldManager mergeFieldManager;

    public String process(String emailBodyTemplate, String modelReferenceName, Object model) throws TemplateException, IOException
    {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_22));
        cfg.setClassicCompatible(true); // does't throw error on null values
        cfg.setDateFormat("MM/dd/yyyy");
        cfg.setDateTimeFormat("MM/dd/yyyy HH:mm");

        Map<String, Object> templatingModel = new HashMap<>();
        templatingModel.put(modelReferenceName, model);
        // formatting date time for java.time.* classes isn't implemented in Freemarker
        templatingModel.put("formatDateTime", new FormatDateTimeMethodModel());
        // set the application base URL as a variable, to be used in any templates
        templatingModel.put("baseURL", applicationConfig.getBaseUrl());
        templatingModel.put("basePortalURL", applicationConfig.getBasePortalUrl());

        emailBodyTemplate = checkIfTemplateBodyContainsMergeTerms(emailBodyTemplate, model);

        Template t = new Template("templateName", new StringReader(emailBodyTemplate), cfg);

        Writer out = new StringWriter();
        t.process(templatingModel, out);

        return out.toString();
    }

    private String checkIfTemplateBodyContainsMergeTerms(String emailBodyTemplate, Object model)
    {
        List<String> spelExpressions = getSpelExpressions(emailBodyTemplate);
        if (spelExpressions != null)
        {
            Map<String, String> expressionsToEvaluate = new HashMap<>();
            StandardEvaluationContext stContext = new StandardEvaluationContext(model);
            SpelParserConfiguration config = new SpelParserConfiguration(true, true);
            SpelExpressionParser parser = new SpelExpressionParser(config);

            try
            {
                stContext.registerFunction("toClientDateTimeTimezone",
                        DateTimeService.class.getDeclaredMethod("toClientDateTimeTimezone", LocalDateTime.class));
                stContext.registerFunction("toClientDateTimezone",
                        DateTimeService.class.getDeclaredMethod("toClientDateTimezone", LocalDateTime.class));
                stContext.registerFunction("dateToClientDateTimezone",
                        DateTimeService.class.getDeclaredMethod("dateToClientDateTimezone", Date.class));
                stContext.registerFunction("dateToClientDateTimeTimezone",
                        DateTimeService.class.getDeclaredMethod("dateToClientDateTimeTimezone", Date.class));
                stContext.registerFunction("currentDateToClientDate",
                        DateTimeService.class.getDeclaredMethod("currentDateToClientDate"));
                stContext.registerFunction("currentDateToClientDateTime",
                        DateTimeService.class.getDeclaredMethod("currentDateToClientDateTime"));
            }
            catch (NoSuchMethodException e)
            {
                log.error("There is no method with that name", e);
            }

            for (String spelExpression : spelExpressions)
            {
                for (CorrespondenceMergeField mergeField : getMergeFieldManager().getMergeFields())
                {
                    String mergeFieldId = "${" + mergeField.getFieldId() + "}";
                    if (mergeFieldId.equalsIgnoreCase(spelExpression) && mergeField.getEmailFieldValue() != null)
                    {
                        SpelExpression expression = parser.parseRaw(mergeField.getEmailFieldValue());
                        Object generatedExpression = "";
                        if (expression.getValue(stContext) != null)
                        {
                            try
                            {
                                generatedExpression = expression.getValue(stContext);
                            }
                            catch (RuntimeException e)
                            {
                                log.error("Unable to parse SpEL expression [{}]", spelExpression);
                            }
                            if (expression.getValue(stContext) instanceof String)
                            {
                                generatedExpression = String.valueOf(expression.getValue(stContext)).replace("\n\n", "<br>");
                            }
                            else
                            {
                                generatedExpression = String.valueOf(expression.getValue(stContext));
                            }
                            expressionsToEvaluate.put(mergeField.getFieldId(), (String) generatedExpression);
                        }
                    }
                }
            }
            for (String spelEx : expressionsToEvaluate.keySet())
            {
                String replaceSpelExpression = "${" + spelEx + "}";
                emailBodyTemplate = emailBodyTemplate.replace(replaceSpelExpression, expressionsToEvaluate.get(spelEx));
            }
        }
        return emailBodyTemplate;
    }

    private List<String> getSpelExpressions(String emailBodyTemplate)
    {
        Pattern regex = Pattern.compile("(\\$\\{)(.*?)(\\})");
        Matcher m = regex.matcher(emailBodyTemplate);
        List<String> spelExpressions = new ArrayList<>();

        while (m.find())
        {
            spelExpressions.add(m.group());
        }
        return spelExpressions;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig)
    {
        this.applicationConfig = applicationConfig;
    }

    public ApplicationConfig getApplicationConfig()
    {
        return applicationConfig;
    }

    public CorrespondenceMergeFieldManager getMergeFieldManager()
    {
        return mergeFieldManager;
    }

    public void setMergeFieldManager(CorrespondenceMergeFieldManager mergeFieldManager)
    {
        this.mergeFieldManager = mergeFieldManager;
    }

}
