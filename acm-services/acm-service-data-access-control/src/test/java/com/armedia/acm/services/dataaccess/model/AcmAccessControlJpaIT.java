package com.armedia.acm.services.dataaccess.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml"
})
@TransactionConfiguration(defaultRollback = false)
public class AcmAccessControlJpaIT
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    private String accessLevel = "TEST_ACCESS_LEVEL";
    private String accessorType = "TEST_ACCESSOR_TYPE";
    private String objectType = "TEST OBJECT TYPE";
    private String objectState = "TEST OBJECT STATE";
    private Long objectId = 500L;
    private String accessorId = "TEST ACCESSOR ID";

    @Before
    public void setUp() throws Exception
    {
        String deleteDefault = "DELETE FROM AcmAccessControlDefault a " +
                "WHERE a.accessLevel = :accessLevel " +
                "AND a.accessorType = :accessorType " +
                "AND a.objectType = :objectType " +
                "AND a.objectState = :objectState";
        Query deleteDefaultQuery = entityManager.createQuery(deleteDefault);
        deleteDefaultQuery.setParameter("accessLevel", accessLevel);
        deleteDefaultQuery.setParameter("accessorType", accessorType);
        deleteDefaultQuery.setParameter("objectState", objectState);
        deleteDefaultQuery.setParameter("objectType", objectType);

        deleteDefaultQuery.executeUpdate();

        String deleteEntry = "DELETE FROM AcmAccessControlEntry a " +
                "WHERE a.accessLevel = :accessLevel " +
                "AND a.accessorId = :accessorId " +
                "AND a.objectType = :objectType " +
                "AND a.objectState = :objectState " +
                "AND a.objectId = :objectId";
        Query deleteEntryQuery = entityManager.createQuery(deleteEntry);
        deleteEntryQuery.setParameter("accessLevel", accessLevel);
        deleteEntryQuery.setParameter("accessorId", accessorId);
        deleteEntryQuery.setParameter("objectState", objectState);
        deleteEntryQuery.setParameter("objectType", objectType);
        deleteEntryQuery.setParameter("objectId", objectId);

        deleteEntryQuery.executeUpdate();
    }

    @Test
    @Transactional
    public void storeDefaultAccessControl()
    {
        AcmAccessControlDefault accessControlDefault = new AcmAccessControlDefault();
        accessControlDefault.setAccessDecision("GRANT");
        accessControlDefault.setAccessLevel(accessLevel);
        accessControlDefault.setAccessorType(accessorType);
        accessControlDefault.setAllowDiscretionaryUpdate(true);
        accessControlDefault.setCreator("creator");
        accessControlDefault.setModifier("modifier");
        accessControlDefault.setObjectState(objectState);
        accessControlDefault.setObjectType(objectType);

        entityManager.persist(accessControlDefault);

        entityManager.flush();

        assertNotNull(accessControlDefault.getId());
    }

    @Test
    @Transactional
    public void storeAccessControlEntry()
    {
        AcmAccessControlEntry accessControlEntry = new AcmAccessControlEntry();
        accessControlEntry.setAccessDecision("GRANT");
        accessControlEntry.setAccessLevel(accessLevel);
        accessControlEntry.setAccessorType(accessorType);
        accessControlEntry.setAllowDiscretionaryUpdate(true);
        accessControlEntry.setCreator("creator");
        accessControlEntry.setModifier("modifier");
        accessControlEntry.setObjectState(objectState);
        accessControlEntry.setObjectType(objectType);
        accessControlEntry.setObjectId(objectId);
        accessControlEntry.setAccessorId(accessorId);
        accessControlEntry.setAccessDecisionReason("Default");

        entityManager.persist(accessControlEntry);

        entityManager.flush();

        assertNotNull(accessControlEntry.getId());
    }
}
