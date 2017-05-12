package com.armedia.acm.drools;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.junit.Assert.assertEquals;

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
