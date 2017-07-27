package com.armedia.acm.calendar.service.integration.exchange;

import static com.armedia.acm.calendar.service.integration.exchange.ExchangeCalendarService.PROCESS_USER;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.service.outlook.dao.OutlookDao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import microsoft.exchange.webservices.data.core.ExchangeService;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 26, 2017
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CalendarEntityHandlerTest
{

    private static final String ENTITY_TYPE_FOR_QUERY = "CaseFile";

    @Mock
    private ExchangeService mockedService;

    @Mock
    private EntityManager mockedEm;

    @Mock
    private TypedQuery<AcmContainerEntity> mockedQuery;

    @Mock
    private AcmContainerEntity mockedEntity;

    @Mock
    private AuditPropertyEntityAdapter mockedAuditPropertyEntityAdapter;

    @Mock
    private OutlookDao mockedOutlookDao;

    @Mock
    private AcmContainerDao mockedContainerEntityDao;

    @InjectMocks
    private CalendarEntityHandler entityHandler;

    @Mock
    private ConfigurationFileAddedEvent mockedConfigurationEvent;

    @Mock
    private AcmContainerEntity mockedContainerEntity;

    @Mock
    private AcmContainer mockedContainer;

    private List<String> closedStates = Arrays.asList("CLOSED");

    @Before
    public void setUp()
    {
        entityHandler.setEntityType("CASE_FILE");
        entityHandler.setEntityTypeForQuery("CaseFile");
        File purgerSettings = new File(getClass().getClassLoader().getResource("calendarPurgersSettings.properties").getFile());
        when(mockedConfigurationEvent.getConfigFile()).thenReturn(purgerSettings);
        entityHandler.onApplicationEvent(mockedConfigurationEvent);
    }

    /**
     * Test method for
     * {@link com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler#purgeCalendars(microsoft.exchange.webservices.data.core.ExchangeService, com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions, java.lang.Integer)}.
     */
    @Test
    public void testPurgeCalendars_retainIndefinitely()
    {
        // when
        entityHandler.purgeCalendars(mockedService, PurgeOptions.RETAIN_INDEFINITELY, null);
        // then
        verifyZeroInteractions(mockedService, mockedEm, mockedAuditPropertyEntityAdapter, mockedOutlookDao, mockedContainerEntityDao);

    }

    /**
     * Test method for
     * {@link com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler#purgeCalendars(microsoft.exchange.webservices.data.core.ExchangeService, com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions, java.lang.Integer)}.
     */
    @Test
    public void testPurgeCalendars_closed()
    {
        // given
        when(mockedEm.createQuery(anyString(), eq(AcmContainerEntity.class))).thenReturn(mockedQuery);
        when(mockedQuery.getResultList()).thenReturn(Arrays.asList(mockedContainerEntity));
        when(mockedContainerEntity.getContainer()).thenReturn(mockedContainer);

        // when
        entityHandler.purgeCalendars(mockedService, PurgeOptions.CLOSED, null);

        // then
        verify(mockedEm).createQuery(
                String.format("SELECT obj FROM %s obj WHERE obj.status IN :statuses AND obj.container.calendarFolderId IS NOT NULL",
                        ENTITY_TYPE_FOR_QUERY),
                AcmContainerEntity.class);
        verify(mockedQuery).setParameter("statuses", closedStates);
        verify(mockedQuery).getResultList();
        verify(mockedAuditPropertyEntityAdapter).setUserId(PROCESS_USER);

    }

}
