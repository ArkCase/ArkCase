package com.armedia.acm.services.users.dao.ldap;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.services.users.model.AcmUser;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;

/**
 * Created by dmiller on 4/20/16.
 */
public class UserDaoTest extends EasyMockSupport
{
    private UserDao unit;

    private EntityManager mockEntityManager;

    @Before
    public void setUp() throws Exception
    {
        mockEntityManager = createMock(EntityManager.class);

        unit = new UserDao();

        unit.setEntityManager(mockEntityManager);
    }

    @Test
    public void saveAcmUser() throws Exception
    {
        AcmUser user = new AcmUser();
        user.setFirstName("first");
        user.setLastName("last");
        user.setFullName("full");
        user.setMail("mail");

        expect(mockEntityManager.find(eq(AcmUser.class), anyObject(AcmUser.class))).andReturn(new AcmUser());
        mockEntityManager.persist(anyObject(AcmUser.class));

        replayAll();

        AcmUser saved = unit.save(user);

        verifyAll();

        assertEquals(user.getFirstName(), saved.getFirstName());
        assertEquals(user.getLastName(), saved.getLastName());
        assertEquals(user.getMail(), saved.getMail());
        assertEquals(user.getFullName(), saved.getFullName());
    }

}
