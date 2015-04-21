package com.armedia.acm.activiti.services;

import com.armedia.acm.activiti.exceptions.AcmBpmnException;
import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.activiti.services.dao.AcmBpmnDao;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.apache.commons.codec.digest.DigestUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-library-test-activiti-process-definition-service.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AcmBpmnServiceTest extends EasyMockSupport {

    @Autowired
    AcmBpmnServiceImpl processDefinitionManagementService;

    private AcmBpmnDao acmBpmnDao;
    private String processDefinitionsFolder;
    private Capture<AcmProcessDefinition> capture;
    private ProcessEngine processEngine;
    private Resource resourceFile;
    private Resource resourceFileNotChanged;
    private Resource resourceFileChanged;
    private Set<String> filesToDelete = null;
    private String resourceFileMD5Sum;
    private String resourceFileNotChangedMD5Sum;
    private String resourceFileChangedMD5Sum;

    @BeforeClass
    public static void initialCleanUp() {
        String userHome = System.getProperty("user.home");
        File versionsFolder = new File(userHome + "/.acm/activiti/versions");
        if(versionsFolder.exists()) {
            for (File f : versionsFolder.listFiles()) {
                if (f.isFile() && f.getName().startsWith("Test"))
                    f.delete();
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        filesToDelete = new HashSet<>();
        resourceFile = new ClassPathResource("/activiti/TestActivitiSpringProcess.bpmn20.xml");
        resourceFileMD5Sum  = getDigest(resourceFile.getFile());
        resourceFileNotChanged = new ClassPathResource("/activiti/TestActivitiSpringProcessNotChanged.bpmn20.xml");
        resourceFileNotChangedMD5Sum  = getDigest(resourceFileNotChanged.getFile());
        resourceFileChanged = new ClassPathResource("/activiti/TestActivitiSpringProcessChanged.bpmn20.xml");
        resourceFileChangedMD5Sum  = getDigest(resourceFileChanged.getFile());
        processEngine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration().buildProcessEngine();
        processDefinitionManagementService.setActivitiRepositoryService(processEngine.getRepositoryService());
        acmBpmnDao = createMock(AcmBpmnDao.class);
        processDefinitionManagementService.setAcmBpmnDao(acmBpmnDao);
        String userHome = System.getProperty("user.home");
        processDefinitionsFolder = userHome + "/.acm/activiti/versions";
        processDefinitionManagementService.setProcessDefinitionsFolder(processDefinitionsFolder);
    }

    @After
    public void destroy() {
        processEngine.close();
    }

    @Test
    public void deployProcessDefinitionTest() throws IOException, URISyntaxException {
        File f = resourceFile.getFile();
        assertTrue(f.exists());
        EasyMock.expect(acmBpmnDao.getByKeyAndDigest("TestActivitiSpringProcessUnitTest", resourceFileMD5Sum)).andReturn(null);
        capture = new Capture<AcmProcessDefinition>();
        EasyMock.expect(acmBpmnDao.save(EasyMock.capture(capture))).andAnswer(new IAnswer<AcmProcessDefinition>() {
            public AcmProcessDefinition answer() throws Throwable {
                capture.getValue().setId(1l);
                return capture.getValue();
            }
        });

        acmBpmnDao.remove(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        EasyMock.replay(acmBpmnDao);

        AcmProcessDefinition pd = processDefinitionManagementService.deploy(f, false, false);
        filesToDelete.add(pd.getFileName());
        assertNotNull(pd);
        assertEquals(Long.valueOf(1l), pd.getId());
        assertEquals("1", pd.getDeploymentId());
        assertEquals("Testing Activiti Spring Module", pd.getName());
        assertEquals("TestActivitiSpringProcessUnitTest_v1.bpmn20.xml", pd.getFileName());
        assertEquals(null, pd.getDescription());
        assertEquals(1, pd.getVersion());
        assertEquals("TestActivitiSpringProcessUnitTest", pd.getKey());

        //check if file is deleted from temp folder
        assertTrue(f.exists());
        //check if file is copied to new location
        assertTrue(new File(processDefinitionsFolder + "/" + pd.getFileName()).exists());

        //cleanup
        processDefinitionManagementService.remove(pd, true);
    }

    @Test
    public void deployExistingProcessDefinitionTest() throws IOException, URISyntaxException {
        File f = resourceFile.getFile();
        File f1 = resourceFileNotChanged.getFile();

        assertTrue(f.exists());
        EasyMock.expect(acmBpmnDao.getByKeyAndDigest("TestActivitiSpringProcessUnitTest", resourceFileMD5Sum)).andReturn(null);
        AcmProcessDefinition fromDBExisting = new AcmProcessDefinition();
        fromDBExisting.setId(1l);
        fromDBExisting.setDeploymentId("1");
        fromDBExisting.setName("Testing Activiti Spring Module");
        fromDBExisting.setFileName("TestActivitiSpringProcessUnitTest_v1.bpmn20.xml");
        fromDBExisting.setDescription(null);
        fromDBExisting.setVersion(1);
        fromDBExisting.setMd5Hash("ecf918b65e9ad2b6aaf51166aa3cac9a");
        fromDBExisting.setKey("TestActivitiSpringProcessUnitTest");
        EasyMock.expect(acmBpmnDao.getByKeyAndDigest("TestActivitiSpringProcessUnitTest", resourceFileNotChangedMD5Sum)).andReturn(fromDBExisting);
        capture = new Capture<AcmProcessDefinition>();
        EasyMock.expect(acmBpmnDao.save(EasyMock.capture(capture))).andAnswer(new IAnswer<AcmProcessDefinition>() {
            public AcmProcessDefinition answer() throws Throwable {
                capture.getValue().setId(1l);
                return capture.getValue();
            }
        });

        acmBpmnDao.remove(EasyMock.anyObject());
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(acmBpmnDao);

        AcmProcessDefinition pd = processDefinitionManagementService.deploy(f, false, false);
        filesToDelete.add(pd.getFileName());
        assertNotNull(pd);
        assertEquals(Long.valueOf(1l), pd.getId());
        assertEquals("1", pd.getDeploymentId());
        assertEquals("Testing Activiti Spring Module", pd.getName());
        assertEquals("TestActivitiSpringProcessUnitTest_v1.bpmn20.xml", pd.getFileName());
        assertEquals(null, pd.getDescription());
        assertEquals(1, pd.getVersion());
        assertEquals("TestActivitiSpringProcessUnitTest", pd.getKey());


        AcmProcessDefinition pd1 = processDefinitionManagementService.deploy(f1, false, false);
        filesToDelete.add(pd1.getFileName());
        assertNotNull(pd1);
        assertEquals(Long.valueOf(1l), pd1.getId());
        assertEquals("1", pd1.getDeploymentId());
        assertEquals("Testing Activiti Spring Module", pd1.getName());
        assertEquals("TestActivitiSpringProcessUnitTest_v1.bpmn20.xml", pd1.getFileName());
        assertEquals(null, pd1.getDescription());
        assertEquals(1, pd1.getVersion());
        assertEquals("TestActivitiSpringProcessUnitTest", pd1.getKey());


        //check if file is deleted from temp folder
        assertTrue(f.exists());
        //check if file is copied to new location
        assertTrue(new File(processDefinitionsFolder + "/" + pd.getFileName()).exists());


        //cleanup
        processDefinitionManagementService.remove(pd, true);
    }

    @Test
    public void deployNotExistingProcessDefinitionTest() throws IOException, URISyntaxException {
        File f = resourceFile.getFile();
        File f1 = resourceFileChanged.getFile();

        assertTrue(f.exists());
        capture = new Capture<AcmProcessDefinition>();
        EasyMock.expect(acmBpmnDao.getByKeyAndDigest("TestActivitiSpringProcessUnitTest", resourceFileMD5Sum)).andReturn(null);
        EasyMock.expect(acmBpmnDao.getByKeyAndDigest("TestActivitiSpringProcessUnitTest", resourceFileChangedMD5Sum)).andReturn(null);
        EasyMock.expect(acmBpmnDao.save(EasyMock.capture(capture))).andAnswer(new IAnswer<AcmProcessDefinition>() {
            public AcmProcessDefinition answer() throws Throwable {
                capture.getValue().setId(1l);
                return capture.getValue();
            }
        });
        EasyMock.expect(acmBpmnDao.save(EasyMock.capture(capture))).andAnswer(new IAnswer<AcmProcessDefinition>() {
            public AcmProcessDefinition answer() throws Throwable {
                capture.getValue().setId(2l);
                return capture.getValue();
            }
        });


        acmBpmnDao.remove(EasyMock.anyObject());
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(acmBpmnDao);

        AcmProcessDefinition pd = processDefinitionManagementService.deploy(f, false, false);
        filesToDelete.add(pd.getFileName());
        assertNotNull(pd);
        assertEquals(Long.valueOf(1l), pd.getId());
        assertEquals("1", pd.getDeploymentId());
        assertEquals("Testing Activiti Spring Module", pd.getName());
        assertEquals("TestActivitiSpringProcessUnitTest_v1.bpmn20.xml", pd.getFileName());
        assertEquals(null, pd.getDescription());
        assertEquals(1, pd.getVersion());
        assertEquals("TestActivitiSpringProcessUnitTest", pd.getKey());


        AcmProcessDefinition pd1 = processDefinitionManagementService.deploy(f1, false, false);
        filesToDelete.add(pd1.getFileName());
        assertNotNull(pd1);
        assertEquals(Long.valueOf(2l), pd1.getId());
        assertEquals("4", pd1.getDeploymentId());
        assertEquals("Testing Activiti Spring Module", pd1.getName());
        assertEquals("TestActivitiSpringProcessUnitTest_v2.bpmn20.xml", pd1.getFileName());
        assertEquals(null, pd1.getDescription());
        assertEquals(2, pd1.getVersion());
        assertEquals("TestActivitiSpringProcessUnitTest", pd1.getKey());


        //check if file is deleted from temp folder
        assertTrue(f.exists());
        //check if file is copied to new location
        assertTrue(new File(processDefinitionsFolder + "/" + pd.getFileName()).exists());


        //cleanup
        processDefinitionManagementService.remove(pd, true);
        processDefinitionManagementService.remove(pd1, true);
    }

    @Test
    public void countProcessDefinitionsEmptyTest() {
        EasyMock.expect(acmBpmnDao.count()).andReturn(0l);
        EasyMock.replay(acmBpmnDao);
        assertEquals(0, processDefinitionManagementService.count());
    }

    @Test
    public void countProcessDefinitionsTest() throws URISyntaxException {
        EasyMock.expect(acmBpmnDao.count()).andReturn(1l);
        EasyMock.replay(acmBpmnDao);
        assertEquals(1, processDefinitionManagementService.count());
    }

    @After
    public void cleanUp() {
        //in case of failed test or exception, database will rollback, and files and deployments are cleaned manually
        String userHome = System.getProperty("user.home");
        String processDefinitionsFolder = userHome + "/.acm/activiti/versions";
        //delete created files
        for (String file : filesToDelete) {
            File toBeDeleted = new File(processDefinitionsFolder + "/" + file);
            if (toBeDeleted.exists())
                toBeDeleted.delete();
        }
    }

    public String getDigest(File processDefinitionFile) {
        try {
            FileInputStream stream = new FileInputStream(processDefinitionFile);

            String md5Hex = DigestUtils.md5Hex(stream);
            closeStream(stream);
            return md5Hex;
        } catch (IOException e) {
            throw new AcmBpmnException("Error performing file digest!", e);
        }
    }
    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {

            }
        }
    }
}