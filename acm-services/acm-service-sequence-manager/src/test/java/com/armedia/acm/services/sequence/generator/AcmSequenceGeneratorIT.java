package com.armedia.acm.services.sequence.generator;

/*-
 * #%L
 * ACM Service: Sequence Manager
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;
import com.armedia.acm.services.sequence.model.AcmSequenceEntity;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.service.AcmSequenceConfigurationService;
import com.armedia.acm.services.sequence.service.AcmSequenceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(name = "spring", locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-sequence-manager.xml",
})

public class AcmSequenceGeneratorIT {
    private static final int NUM_OF_THREADS = 100;
    private volatile Exception exception;

    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
        System.setProperty("application.profile.reversed", "runtime");
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Autowired
    private AcmSequenceGeneratorManager acmSequenceGeneratorManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private AcmSequenceConfigurationService acmSequenceConfigurationService;

    @Autowired
    private AcmSequenceService acmSequenceService;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws AcmSequenceException
    {
        auditAdapter.setUserId("auditUser");

        List<AcmSequenceConfiguration> acmSequenceConfigurations = acmSequenceConfigurationService.getSequenceConfiguration();
        Set<String> configurationNames = new HashSet<>();
        for (AcmSequenceConfiguration sequenceConfiguration : acmSequenceConfigurations)
        {
            configurationNames.add(sequenceConfiguration.getSequenceName());
        }
        if (!configurationNames.contains("acmTestSequence"))
        {
            List<AcmSequencePart> acmSequenceParts = new ArrayList<>();
            AcmSequenceConfiguration acmSequenceConfiguration = new AcmSequenceConfiguration();
            acmSequenceConfiguration.setSequenceName("acmTestSequence");
            acmSequenceConfiguration.setSequenceEnabled(true);
            acmSequenceConfiguration.setSequenceDescription("This is sequence configuration used for testing");
            acmSequenceConfiguration.setSequenceParts(acmSequenceParts);

            AcmSequencePart acmSequencePart1 = new AcmSequencePart();
            acmSequencePart1.setSequencePartType("DATE");
            acmSequencePart1.setSequencePartName("Date1");
            acmSequencePart1.setSequenceDateFormat("yyyyMMdd");

            AcmSequencePart acmSequencePart2 = new AcmSequencePart();
            acmSequencePart2.setSequencePartType("ARBITRARY_TEXT");
            acmSequencePart2.setSequencePartName("Text1");
            acmSequencePart2.setSequenceArbitraryText("_");

            AcmSequencePart acmSequencePart3 = new AcmSequencePart();
            acmSequencePart3.setSequencePartType("AUTOINCREMENT");
            acmSequencePart3.setSequencePartName("Autoincrement1");
            acmSequencePart3.setSequenceIncrementSize(1);
            acmSequencePart3.setSequenceNumberLength(6);
            acmSequencePart3.setSequenceStartNumber(0);
            acmSequencePart3.setSequenceFillBlanks(true);

            acmSequenceParts.add(acmSequencePart1);
            acmSequenceParts.add(acmSequencePart2);
            acmSequenceParts.add(acmSequencePart3);

            acmSequenceConfigurations.add(acmSequenceConfiguration);
        }
        acmSequenceConfigurationService.saveSequenceConfiguration(acmSequenceConfigurations);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            String queryTextDelete = "DELETE " +
                    "FROM AcmSequenceEntity sequenceEntity " +
                    "WHERE sequenceEntity.sequenceName = 'acmTestSequence'";

            TypedQuery<AcmSequenceEntity> queryDelete = entityManager.createQuery(queryTextDelete, AcmSequenceEntity.class);
            queryDelete.executeUpdate();
            return null;
        });
        transactionTemplate.execute(status -> {
            String queryTextDelete = "DELETE " +
                    "FROM AcmSequenceRegistryUsed sequenceEntity " +
                    "WHERE sequenceEntity.sequenceName = 'acmTestSequence'";

            TypedQuery<AcmSequenceEntity> queryDelete = entityManager.createQuery(queryTextDelete, AcmSequenceEntity.class);
            queryDelete.executeUpdate();
            return null;
        });

        AcmSequenceEntity sequenceEntity = new AcmSequenceEntity();
        sequenceEntity.setSequenceName("acmTestSequence");
        sequenceEntity.setSequencePartName("Autoincrement1");
        sequenceEntity.setSequencePartValue(NUM_OF_THREADS + 1L);
        acmSequenceService.saveSequenceEntity(sequenceEntity);

        transactionTemplate.execute(status -> {
            String queryTextRegistryDelete = "DELETE " +
                    "FROM AcmSequenceRegistry sequenceRegistry " +
                    "WHERE sequenceRegistry.sequenceName = 'acmTestSequence' " +
                    "AND sequenceRegistry.sequencePartName = 'Autoincrement1'";

            TypedQuery<AcmSequenceEntity> queryDeleteRegistry = entityManager.createQuery(queryTextRegistryDelete, AcmSequenceEntity.class);
            queryDeleteRegistry.executeUpdate();

            return null;
        });
    }

    @Test
    public void shouldGenerateNewUniqueSequence() throws InterruptedException
    {
        assertNotNull(entityManager);
        Set<String> uniqueSequences = new HashSet<>();

        Runnable runnable = () -> {
            try
            {
                log.info(">>> Thread in: {}", Thread.currentThread().getName());
                String sequence = acmSequenceGeneratorManager.generateValue(
                        "acmTestSequence",
                        new Object());
                log.info(">>> Thread out: {}, value: {}", Thread.currentThread().getName(), sequence);

                if (!uniqueSequences.contains(sequence))
                {
                    uniqueSequences.add(sequence);
                    System.out.println("<<<<<< This is a unique generated sequence  - " + sequence);
                }
                else
                {
                    fail("Duplicate sequence: " + sequence);
                }

            }
            catch (Exception e)
            {
                exception = e;
            }

        };

        runThreads(runnable);
        assertEquals(NUM_OF_THREADS, uniqueSequences.size());
    }

    @Test
    public void shouldUseUnusedSequenceFromRegistry() throws InterruptedException, AcmSequenceException
    {
        assertNotNull(entityManager);
        Set<String> uniqueSequences = new HashSet<>();

        AcmSequenceRegistry sequenceRegistry = new AcmSequenceRegistry();
        sequenceRegistry.setSequenceName("acmTestSequence");
        sequenceRegistry.setSequencePartName("Autoincrement1");

        for (int i = 0; i < NUM_OF_THREADS; i++)
        {
            String autoIncrementPartValue = "20210316_" + String.format("%0" + 6 + "d", i);
            sequenceRegistry.setSequenceValue(autoIncrementPartValue);
            sequenceRegistry.setSequencePartValue((long) i);
            acmSequenceService.saveSequenceRegistry(sequenceRegistry);
        }

        Runnable runnable = () -> {
            try
            {
                log.info(">>> Thread in: {}", Thread.currentThread().getName());
                String sequence = acmSequenceGeneratorManager.generateValue(
                        "acmTestSequence",
                        new Object());
                log.info(">>> Thread out: {}, value: {}", Thread.currentThread().getName(), sequence);

                if (!uniqueSequences.contains(sequence))
                {
                    uniqueSequences.add(sequence);
                    System.out.println("<<<<<< This is a unique sequence that was unused and stored in the sequence registry - " + sequence);
                }
                else
                {
                    fail("Duplicate sequence: " + sequence);
                }

            }
            catch (Exception e)
            {
                exception = e;
                log.error("Test failed", e);
            }

        };

        runThreads(runnable);
        assertEquals(NUM_OF_THREADS, uniqueSequences.size());
    }

    private void runThreads(Runnable runnable) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUM_OF_THREADS; i++) {
            threads.add(new Thread(runnable));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        if (exception != null) {
            log.error(exception);
            fail(exception.getMessage());
        }
    }
}

