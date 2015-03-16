package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-mule-context-manager.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-cmis-configuration.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-activiti-actions.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-event.xml"})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class EcmFileTransactionIT
{

    @Autowired
    private EcmFileTransaction unit;

    @Autowired
    private MuleClient muleClient;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private String testFolderId;

    private Logger log = LoggerFactory.getLogger(getClass());

    private Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "testUser");

    @Before
    public void setUp() throws Exception
    {
        String testPath = "/acm/test/folder";
        MuleMessage message = muleClient.send("vm://getTestFolderId.in", testPath, null);
        String folderId = message.getPayloadAsString();

        testFolderId = folderId;

        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void muleAddAndRetrieveFile() throws Exception
    {
        assertNotNull(testFolderId);
        assertNotNull(entityManager);

        log.debug("Found folder id '" + testFolderId + "'");

        Resource uploadFile = new ClassPathResource("/log4j.properties");
        InputStream is = uploadFile.getInputStream();

        EcmFile ecmFile = new EcmFile();

        DateFormat solrDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String created = solrDateFormat.format(new Date());
        Date date = solrDateFormat.parse(created);


        ecmFile.setFileName("log4j.properties-" + System.currentTimeMillis());
        ecmFile.setFileMimeType("text/plain");

        ecmFile.setCreator("ANN-acm");
        ecmFile.setModifier("MARJAN-ACM");
        ecmFile.setCreated(date);
        ecmFile.setModified(date);

        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("cmisFolderId", testFolderId);
        messageProperties.put("inputStream", is);
        messageProperties.put("acmUser", auth);
        messageProperties.put("auditAdapter", auditAdapter);

        MuleMessage message = muleClient.send("vm://addFile.in", ecmFile, messageProperties);

        assertNotNull(message);

        EcmFile found = message.getPayload(EcmFile.class);

        entityManager.flush();

        assertNotNull(found.getEcmFileId());
        assertNotNull(found.getCreator());

        log.debug("upload file id '" + found.getEcmFileId() + "'");

        EcmFile persisted = entityManager.find(EcmFile.class, found.getFileId());
        assertNotNull(persisted);
        entityManager.refresh(persisted);

        MuleMessage downloadedFile = muleClient.send("vm://downloadFileFlow.in", ecmFile.getEcmFileId(), null);
        ContentStream filePayload = (ContentStream) downloadedFile.getPayload();

        assertNotNull(filePayload);

        log.debug("Download file: " + filePayload.getFileName() + "; " + filePayload.getMimeType() + "; " + filePayload.getBigLength());

    }






}
