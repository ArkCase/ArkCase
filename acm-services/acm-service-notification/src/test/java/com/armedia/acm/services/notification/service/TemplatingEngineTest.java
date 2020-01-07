
package com.armedia.acm.services.notification.service;

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

import static org.junit.Assert.fail;

import com.armedia.acm.core.ApplicationConfig;
import com.armedia.acm.services.email.service.TemplatingEngine;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import freemarker.template.TemplateException;

public class TemplatingEngineTest
{
    private TemplatingEngine templatingEngine;

    @Before
    public void setup()
    {
        templatingEngine = new TemplatingEngine();

        ApplicationConfig appConfig = new ApplicationConfig();
        appConfig.setBaseUrl("https://test.com/arkcase");

        templatingEngine.setApplicationConfig(appConfig);
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
        private List<String> stringList;

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

        public List<String> getStringList()
        {
            return stringList;
        }

        public void setStringList(List<String> stringList)
        {
            this.stringList = stringList;
        }
    }

    @Test
    public void testTemplate()
    {
        Model model = new Model();
        model.setDate(new Date());
        model.setDateTime(LocalDateTime.now());
        model.setId(1111l);
        model.setStringList(Arrays.asList("string1", "string2"));

        String template = "dateAsDateTime: ${model.date?datetime}\n" +
                "dateAsDate: ${model.date?date}\n" +
                "localDateTime: ${formatDateTime(model.dateTime,'MM/dd/yyyy HH:mm')}\n" +
                "nullDate: ${model.nullDate?has_content?then(model.nullDate?datetime,'')}\n" +
                "default nullDate: ${model.nullDate?has_content?then(model.nullDate?datetime,'not set')}\n" +
                "nullDateTime: ${formatDateTime(model.nullDateTime,'MM/dd/yyyy HH:MM')}\n" +
                "stringFromMethod: ${model.getString()}\n" +
                "nullString: ${model.getNull()}\n" +
                "nullField: ${model.nullField}\n" +
                "number: ${model.id}\n" +
                "not formatted number: ${model.id?c}\n" +
                "test: <#if model.test>Fail<#else>Success</#if>\n" +
                "stringList: ${model.stringList?join(\", \")}\n" +
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
