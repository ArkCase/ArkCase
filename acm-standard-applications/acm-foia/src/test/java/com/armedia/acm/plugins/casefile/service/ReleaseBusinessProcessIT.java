package com.armedia.acm.plugins.casefile.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.armedia.acm.objectchangestatus.service.ChangeObjectStatusService;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import gov.foia.broker.FOIARequestFileBrokerClient;
import gov.foia.model.FOIARequest;
import gov.foia.service.ResponseFolderCompressorService;
import gov.foia.service.ResponseFolderNotifyService;

/**
 * Created by dmiller on 8/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-foia-activiti-test.xml" })
public class ReleaseBusinessProcessIT
{
    private final String processName = "foia-extension-release-process";
    @Autowired
    private ProcessEngine pe;
    @Autowired
    private RepositoryService repo;
    @Autowired
    private RuntimeService rt;
    @Autowired
    @Qualifier("changeObjectStatusService")
    private ChangeObjectStatusService changeObjectStatusService;

    @Autowired
    @Qualifier("queueCaseService")
    private QueueCaseService queueCaseService;

    @Autowired
    @Qualifier("responseFolderCompressorService")
    private ResponseFolderCompressorService responseFolderCompressorService;

    @Autowired
    @Qualifier("foiaRequestFileBrokerClient")
    private FOIARequestFileBrokerClient foiaRequestFileBrokerClient;

    @Autowired
    @Qualifier("responseFolderNotifyService")
    private ResponseFolderNotifyService responseFolderNotifyService;

    @Before
    public void setUp() throws Exception
    {
        // deploy
        repo.createDeployment().addClasspathResource("activiti/foia-extension-release-process_v7.bpmn20.xml").deploy();
    }

    @After
    public void shutDown() throws Exception
    {
        pe.close();
    }

    @Test
    public void release() throws Exception
    {
        Long foiaId = 500L;
        String objectType = CaseFileConstants.OBJECT_TYPE;

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_TYPE", objectType);
        processVariables.put("OBJECT_ID", foiaId);

        changeObjectStatusService.change(foiaId, objectType, "Released");
        expect(queueCaseService.enqueue(foiaId, "Release")).andReturn(new FOIARequest());
        expect(responseFolderCompressorService.compressResponseFolder(foiaId)).andReturn("temp-file-name.zip");
        foiaRequestFileBrokerClient.sendReleaseFile(foiaId);
        responseFolderNotifyService.sendEmailNotification(foiaId);

        replay(changeObjectStatusService, queueCaseService, responseFolderCompressorService, foiaRequestFileBrokerClient,
                responseFolderNotifyService);

        rt.startProcessInstanceByKey(processName, processVariables);

        verify(changeObjectStatusService, queueCaseService, responseFolderCompressorService, foiaRequestFileBrokerClient,
                responseFolderNotifyService);
    }
}
