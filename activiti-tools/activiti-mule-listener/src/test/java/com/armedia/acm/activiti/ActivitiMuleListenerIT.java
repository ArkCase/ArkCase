package com.armedia.acm.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.event.EventHandler;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.module.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/activitiFlow.xml",
        "classpath:/spring/spring-library-activiti-mule-listener.xml",
        "classpath:/spring-library-activiti-test.xml"} )
public class ActivitiMuleListenerIT
{

    @Autowired
    private MuleClient muleClient;

    @Autowired
    private ProcessEngine pe;

    @Autowired
    private RepositoryService repo;

    @Autowired
    private RuntimeService rt;

    @Autowired
    private TaskService ts;

    @Autowired
    private HistoryService hs;

    @Autowired
    private SpringProcessEngineConfiguration springProcessEngineConfiguration;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        // mule
        muleClient.getMuleContext().start();

        // deploy
        repo.createDeployment()
                .addClasspathResource("FinancialReportProcess.bpmn20.xml")
                .deploy();

        List<EventHandler> handlers = springProcessEngineConfiguration.getCustomEventHandlers();

    }

    @After
    public void shutDown() throws Exception
    {
        pe.close();

        muleClient.getMuleContext().stop();
    }

    @Test
    public void startAndCompleteTask() throws Exception
    {
        assertNotNull(rt);

        // start a process
        String pid = rt.startProcessInstanceByKey("financialReport").getId();

        claimFirstTask("accountancy", "fozzie");

        // ozzie gets his task
        completeTask("fozzie");

        // now we should have another task, for a manger to review Fozzie's work
        claimFirstTask("management", "kermit");
        completeTask("kermit");

        // verify process is complete
        HistoricProcessInstance hpi = hs.createHistoricProcessInstanceQuery()
                .processInstanceId(pid).singleResult();
        assertNotNull(hpi.getEndTime());

        log.info("pid " + pid + " ended at " + hpi.getEndTime());

    }

    private void completeTask(String forUser) {
        List<Task> tasks = ts.createTaskQuery().taskAssignee(forUser).list();
        for ( Task t : tasks )
        {
            log.info("Completing task: " + t.getName() + " for user " + forUser);
            ts.complete(t.getId());
        }
    }

    private void claimFirstTask(String group, String forUser) {
        // get & claim task 1
        List<Task> tasks = ts.createTaskQuery().taskCandidateGroup(group).list();
        for ( Task t : tasks )
        {
            log.info("Claiming task: " + t.getName() + " for user " + forUser);
            ts.claim(t.getId(), forUser);
        }
    }
}
