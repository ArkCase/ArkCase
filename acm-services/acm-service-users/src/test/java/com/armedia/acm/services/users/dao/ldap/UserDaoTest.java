package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

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
    public void saveUser() throws Exception
    {
        AcmUser user = new AcmUser();
        user.setFirstName("first");
        user.setLastName("last");
        user.setFullName("full");
        user.setMail("mail");

        expect(mockEntityManager.find(eq(AcmUser.class), anyObject(AcmUser.class))).andReturn(new AcmUser());
        mockEntityManager.persist(anyObject(AcmUser.class));

        replayAll();

        AcmUser saved = unit.saveAcmUser(user);

        verifyAll();

        assertEquals(user.getFirstName(), saved.getFirstName());
        assertEquals(user.getLastName(), saved.getLastName());
        assertEquals(user.getMail(), saved.getMail());
        assertEquals(user.getFullName(), saved.getFullName());
    }

}
