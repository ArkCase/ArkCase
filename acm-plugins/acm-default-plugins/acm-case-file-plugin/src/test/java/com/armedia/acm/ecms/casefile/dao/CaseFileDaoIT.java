package com.armedia.acm.ecms.casefile.dao;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-case-file.xml",
        "classpath:/spring/spring-library-data-source.xml",
        "classpath:/spring/test-case-file-context.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class CaseFileDaoIT
{
    @Autowired
    private CaseFileDao caseFileDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Before
    public void setUp()
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void saveCaseFile()
    {
        assertNotNull(caseFileDao);
        assertNotNull(entityManager);

        CaseFile caseFile = new CaseFile();
        caseFile.setCaseNumber("caseNumber");
        caseFile.setCaseType("caseType");
        caseFile.setStatus("status");
        caseFile.setTitle("title");

        CaseFile saved = caseFileDao.save(caseFile);

        entityManager.flush();

        assertNotNull(saved.getId());
    }

}
