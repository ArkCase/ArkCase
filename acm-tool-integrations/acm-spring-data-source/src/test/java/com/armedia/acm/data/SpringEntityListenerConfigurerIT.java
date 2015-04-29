package com.armedia.acm.data;

import com.armedia.acm.spring.SpringContextHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source-test.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml"
})
public class SpringEntityListenerConfigurerIT
{
    @Autowired
    private TestInsertListener testInsertListener;

    @Autowired
    private TestUpdateListener testUpdateListener;

    @Autowired
    private TestDeleteListener testDeleteListener;

    @Autowired
    private SpringEntityListenerConfigurer springEntityListenerConfigurer;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SpringContextHolder springContextHolder;

    @Before
    public void setUp() throws Exception
    {
        springEntityListenerConfigurer.setSpringContextHolder(springContextHolder);
    }

    @Test
    public void findListenersOnApplicationContextInjection()
    {
        assertNotNull(applicationContext);

        assertNotNull(springEntityListenerConfigurer.getSpringContextHolder());

        assertEquals(1, springEntityListenerConfigurer.findBeforeInsertListeners().size());
        assertEquals(testInsertListener, springEntityListenerConfigurer.findBeforeInsertListeners().iterator().next());

        assertEquals(1, springEntityListenerConfigurer.findBeforeUpdateListeners().size());
        assertEquals(testUpdateListener, springEntityListenerConfigurer.findBeforeUpdateListeners().iterator().next());

        assertEquals(1, springEntityListenerConfigurer.findBeforeDeleteListeners().size());
        assertEquals(testDeleteListener, springEntityListenerConfigurer.findBeforeDeleteListeners().iterator().next());
    }

    @Test
    public void generateAdaptersForListeners()
    {
        assertEquals(3, springEntityListenerConfigurer.generateAdaptersForListeners().size());
    }


}
