package com.armedia.acm.data;

import com.armedia.acm.data.model.TestEntity;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.queries.DatabaseQueryMechanism;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
})
public class EncryptColumnEntityAdapterTest extends EasyMockSupport {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    EncryptColumnEntityAdapter adapter;

    @Before
    public void setUp() {
        //set hardcoded values not to depend how is set in database.properties in .acm folder
        adapter.setEncryptionEnabled(true);
        adapter.setEncryptionDBFunction("pgp_sym_encrypt");
        adapter.setEncryptionPassphrase("passphrase");
        adapter.setEncryptionProperties("compress-algo=1, cipher-algo=aes256");
    }

    @Test
    public void testAboutToInsert() throws Exception {
        assertNotNull(adapter);

        assertTrue(adapter.getEncryptionEnabled());
        assertEquals("compress-algo=1, cipher-algo=aes256", adapter.getEncryptionProperties());
        assertEquals("pgp_sym_encrypt", adapter.getEncryptionDBFunction());
        assertEquals("passphrase", adapter.getEncryptionPassphrase());

        //given
        DescriptorEvent event = createMock(DescriptorEvent.class);
        AbstractSession session = createMock(AbstractSession.class);
        Platform platform = createMock(PostgreSQLPlatform.class);
        TestEntity entity = new TestEntity();
        ClassDescriptor classDescriptor = createMock(ClassDescriptor.class);
        DescriptorQueryManager queryManager = createMock(DescriptorQueryManager.class);
        SQLCall insertCall = createMock(SQLCall.class);
        DatabaseQuery query = createMock(DatabaseQuery.class);
        DatabaseQueryMechanism queryMechanism = createMock(DatabaseQueryMechanism.class);
        AbstractRecord modifyRow = createMock(AbstractRecord.class);
        Set fields = new LinkedHashSet<>();
        Collections.addAll(fields, new DatabaseField("id", "table"), new DatabaseField("name", "table"), new DatabaseField("lastName", "table"), new DatabaseField("gender", "table"));

        //when
        EasyMock.expect(event.getSession()).andReturn(session);
        EasyMock.expect(session.getDatasourcePlatform()).andReturn(platform);
        EasyMock.expect(platform.isPostgreSQL()).andReturn(true);
        EasyMock.expect(event.getObject()).andReturn(entity).anyTimes();
        EasyMock.expect(event.getClassDescriptor()).andReturn(classDescriptor);
        EasyMock.expect(classDescriptor.getQueryManager()).andReturn(queryManager);
        EasyMock.expect(queryManager.getInsertCall()).andReturn(insertCall);
        EasyMock.expect(event.getQuery()).andReturn(query).anyTimes();
        EasyMock.expect(query.getSQLString()).andReturn("INSERT INTO table (id, name, lastName, gender) VALUES (?, ?, ?, ?)");
        EasyMock.expect(query.getQueryMechanism()).andReturn(queryMechanism);
        EasyMock.expect(queryMechanism.getModifyRow()).andReturn(modifyRow);
        EasyMock.expect(modifyRow.keySet()).andReturn(fields);

        String expectedSqlString = "INSERT INTO table (id, name, lastName, gender) VALUES (?, " +
                adapter.getEncryptionDBFunction() +
                "(?, '" +
                adapter.getEncryptionPassphrase() +
                "', '" +
                adapter.getEncryptionProperties() + "'), ?, " +
                adapter.getEncryptionDBFunction() +
                "(?, '" +
                adapter.getEncryptionPassphrase() +
                "', '" +
                adapter.getEncryptionProperties() + "')"
                + ")";
        insertCall.setSQLString(expectedSqlString);
        EasyMock.expectLastCall();
        log.info("Expected SQL: ", expectedSqlString);

        replayAll();

        //than
        adapter.aboutToInsert(event);

    }
}