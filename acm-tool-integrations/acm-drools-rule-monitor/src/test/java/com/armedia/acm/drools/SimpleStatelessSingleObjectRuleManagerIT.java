package com.armedia.acm.drools;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by dmiller on 3/24/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-drools-rule-manager-test.xml"
})
public class SimpleStatelessSingleObjectRuleManagerIT
{
    @Autowired
    private StringBuilderRuleManager stringBuilderRuleManager;

    @Test
    public void applyRules() throws Exception
    {
        StringBuilder sb = new StringBuilder("Grateful Dead");

        StringBuilder applied = stringBuilderRuleManager.applyRules(sb);

        assertEquals("Grateful Dead-not empty", applied.toString());
    }
}
