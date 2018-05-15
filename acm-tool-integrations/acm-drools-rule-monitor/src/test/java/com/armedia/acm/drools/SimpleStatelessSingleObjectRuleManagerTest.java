package com.armedia.acm.drools;

/*-
 * #%L
 * Tool Integrations: Drools Business Rule Monitor
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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Created by dmiller on 3/24/2017.
 */
public class SimpleStatelessSingleObjectRuleManagerTest
{
    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    private StringBuilderRuleManager unit;

    @Before
    public void setUp() throws Exception
    {
        unit = new StringBuilderRuleManager();
    }

    @Test
    public void afterPropertiesSet() throws Exception
    {
        Resource ruleFolder = new ClassPathResource("/rules");
        String ruleFolderPath = ruleFolder.getFile().getCanonicalPath();

        unit.setRuleFileLocation(ruleFolderPath);
        unit.setRuleSpreadsheetFilename("drools-form-string-builder-rules.xlsx");

        StringBuilder stringBuilder = new StringBuilder();
        unit.afterPropertiesSet();
        unit.applyRules(stringBuilder);

        assertEquals("empty", stringBuilder.toString());
    }
}
