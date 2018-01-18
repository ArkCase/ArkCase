package com.armedia.acm.services.users.dao;

import static junit.framework.Assert.assertTrue;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-data-source.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-config-user-service-test-dummy-beans.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-object-converter.xml"
})
@Rollback
public class AcmGroupDaoIT
{

    @Autowired
    private AcmGroupDao groupDao;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Before
    public void setUp()
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional(transactionManager = "transactionManager")
    public void saveGroupTest() throws Exception
    {
        AcmGroup group = new AcmGroup();
        group.setName("B");
        group.setDescription("new group");
        group.setType(AcmGroupType.ADHOC_GROUP);
        group.setStatus(AcmGroupStatus.ACTIVE);

        groupDao.save(group);
    }

    @Test
    @Transactional(transactionManager = "transactionManager")
    public void saveMemberGroupTest() throws Exception
    {
        AcmGroup group = new AcmGroup();
        group.setName("A");
        group.setType(AcmGroupType.ADHOC_GROUP);
        group.setStatus(AcmGroupStatus.ACTIVE);

        AcmGroup memberGroup = new AcmGroup();
        memberGroup.setName("aa");
        group.setType(AcmGroupType.ADHOC_GROUP);
        group.setStatus(AcmGroupStatus.ACTIVE);

        group.addGroupMember(memberGroup);
        group = groupDao.save(group);

        assertTrue(group.getMemberGroups().size() == 1);
    }
}