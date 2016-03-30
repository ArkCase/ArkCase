/**
 *
 */
package com.armedia.acm.services.users.model.group;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.group.GroupService;
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

import static org.junit.Assert.*;

/**
 * @author riste.tutureski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-config-user-service-test-dummy-beans.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class AcmGroupIT
{

    @Autowired
    private AcmGroupDao groupDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Autowired
    private GroupService groupService;

    @Before
    public void setUp()
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void saveGroup() throws Exception
    {
        AcmGroup group = new AcmGroup();

        group.setName("Group Name");
        group.setDescription("Group Description");
        group.setType("Group Type");
        group.setStatus("Group Status");

        AcmGroup saved = getGroupDao().save(group);
        getEntityManager().flush();


        AcmGroup fromDatabase = getGroupDao().findByName(saved.getName());
        assertNotNull(fromDatabase);


        getGroupDao().deleteAcmGroupByName(fromDatabase.getName());
        getEntityManager().flush();
    }

    @Test
    @Transactional
    public void saveSubGroup() throws Exception
    {
        // Parent group
        AcmGroup group = new AcmGroup();

        group.setName("Group Name");
        group.setDescription("Group Description");
        group.setType("Group Type");
        group.setStatus("Group Status");


        // Subgroup
        AcmGroup subGroup = new AcmGroup();

        subGroup.setName("SubGroup Name");
        subGroup.setDescription("SubGroup Description");
        subGroup.setType("SubGroup Type");
        subGroup.setStatus("SubGroup Status");
        subGroup.setParentGroup(group);

        AcmGroup savedGroup = getGroupDao().save(subGroup);
        getEntityManager().flush();


        AcmGroup parent = getGroupDao().findByName(savedGroup.getParentGroup().getName());
        assertEquals(parent.getName(), savedGroup.getParentGroup().getName());


        getGroupDao().deleteAcmGroupByName(savedGroup.getName());
        getEntityManager().flush();

        getGroupDao().deleteAcmGroupByName(savedGroup.getParentGroup().getName());
        getEntityManager().flush();
    }

    @Test
    @Transactional
    public void removeGroup() throws Exception
    {
        AcmGroup group = new AcmGroup();

        group.setName("Group Name");
        group.setDescription("Group Description");
        group.setType("Group Type");
        group.setStatus("Group Status");

        AcmGroup saved = getGroupDao().save(group);
        getEntityManager().flush();


        AcmGroup fromDatabase = getGroupDao().findByName(saved.getName());
        assertNotNull(fromDatabase);


        getGroupDao().deleteAcmGroupByName(fromDatabase.getName());
        getEntityManager().flush();

        fromDatabase = getGroupDao().findByName(saved.getName());
        assertNull(fromDatabase);
    }

    @Test
    @Transactional
    public void removeParentGroup() throws Exception
    {
        // Parent group
        AcmGroup group = new AcmGroup();

        group.setName("Group Name");
        group.setDescription("Group Description");
        group.setType("Group Type");
        group.setStatus("Group Status");

        // Subgroup
        AcmGroup subGroup = new AcmGroup();

        subGroup.setName("SubGroup Name");
        subGroup.setDescription("SubGroup Description");
        subGroup.setType("SubGroup Type");
        subGroup.setStatus("SubGroup Status");
        subGroup.setParentGroup(group);

        AcmGroup savedGroup = getGroupDao().save(subGroup);
        getEntityManager().flush();


        AcmGroup parent = getGroupDao().findByName(savedGroup.getName());
        getEntityManager().flush();
        assertNotNull(parent);

        getGroupDao().deleteAcmGroupByName(parent.getName());
        getEntityManager().flush();

        parent = getGroupDao().findByName(savedGroup.getName());
        assertNull(parent);
    }

    @Test
    @Transactional
    public void removeSubGroup() throws Exception
    {
        // Parent group
        AcmGroup group = new AcmGroup();

        group.setName("Group Name");
        group.setDescription("Group Description");
        group.setType("Group Type");
        group.setStatus("Group Status");


        // Subgroup
        AcmGroup subGroup = new AcmGroup();

        subGroup.setName("SubGroup Name");
        subGroup.setDescription("SubGroup Description");
        subGroup.setType("SubGroup Type");
        subGroup.setStatus("SubGroup Status");
        subGroup.setParentGroup(group);

        getGroupDao().save(group);
        getEntityManager().flush();

        getGroupDao().deleteAcmGroupByName(subGroup.getName());
        getEntityManager().flush();

        AcmGroup parent = getGroupDao().findByName(group.getName());
        assertNotNull(parent);

        AcmGroup child = getGroupDao().findByName(subGroup.getName());
        assertNull(child);

        getGroupDao().deleteAcmGroupByName(group.getName());
        getEntityManager().flush();
    }

    @Test
    @Transactional
    public void setSupervisorInSubGroupFromParent() throws Exception
    {
        // Parent group
        AcmGroup group = new AcmGroup();

        group.setName("Group Name");
        group.setDescription("Group Description");
        group.setType("Group Type");
        group.setStatus("Group Status");

        AcmUser supervisor = new AcmUser();
        supervisor.setUserId("test-user");
        supervisor.setUserDirectoryName("Test Directory Name");
        supervisor.setUserState("TEST");
        supervisor.setFirstName("First Name");
        supervisor.setLastName("Last Name");

        group.setSupervisor(supervisor);


        // Subgroup
        AcmGroup subGroup = new AcmGroup();

        subGroup.setName("SubGroup Name");
        subGroup.setDescription("SubGroup Description");
        subGroup.setType("SubGroup Type");
        subGroup.setStatus("SubGroup Status");
        subGroup.setParentGroup(group);
        subGroup.setSupervisor(group.getSupervisor());

        AcmGroup savedGroup = getGroupDao().save(group);
        getEntityManager().flush();


        assertNotNull(savedGroup.getName());
        assertNotNull(savedGroup.getChildGroups().get(0).getName());
        assertEquals(savedGroup.getSupervisor().getUserId(), savedGroup.getChildGroups().get(0).getSupervisor().getUserId());


        getGroupDao().deleteAcmGroupByName(subGroup.getName());
        getEntityManager().flush();

        getGroupDao().deleteAcmGroupByName(group.getName());
        getEntityManager().flush();
    }

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

}