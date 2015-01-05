/**
 * 
 */
package com.armedia.acm.services.users.model.group;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
								   "/spring/spring-library-user-service.xml",
                                   "/spring/spring-library-context-holder.xml"
                                   })
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class AcmGroupIT {
	
	@Autowired
	private AcmGroupDao groupDao;
	
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
		
		AcmGroup savedGroup = getGroupDao().save(group);
		getEntityManager().flush();
		
		
		// Subgroup
		AcmGroup subGroup = new AcmGroup();
		
		subGroup.setName("SubGroup Name");
		subGroup.setDescription("SubGroup Description");
		subGroup.setType("SubGroup Type");
		subGroup.setStatus("SubGroup Status");
		subGroup.setParentGroup(savedGroup);
		
		AcmGroup savedSubGroup = getGroupDao().save(subGroup);
		getEntityManager().flush();
		
		
		AcmGroup parent = getGroupDao().findByName(savedGroup.getName());
		assertEquals(savedGroup.getName(), savedSubGroup.getParentGroup().getName());
		
		
		getGroupDao().deleteAcmGroupByName(savedSubGroup.getName());
		getEntityManager().flush();
		
		getGroupDao().deleteAcmGroupByName(savedGroup.getName());
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
		
		AcmGroup savedGroup = getGroupDao().save(group);
		getEntityManager().flush();
		
		
		// Subgroup
		AcmGroup subGroup = new AcmGroup();
		
		subGroup.setName("SubGroup Name");
		subGroup.setDescription("SubGroup Description");
		subGroup.setType("SubGroup Type");
		subGroup.setStatus("SubGroup Status");
		subGroup.setParentGroup(savedGroup);
		
		getGroupDao().save(subGroup);
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
		
		AcmGroup savedGroup = getGroupDao().save(group);
		getEntityManager().flush();
		
		
		// Subgroup
		AcmGroup subGroup = new AcmGroup();
		
		subGroup.setName("SubGroup Name");
		subGroup.setDescription("SubGroup Description");
		subGroup.setType("SubGroup Type");
		subGroup.setStatus("SubGroup Status");
		subGroup.setParentGroup(savedGroup);
		
		AcmGroup savedSubGroup = getGroupDao().save(subGroup);
		getEntityManager().flush();		
		
		getGroupDao().deleteAcmGroupByName(savedSubGroup.getName());
		getEntityManager().flush();
		
		AcmGroup parent = getGroupDao().findByName(savedGroup.getName());
		assertEquals(0, parent.getChildGroups().size());
		
		getGroupDao().deleteAcmGroupByName(savedGroup.getName());
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
		
		AcmGroup savedGroup = getGroupDao().save(group);
		getEntityManager().flush();
		
		
		// Subgroup
		AcmGroup subGroup = new AcmGroup();
		
		subGroup.setName("SubGroup Name");
		subGroup.setDescription("SubGroup Description");
		subGroup.setType("SubGroup Type");
		subGroup.setStatus("SubGroup Status");
		subGroup.setParentGroup(savedGroup);
		subGroup.setSupervisor(savedGroup.getSupervisor());
		
		AcmGroup savedSubGroup = getGroupDao().save(subGroup);
		getEntityManager().flush();
		
		
		
		assertNotNull(savedGroup.getName());
		assertNotNull(savedSubGroup.getName());
		assertEquals(savedGroup.getSupervisor().getUserId(), savedSubGroup.getSupervisor().getUserId());
		
		
		
		getGroupDao().deleteAcmGroupByName(savedSubGroup.getName());
		getEntityManager().flush();
		
		getGroupDao().deleteAcmGroupByName(savedGroup.getName());
		getEntityManager().flush();
    }

	public AcmGroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(AcmGroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
