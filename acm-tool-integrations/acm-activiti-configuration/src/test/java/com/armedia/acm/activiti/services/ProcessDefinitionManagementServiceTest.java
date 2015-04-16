package com.armedia.acm.activiti.services;

import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.activiti.services.dao.AcmProcessDefinitionDao;
import org.activiti.engine.test.Deployment;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-library-test-activiti-process-definition-service.xml")
public class ProcessDefinitionManagementServiceTest extends EasyMockSupport {

    @Autowired
    ProcessDefinitionManagementServiceImpl processDefinitionManagementService;

    private AcmProcessDefinitionDao acmProcessDefinitionDao;
    private String processDefinitionsFolder;
    private Capture<AcmProcessDefinition> capture;

    @Before
    public void setUp() throws Exception {
        acmProcessDefinitionDao = createMock(AcmProcessDefinitionDao.class);
        processDefinitionManagementService.setAcmProcessDefinitionDao(acmProcessDefinitionDao);
        String userHome = System.getProperty("user.home");
        processDefinitionsFolder = userHome + "/.acm/activiti/versions";
        processDefinitionManagementService.setProcessDefinitionsFolder(processDefinitionsFolder);
    }

    @Test
    @Deployment
    public void deployProcessDefinitionTest() throws FileNotFoundException, URISyntaxException {

        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        assertTrue(f.exists());
        capture = new Capture<AcmProcessDefinition>();
        EasyMock.expect(acmProcessDefinitionDao.save(EasyMock.capture(capture))).andAnswer(new IAnswer<AcmProcessDefinition>() {
            public AcmProcessDefinition answer() throws Throwable {
                capture.getValue().setId(1l);
                return capture.getValue();
            }
        });
        acmProcessDefinitionDao.remove(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        EasyMock.replay(acmProcessDefinitionDao);

        AcmProcessDefinition pd = processDefinitionManagementService.deployProcessDefinition(f, false, false);
        assertNotNull(pd);
        assertEquals(1l, pd.getId());
        assertEquals("1", pd.getDeploymentId());
        assertEquals("Testing Activiti Spring Module", pd.getName());
        assertEquals("TestActivitiSpringProcess_v1.bpmn20.xml", pd.getFileName());
        assertEquals(null, pd.getDescription());
        assertEquals(1, pd.getVersion());
        assertEquals("TestActivitiSpringProcess", pd.getKey());

        //check if file is deleted from temp folder
        assertTrue(f.exists());
        //check if file is copied to new location
        assertTrue(new File(processDefinitionsFolder + "/" + pd.getFileName()).exists());

        //cleanup
        processDefinitionManagementService.removeProcessDefinition(pd);
    }

    @Test
    @Deployment
    public void deployExistingProcessDefinitionTest() throws FileNotFoundException, URISyntaxException {

        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        File f1 = new File(getClass().getResource("/activiti/TestActivitiSpringProcessNotChanged.bpmn20.xml").toURI());
        assertTrue(f.exists());
        capture = new Capture<AcmProcessDefinition>();
        EasyMock.expect(acmProcessDefinitionDao.save(EasyMock.capture(capture))).andAnswer(new IAnswer<AcmProcessDefinition>() {
            public AcmProcessDefinition answer() throws Throwable {
                capture.getValue().setId(1l);
                return capture.getValue();
            }
        });
        EasyMock.expect(acmProcessDefinitionDao.getByKeyAndVersion("TestActivitiSpringProcess", 1)).andAnswer(new IAnswer<AcmProcessDefinition>() {
            public AcmProcessDefinition answer() throws Throwable {
                capture.getValue().setId(1l);
                capture.getValue().setDeploymentId("1");
                capture.getValue().setName("Testing Activiti Spring Module");
                capture.getValue().setFileName("TestActivitiSpringProcess_v1.bpmn20.xml");
                capture.getValue().setDescription(null);
                capture.getValue().setVersion(1);
                capture.getValue().setKey("TestActivitiSpringProcess");
                return capture.getValue();
            }
        });

        acmProcessDefinitionDao.remove(EasyMock.anyObject());
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(acmProcessDefinitionDao);

        AcmProcessDefinition pd = processDefinitionManagementService.deployProcessDefinition(f, false, false);
        assertNotNull(pd);
        assertEquals(1l, pd.getId());
        assertEquals("1", pd.getDeploymentId());
        assertEquals("Testing Activiti Spring Module", pd.getName());
        assertEquals("TestActivitiSpringProcess_v1.bpmn20.xml", pd.getFileName());
        assertEquals(null, pd.getDescription());
        assertEquals(1, pd.getVersion());
        assertEquals("TestActivitiSpringProcess", pd.getKey());


        AcmProcessDefinition pd1 = processDefinitionManagementService.deployProcessDefinition(f1, false, false);
        assertNotNull(pd1);
        assertEquals(1l, pd1.getId());
        assertEquals("1", pd1.getDeploymentId());
        assertEquals("Testing Activiti Spring Module", pd1.getName());
        assertEquals("TestActivitiSpringProcess_v1.bpmn20.xml", pd1.getFileName());
        assertEquals(null, pd1.getDescription());
        assertEquals(1, pd1.getVersion());
        assertEquals("TestActivitiSpringProcess", pd1.getKey());


        //check if file is deleted from temp folder
        assertTrue(f.exists());
        //check if file is copied to new location
        assertTrue(new File(processDefinitionsFolder + "/" + pd.getFileName()).exists());


        //cleanup
        processDefinitionManagementService.removeProcessDefinition(pd);
    }


    @Test
    @Deployment
    public void deployNotExistingProcessDefinitionTest() throws FileNotFoundException, URISyntaxException {

        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        File f1 = new File(getClass().getResource("/activiti/TestActivitiSpringProcessChanged.bpmn20.xml").toURI());
        assertTrue(f.exists());
        capture = new Capture<AcmProcessDefinition>();
        EasyMock.expect(acmProcessDefinitionDao.save(EasyMock.capture(capture))).andAnswer(new IAnswer<AcmProcessDefinition>() {
            public AcmProcessDefinition answer() throws Throwable {
                capture.getValue().setId(1l);
                return capture.getValue();
            }
        });
        EasyMock.expect(acmProcessDefinitionDao.save(EasyMock.capture(capture))).andAnswer(new IAnswer<AcmProcessDefinition>() {
            public AcmProcessDefinition answer() throws Throwable {
                capture.getValue().setId(2l);
                return capture.getValue();
            }
        });


        acmProcessDefinitionDao.remove(EasyMock.anyObject());
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(acmProcessDefinitionDao);

        AcmProcessDefinition pd = processDefinitionManagementService.deployProcessDefinition(f, false, false);
        assertNotNull(pd);
        assertEquals(1l, pd.getId());
        assertEquals("1", pd.getDeploymentId());
        assertEquals("Testing Activiti Spring Module", pd.getName());
        assertEquals("TestActivitiSpringProcess_v1.bpmn20.xml", pd.getFileName());
        assertEquals(null, pd.getDescription());
        assertEquals(1, pd.getVersion());
        assertEquals("TestActivitiSpringProcess", pd.getKey());


        AcmProcessDefinition pd1 = processDefinitionManagementService.deployProcessDefinition(f1, false, false);
        assertNotNull(pd1);
        assertEquals(2l, pd1.getId());
        assertEquals("4", pd1.getDeploymentId());
        assertEquals("Testing Activiti Spring Module", pd1.getName());
        assertEquals("TestActivitiSpringProcess_v2.bpmn20.xml", pd1.getFileName());
        assertEquals(null, pd1.getDescription());
        assertEquals(2, pd1.getVersion());
        assertEquals("TestActivitiSpringProcess", pd1.getKey());


        //check if file is deleted from temp folder
        assertTrue(f.exists());
        //check if file is copied to new location
        assertTrue(new File(processDefinitionsFolder + "/" + pd.getFileName()).exists());


        //cleanup
        processDefinitionManagementService.removeProcessDefinition(pd);
        processDefinitionManagementService.removeProcessDefinition(pd1);
    }

    @Test
    public void countProcessDefinitionsEmptyTest() {
        EasyMock.expect(acmProcessDefinitionDao.count()).andReturn(0l);
        EasyMock.replay(acmProcessDefinitionDao);
        assertEquals(0, processDefinitionManagementService.count());
    }

    @Test
    public void countProcessDefinitionsTest() throws URISyntaxException {
        EasyMock.expect(acmProcessDefinitionDao.count()).andReturn(1l);
        EasyMock.replay(acmProcessDefinitionDao);
        assertEquals(1, processDefinitionManagementService.count());
    }
}