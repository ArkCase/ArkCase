package com.armedia.acm.services.notification.service;

import static org.junit.Assert.fail;

import com.armedia.acm.core.AcmApplication;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import freemarker.template.TemplateException;

public class TemplatingEngineTest
{
    private TemplatingEngine templatingEngine;

    @Before
    public void setup()
    {
        templatingEngine = new TemplatingEngine();

        AcmApplication acmApplication = new AcmApplication();
        acmApplication.setBaseUrl("https://test.com/arkcase");

        templatingEngine.setAcmApplication(acmApplication);
    }

    public class Model
    {
        private Date date;
        private LocalDateTime dateTime;
        private String nullField;
        private Date nullDate;
        private LocalDateTime nullDateTime;
        private Long id;
        private boolean test;

        public Date getDate()
        {
            return date;
        }

        public void setDate(Date date)
        {
            this.date = date;
        }

        public LocalDateTime getDateTime()
        {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime)
        {
            this.dateTime = dateTime;
        }

        public String getString()
        {
            return "Some String";
        }

        public String getNull()
        {
            return null;
        }

        public String getNullField()
        {
            return nullField;
        }

        public void setNullField(String nullField)
        {
            this.nullField = nullField;
        }

        public LocalDateTime getNullDateTime()
        {
            return nullDateTime;
        }

        public void setNullDateTime(LocalDateTime nullDateTime)
        {
            this.nullDateTime = nullDateTime;
        }

        public Date getNullDate()
        {
            return nullDate;
        }

        public void setNullDate(Date nullDate)
        {
            this.nullDate = nullDate;
        }

        public boolean isTest()
        {
            return test;
        }

        public void setTest(boolean test)
        {
            this.test = test;
        }

        public Long getId()
        {
            return id;
        }

        public void setId(Long id)
        {
            this.id = id;
        }
    }

    @Test
    public void testTemplate()
    {
        Model model = new Model();
        model.setDate(new Date());
        model.setDateTime(LocalDateTime.now());
        model.setId(1111l);

        String template = "dateAsDateTime: ${model.date?datetime}\n" +
                "dateAsDate: ${model.date?date}\n" +
                "localDateTime: ${formatDateTime(model.dateTime,'MM/dd/yyyy HH:MM')}\n" +
                "nullDate: ${model.nullDate?has_content?then(model.nullDate?datetime,'')}\n" +
                "default nullDate: ${model.nullDate?has_content?then(model.nullDate?datetime,'not set')}\n" +
                "nullDateTime: ${formatDateTime(model.nullDateTime,'MM/dd/yyyy HH:MM')}\n" +
                "stringFromMethod: ${model.getString()}\n" +
                "nullString: ${model.getNull()}\n" +
                "nullField: ${model.nullField}\n" +
                "number: ${model.id}\n" +
                "not formatted number: ${model.id?c}\n" +
                "test: <#if model.test>Fail<#else>Success</#if>" +
                "baseURL: ${baseURL}";

        String result = null;
        try
        {
            result = templatingEngine.process(template, "model", model);
        }
        catch (TemplateException | IOException e)
        {
            fail(e.getMessage());
        }

        System.out.println(result);
    }
}
